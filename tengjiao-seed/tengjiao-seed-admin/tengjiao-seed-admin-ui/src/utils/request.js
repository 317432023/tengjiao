import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import qs from 'qs'

const securityStrategy = process.env.VUE_APP_SEC_STRATEGY
const sendTokenKey = process.env.VUE_APP_SEND_TOKEN_KEY
const sendTokenValuePrefix = process.env.VUE_APP_SEND_TOKEN_VAL_PREFIX
const timeout = process.env.VUE_APP_REQ_TIMEOUT

const config = {
  baseURL: process.env.VUE_APP_BASE_API, // 最终请求路径为 baseURL + request_uri
  withCredentials: securityStrategy === 'cookieStore', // 当跨域时是否发送cookies，如果使用sessionStorage和localStorage 令牌可以不使用cookie
  timeout: timeout, // 请求超时时间（单位:毫秒）
  headers: { 'X-Request-Width': 'XMLHttpRequest' }
}

// 使用以上配置创建一个axios实例
const instance = axios.create(config)

// 设置post请求头UTF-8编码
instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'

// 将参数对象 序列化成URL的形式，以&进行拼接
instance.defaults.paramsSerializer = function (params) {
  // 选项参数表示发送数组名称不带[]
  return qs.stringify(params, { arrayFormat: 'repeat', allowDots: true })
}

// 请求拦截器设置
instance.interceptors.request.use(config => {
  // 若内存中存在令牌则在请求头中附加 sessionStorage或localStorage取出的令牌 进行发送
  const token = store.state.security.token
  securityStrategy !== 'cookieStore' && token && (config.headers[sendTokenKey] = sendTokenValuePrefix + getToken())

  // 若内存中存在验证码令牌则在请求头中附加
  const captchaToken = store.state.security.captchaToken
  captchaToken && (config.headers.captchaToken = captchaToken)

  return config
}, error => {
  console.log(error)
  return Promise.reject(error)
})

const handleFailure = function(code, message) {
  // 异地登录||令牌过期、未登录
  if (code === 2009 || code === 401) {
    MessageBox.confirm('异地登录或会话过期，请重新登录', '确认退出', {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      store.dispatch('security/resetToken').then(() => {
        location.reload()
      })
    })
  } else {

    Message({ message: message || 'Error', type: 'error', duration: 5 * 1000 })
  }

}

// 响应拦截器设置
instance.interceptors.response.use(response => {
  const res = response.data
  if (!res.success) {
    handleFailure(res.code ,res.message)
    return Promise.reject(new Error(message || 'Error'))
  } else {
    return res
  }
}, error => {
  console.log('err: ' +error) // for debug
  const code = error.response.status, message = error.message
  handleFailure(code, message)
  return Promise.reject(error)
})

export default instance
