import router, { resetRouter } from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // 进度条参数配置

const whiteList = ['/login'] // 白名单

/**
 * 钩子函数：导航守卫(前置处理)
 */
router.beforeEach(async (to, from, next) => {
  // 开启进度条
  NProgress.start()

  document.title = getPageTitle(to.meta.title)

  // 取得令牌
  const token = getToken()

  if (token) { // 已存在令牌的情况：如果是访问登陆页直接转到首页；其他情况需要判断用户是否已经拉取用户信息，已经拉取得到角色信息直接放行，否则访问服务端去拉取
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const hasRoles = store.state.security.roles && store.state.security.roles.length > 0

      if (hasRoles) {
        next() // 放行
      } else {
        try {
          console.log('拉取用户信息 开始')
          // 拉取用户信息
          // 角色信息roles 必须是一个数组对象，比如: ['admin'] 或 ['developer','editor']

          const { roles } = await store.dispatch('security/getInfo')

          console.log('取得角色列表：\n' + JSON.stringify(store.state.security.roles)) // 这时候 store.state.security.roles 有值了

          // 基于角色信息生成可访问的动态路由表
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)

          // 重置、动态添加路由到Vue路由对象中
          resetRouter()
          router.addRoutes(accessRoutes)

          // 为保证addRoutes方法的完全起作用

          // 设置replace为true用以保证导航不留下任何历史记录
          next({ ...to, replace: true })
        } catch (error) {
          // 拉取用户信息异常则移除令牌并转向登录页
          await store.dispatch('security/resetToken')
          Message.error(error || 'Has Error')

          next(`/login?redirect=${to.path}`) // 重定向

          NProgress.done()
        }
      }
    }
  } else { // 令牌不存在的情况：判断是否访问白名单，是的话直接放行；其他情况重定向到登录页并带上目标页路由参数
    if (whiteList.indexOf(to.path) !== -1) {
      next() // 放行
    } else {
      next(`/login?redirect=${to.path}`) // 重定向
      NProgress.done()
    }
  }
})

/**
 * 钩子函数：导航守卫(后置处理)
 */
router.afterEach(() => {
  NProgress.done()
})
