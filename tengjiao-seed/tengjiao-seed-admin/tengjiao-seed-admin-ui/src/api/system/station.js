import request from '@/utils/request'

export default {
  page (params) {
    return request({
      url: `system/station/query/list/${params.current}/${params.size}`,
      method: 'post',
      data: {
        asc: params.asc,
        text: params.text,
        sort: params.sort
      }
    })
  },
  /* page (params, data) {
    return request({
      url: 'station/query/list',
      method: 'post',
      params, // {current:1,size:6, name:xx}
      data
    })
  },
  */
  add (data) {
    return request({
      url: 'system/station/add',
      method: 'post',
      data
    })
  },
  delete (id) {
    return request({
      url: `system/station/del/${id}`,
      method: 'delete'
    })
  },
  mod (data) {
    return request({
      url: 'system/station/mod',
      method: 'put',
      data
    })
  },
  getById (id) {
    return request({
      url: `system/station/query/one/${id}`,
      method: 'get'
    })
  }
}
