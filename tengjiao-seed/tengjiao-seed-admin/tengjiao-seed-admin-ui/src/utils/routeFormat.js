
export default function (routers) {
  return filterAsyncRouter(routers)
}

// 遍历后台传来的路由字符串，转换为组件对象
export const filterAsyncRouter = (asyncRouters) => {
  return asyncRouters.filter(route => {
    route.component = loadComponent(route.component)

    if (route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children)
    }
    return true
  })
}

/**
 * require('../layout').default 相当于 Layout ，必须在头部导入 import Layout from '@/layout'
 * @param suf
 * @returns {function(): (Promise<*>|*)}
 */
export const loadComponent = (suf, debug = false) => {
  /*
  if (!debug) return suf ? () => import('@/views' + suf) : require('../layout').default
  return suf ? '@/views' + suf : '../layout'
  */
  if (!debug) return suf ? (resolve) => require([`@/views${suf}`], resolve) : require('../layout').default
  return suf ? '@/views' + suf : '../layout'
}

function pickLastPath (url) {
  const index = url.lastIndexOf('/')
  return index !== -1 ? url.substring(index + 1) : url
}

function isLeaf (raw) {
  return raw.type === 0
}

function isSonOfRoot (raw) {
  return raw.pid === 0
}

function isDashboard (tmp) {
  return tmp.name === 'Dashboard'
}

function buildMeta (tmp, props = ['icon', 'title', 'breadcrumb', 'affix', 'noCache', 'activeMenu', 'perm']) {
  const meta = {}
  props.forEach((value, index, array) => {
    meta[value] = tmp[value]
    delete tmp[value]
  })
  return meta
}

/**
 * 格式化路由列表
 * @param rawRoutes 后端返回的路由列表
 */
function formatRoutes (rawRoutes) {
  const fmtRoutes = []

  rawRoutes.forEach(raw => {
    const tmp = { ...raw }

    //if (isDashboard(tmp)) return true // 相当于continue 遇到主页continue

    if (isLeaf(raw)) { // 菜单
      if (isSonOfRoot(raw)) { // 一级菜单
        tmp.path = isDashboard(tmp) ? '/' : raw.path
        tmp.component = loadComponent()
        tmp.redirect = isDashboard(tmp) ? raw.path : (raw.path + '/index')
        console.log(tmp.redirect )
        tmp.children = [{ path: pickLastPath(tmp.redirect), component: loadComponent(tmp.redirect), name: tmp.name, meta: buildMeta(tmp) }]
        delete tmp.name // 名称已经给了 children 所以删除
      } else { // 二级以上的菜单
        tmp.path = pickLastPath(raw.path)
        console.log(raw.path + '/index' )
        tmp.component = loadComponent(raw.path + '/index')
        tmp.meta = buildMeta(tmp)
      }
    } else { // 目录
      if (isSonOfRoot(raw)) { // 一级目录
        tmp.component = loadComponent()
      } else { // 二级以上的目录
        // 没有component 但是有 path
        tmp.path = pickLastPath(raw.path)
        // ...
      }
      tmp.meta = buildMeta(tmp)
      tmp.children = [] // 预置长度为0的children
    }

    fmtRoutes.push(tmp)
  })

  return fmtRoutes
}

/**
 * 路由树（递归）
 */
function _buildTree (children, fmtRoutes, pid) {
  for (let i = 0, len = fmtRoutes.length; i < len; i++) {
    if (fmtRoutes[i].pid === pid) {
      children.push(fmtRoutes[i])
      if (fmtRoutes[i].type === 0) { // 菜单
        continue
      }
      // 目录才有必要做递归
      _buildTree(fmtRoutes[i].children, fmtRoutes, fmtRoutes[i].id)
    }
  }
}

/**
 * 路由树
 * @param rawRoutes 后端返回的路由列表
 */
export const buildRoutes = (rawRoutes) => {
  const fmtRoutes = formatRoutes(rawRoutes)
  console.log('路由列表(格式化)：' + JSON.stringify(fmtRoutes))
  const routes = []
  _buildTree(routes, fmtRoutes, 0)
  console.log('路由树：' + JSON.stringify(routes))
  return routes
}
