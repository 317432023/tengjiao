import { captcha, login, logout, getInfo, loadMenus } from '@/api/security'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { resetRouter } from '@/router'
import conv2Tree from '@/utils/MenuUtils'

const state = {
  /* 令牌 */
  token: getToken(), // 外部通过 this.$store.state.security.token 访问
  captchaToken: '', // 验证码令牌，登录成功后即销毁
  username: '', // 用户名
  nickname: '', // 昵称
  avatar: '', // 头像
  roles: [], // 角色数组
  station: 0 // 站点ID
}
const getters = {

}
const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_CAPTCHA_TOKEN: (state, captchaToken) => {
    state.captchaToken = captchaToken
  },
  SET_USERNAME: (state, username) => {
    state.username = username
  },
  SET_NICKNAME: (state, nickname) => {
    state.nickname = nickname
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}
const actions = {
  // 登录 取图形验证码
  captcha ({ commit }, params) {
    // const { width, height, mode } = params
    return new Promise((resolve, reject) => {
      captcha(params).then(res => {
        if (res.success) {
          const { data } = res

          commit('SET_CAPTCHA_TOKEN', data.captchaToken) // 验证码令牌放进内存 state.captchaToken = data.captchaToken
        } else commit('SET_CAPTCHA_TOKEN', '')

        resolve(res)
      }).catch(error => {
        commit('SET_CAPTCHA_TOKEN', '')

        reject(error)
      })
    })
  },

  // 登录 动作
  login ({ commit }, userInfo) {
    const { username, password, captcha } = userInfo
    return new Promise((resolve, reject) => {
      login({}, { username: username.trim(), password: password, captcha: captcha }).then(res => {
        if (res.success) {
          const { data } = res
          if (process.env.VUE_APP_SEC_STRATEGY !== 'cookieStore') {
            commit('SET_TOKEN', data) // 令牌放进内存
            setToken(data) // 令牌放进本地 sessionStorage 或 localStorage

            // 移除使用过的captchaToken
            commit('SET_CAPTCHA_TOKEN', '')
          }
        }
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 拉取用户信息 动作
  getInfo ({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const { data } = response

        if (!data) {
          reject(new Error('Verification failed, please Login again.'))
        }

        const { roles, username, nickname, avatar } = data

        // roles must be a non-empty array
        //if (!roles || roles.length === 0) {
        //  reject(new Error('getInfo: roles must be a non-null array!'))
        //}

        commit('SET_ROLES', roles)
        commit('SET_USERNAME', username)
        commit('SET_NICKNAME', nickname)
        commit('SET_AVATAR', avatar)

        console.log('拉取用户信息:' + JSON.stringify(data))

        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 拉取菜单 动作
  loadMenus ({ commit, state }, defaultOpens) {
    return new Promise((resolve, reject) => {
      const roles = state.roles
      // roles must be a non-empty array
      if (!roles || roles.length <= 0) {
        reject(new Error('loadMenus: roles must be a non-null array!'))
      }

      loadMenus(roles).then(response => {
        const { data } = response

        console.log('取得平菜单列表：' + JSON.stringify(data))

        const menuList = conv2Tree(data)

        console.log('转换成菜单树：' + JSON.stringify(menuList))

        // 设置打开的ID数组
        if (defaultOpens) {
          for (const item of data) {
            if (item.open) {
              // :defaultOpeneds=['...'] 属性内容和 <el-submenu index="..."> 里面的index内容是关联的，两个属性内容一样才可以关联
              defaultOpens.push(item.id + '')
            }
          }
        }

        resolve(menuList)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 退出 动作
  logout ({ commit, state, dispatch }) {
    return new Promise((resolve, reject) => {
      logout().then(() => {
        if (process.env.VUE_APP_SEC_STRATEGY !== 'cookieStore') {
          commit('SET_TOKEN', '')
        }
        commit('SET_ROLES', [])
        removeToken()
        resetRouter()

        // reset visited views and cached views
        // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
        // dispatch('tagsView/delAllViews', null, { root: true })

        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken ({ commit }) {
    return new Promise(resolve => {
      if (process.env.VUE_APP_SEC_STRATEGY !== 'cookieStore') {
        commit('SET_TOKEN', '')
      }
      commit('SET_ROLES', [])
      removeToken()
      resolve()
    })
  },

  // dynamically modify permissions
  changeRoles ({ commit, dispatch }, role) {
    return new Promise(async resolve => {
      const token = role + '-token'

      commit('SET_TOKEN', token)
      setToken(token)

      const { roles } = await dispatch('getInfo')

      resetRouter()

      // generate accessible routes map based on roles
      const accessRoutes = await dispatch('permission/generateRoutes', roles/*, { root: true } */)

      // dynamically add accessible routes
      router.addRoutes(accessRoutes)

      // reset visited views and cached views
      dispatch('tagsView/delAllViews', null, { root: true })

      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
