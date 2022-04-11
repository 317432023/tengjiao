import request from '@/utils/request.js'
/**
 * 例子
 */
/*
export findUserList = (current,size,userVO) => {
  return request({
    url: 'user/findUserPage',
    method: 'post',
    params: {
      current,// 服务端用 @RequestParam 接收
      size
    },
    data: userVO // 服务端用 @RequestBody 接收
  })
}
*/

/**
 * 验证码
 * @param params
 */
export function captcha (params, date) {
  return request({
    url: 'captcha?' + (date || Date.now()),
    method: 'get',
    params /* { width:200, height:80, mode:2 } */
  })
}

/**
 * 登录
 * @param {Object} params
 * @param {Object} body
 */
export function login (params, body) {
  return request({
    url: 'login',
    method: 'post',
    params,
    data: body
  })
}

/**
 * 拉取用户信息（角色|权限）
 */
export function getInfo () {
  return request({
    url: 'getInfo',
    method: 'get'
  })
}

/**
 * 退出
 */
export function logout () {
  return request({
    url: 'logout',
    method: 'get'
  })
}

/**
 * 加载远程路由
 * @param roles 角色ID数组（可选，当不传角色时后台自动使用登录者的ID去加载路由）
 */
export function loadRoutes (roles) {
  return request({
    url: 'loadRoutes',
    method: 'post',
    params: { roleIds: roles }
  })
}

/**
 * 加载远程菜单
 * @param roles 角色ID数组
 */
export function loadMenus (roles) {
  return request({
    url: 'loadMenus',
    method: 'post',
    params: { roleIds: roles }
  })
}
