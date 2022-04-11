import request from '../utils/request'

export default {
  listAllStation () {
    return request({
      url: 'common/listAllStation',
      method: 'post'
    })
  }
}
