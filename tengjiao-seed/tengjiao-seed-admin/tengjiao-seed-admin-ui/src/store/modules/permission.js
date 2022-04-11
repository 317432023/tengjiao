import { constantRoutes, notFoundRoute } from '@/router'
import { loadRoutes } from '@/api/security'
import { buildRoutes } from '@/utils/routeFormat'
/**
 * 使用 meta.roles 判定当前用户是否有权限
 * @param roles 角色数组
 * @param route 路由
 */
function hasPermission (roles, route) {
  if (route.meta && route.meta.roles) {
    // 检测角色数组中是否有 角色存在 路由元数据meta.roles中
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * 递归过滤路由表
 * @param routes asyncRoutes 路由数组
 * @param roles 角色数组
 */
export function filterAsyncRoutes (routes, roles) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = { /* 注意此处存储是为了作为菜单生成时方便访问 */
  routes: [], // 总路由表
  addRoutes: [] // 远程路由表
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  /**
   * 基于角色生成 可访问的 (本地/远程)动态路由表
   */
  generateRoutes ({ commit }, roles) {
    return new Promise(async resolve => {
      let accessedRoutes

      /*
      // 使用本地动态路由
      if (roles.includes('admin')) {
        accessedRoutes = asyncRoutes || []
      } else {
        accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      } */
      // 加载远程路由
      const [res, e] = await loadRoutes(roles[0]?roles:undefined).then(res => [res]).catch(e => [null, e])

      if (res && res.success) {
        console.log('动态路由列表：\n' + JSON.stringify(res.data))

        accessedRoutes = buildRoutes(res.data)
      } else {
        console.error(e)
        accessedRoutes = []
      }



      // 添加404路由兜底
      accessedRoutes.push(notFoundRoute)

      // 路由设置到本地存储空间中
      commit('SET_ROUTES', accessedRoutes)

      // 返回动态路由让Vue路由对象 动态添加到 Router 对象中
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
