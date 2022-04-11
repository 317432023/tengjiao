import request from '@/utils/request'

export default {
  /* page (params) {
    return request({
      url: `system/role/query/list/${params.current}/${params.size}`,
      method: 'post',
      data: {
        asc: params.asc,
        text: params.text,
        sort: params.sort
      }
    })
  }, */
  page (params, data) {
    return request({
      url: 'system/role/query/list',
      method: 'post',
      params, // {current:1,size:6, stationId:xx}
      data
    })
  },
  add (data) {
    return request({
      url: 'system/role/add',
      method: 'post',
      data
    })
  },
  delete (id) {
    return request({
      url: `system/role/del/${id}`,
      method: 'delete'
    })
  },
  mod (data) {
    return request({
      url: 'system/role/mod',
      method: 'put',
      data
    })
  },
  getById (id) {
    return request({
      url: `system/role/query/one/${id}`,
      method: 'get'
    })
  },

  updateRoleMenus (data) {
    return request({
      url: 'system/role/mod/menu',
      method: 'put',
      data
    })
  },

  // begin for 更新角色菜单 需要的额外请求
  getMenuTree (type) {
    return request({
      url: 'system/role/query/menuTree',
      method: 'get',
      params: {
        type
      }
    })
  },

  getRoleMenus (roleId) {
    return request({
      url: 'system/role/query/menu',
      method: 'get',
      params: {
        roleId
      }
    })
  }

  // end for 更新角色菜单 需要的额外请求
}
