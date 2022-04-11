import request from '@/utils/request'

export default {
  page (params) {
    return request({
      url: `system/menu/query/list/${params.current}/${params.size}`,
      method: 'post',
      data: {
        asc: params.asc,
        text: params.text,
        sort: params.sort
      }
    })
  },
  getTree (type) {
    return request({
      url: 'system/menu/query/tree',
      method: 'get',
      params: {
        type
      }
    })
  },
  add (data) {
    return request({
      url: 'system/menu/add',
      method: 'post',
      data
    })
  },
  delete (id) {
    return request({
      url: `system/menu/del/${id}`,
      method: 'delete'
    })
  },
  mod (data) {
    return request({
      url: 'system/menu/mod',
      method: 'put',
      data
    })
  },
  getById (id) {
    return request({
      url: `system/menu/query/one/${id}`,
      method: 'get'
    })
  }
}
