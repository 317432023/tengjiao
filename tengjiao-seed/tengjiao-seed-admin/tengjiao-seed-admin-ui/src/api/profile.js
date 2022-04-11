import request from '@/utils/request'

export default {

  /**
   * 修改密码
   * @param params
   */
  modifyPassword (params) {
    return request({
      url: 'profile/modifyPassword',
      method: 'put',
      params
    })
  }

}
