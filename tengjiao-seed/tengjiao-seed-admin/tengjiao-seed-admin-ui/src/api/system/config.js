import request from '@/utils/request'

export default {
  page (params) {
    return request({
      url: `system/config/query/list/${params.current}/${params.size}`,
      method: 'post',
      data: {
        asc: params.asc,
        text: params.text,
        sort: params.sort
      }
    })
  },
  add (data) {
    return request({
      url: 'system/config/add',
      method: 'post',
      data
    })
  },
  delete (id) {
    return request({
      url: `system/config/del/${id}`,
      method: 'delete'
    })
  },
  mod (data) {
    return request({
      url: 'system/config/mod',
      method: 'put',
      data
    })
  },
  getById (id) {
    return request({
      url: `system/config/query/one/${id}`,
      method: 'get'
    })
  }
}
