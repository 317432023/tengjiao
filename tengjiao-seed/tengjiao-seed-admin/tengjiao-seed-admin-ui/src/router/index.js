import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout'

// begin 去掉 vue-router-3.0.7 以上版本跳转时守卫报恼人烦的错误提示 ： Redirected when going from "/xxx" to "/yyy" via a navigation guard
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}
// end

Vue.use(VueRouter)

/* 静态路由 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        //component: () => import('@/views/redirect/index')
        component: (resolve) => require(["@/views/redirect/index"], resolve),
      }
    ]
  },
  {
    path: '/404',
    //component: () => import('@/views/error-page/404'),
    component: (resolve) => require(["@/views/error-page/404"], resolve),
    hidden: true
  },
  {
    path: '/401',
    //component: () => import('@/views/error-page/401'),
    component: (resolve) => require(["@/views/error-page/401"], resolve),
    hidden: true
  },
  {
    path: '/login',
    name: 'Login',
    //component: () => import('@/views/login'),
    component: (resolve) => require(["@/views/login"], resolve),
    meta: { title: '登录页' },
    hidden: true
  },
  /* 首页改为由后端给定 20220330 */
  /*{
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      //component: () => import('@/views/dashboard'),
      component: (resolve) => require(["@/views/dashboard"], resolve), meta: { title: '首页', icon: 'el-icon-odometer', affix: true }}]
  },*/
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [{
      path: 'index',
      //component: () => import('@/views/profile/index'),
      component: (resolve) => require(["@/views/profile/index"], resolve), name: 'Profile', meta: { title: '个人中心', icon: 'user', noCache: true }}
    ]
  }

/*
// 数据格式样板一
{
  path: '/admin', component: Layout,
  children: [
    {path: '', name: 'adminPage', component: () => import('@/views/admin'), meta: {title: '管理员管理'} }
  ]
},

// 数据格式样板二
{
  path: '/admin', component: Layout,
  redirect: '/admin/index',
  children: [
    { path: 'index', name: 'adminPage', component: () => import('@/views/admin/index'), meta: { title: '管理员管理' } }
  ]
} */

]

/* 动态路由 */
export const asyncRoutes = [
  // 用于测试的部分。最终动态路由必须由后端给定
  /*{
    path: '/role',
    component: Layout,
    name: 'Role',
    redirect: '/role/index',
    // meta: { role:['admin', 'editor'] }, // 页面需要角色
    children: [
      {
        path: 'index',
        //component: () => import('@/views/role/index'),
        component: (resolve) => require(["@/views/role/index"], resolve),
        name: '角色管理页'
        // meta: { role:['admin', 'editor'] }, // 页面需要角色
      }
    ]
  }*/
  {
    path: '/system',
    component: Layout,
    name: 'System',
    children: [
      {
        path: 'role',
        component: (resolve) => require(["@/views/system/role/index"], resolve),
        name: 'Role'
        // meta: { role:['admin', 'editor'] }, // 页面需要角色
      }
    ]
  }
]

export const notFoundRoute = { path: '*', redirect: '/404', hidden: true }

const createRouter = () => new VueRouter({
  mode: 'history',  //去掉url中的# // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter () {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
