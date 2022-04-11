import request from '@/utils/request'

export default {
  /* page (params) {
    return request({
      url: `system/admin/query/list/${params.current}/${params.size}`,
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
      url: 'system/admin/query/list',
      method: 'post',
      params, // {current:1,size:6}
      data
    })
  },
  add (data) {
    return request({
      url: 'system/admin/add',
      method: 'post',
      data
    })
  },
  delete (id) {
    return request({
      url: `system/admin/del/${id}`,
      method: 'delete'
    })
  },
  mod (data) {
    return request({
      url: 'system/admin/mod',
      method: 'put',
      data
    })
  },
  getById (id) {
    return request({
      url: `system/admin/query/one/${id}`,
      method: 'get'
    })
  }/*,
  updatePwd (data) {
    return request({
      url: 'system/admin/updatePwd',
      method: 'put',
      data
    })
  },
  updateInfo (data) {
    return request({
      url: 'system/admin/updateInfo',
      method: 'put',
      data
    })
  } */
}
