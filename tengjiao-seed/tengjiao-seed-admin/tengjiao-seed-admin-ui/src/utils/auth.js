import Cookies from 'js-cookie'
const storeTokenKey = process.env.VUE_APP_STORE_TOKEN_KEY
const securityStrategy = process.env.VUE_APP_SEC_STRATEGY

/**
 * 令牌的存取、移除操作
 */
const tokenOperation = {
  /**
   * 令牌在Cookie中的操作
   */
  cookieStore: {
    getToken: () => Cookies.get(storeTokenKey),
    setToken: (token) => Cookies.set(storeTokenKey, token),
    removeToken: () => Cookies.remove(storeTokenKey)
  },
  /**
   * 令牌在sessionStorage中的操作
   */
  sessionStore: {
    getToken: () => sessionStorage.getItem(storeTokenKey),
    setToken: (token) => sessionStorage.setItem(storeTokenKey, token),
    removeToken: () => sessionStorage.removeItem(storeTokenKey)
  },
  /**
   * 令牌在localStorage中的操作
   */
  localStore: {
    getToken: () => localStorage.getItem(storeTokenKey),
    setToken: (token) => localStorage.setItem(storeTokenKey, token),
    removeToken: () => localStorage.removeItem(storeTokenKey)
  }
}

export function getToken () { return tokenOperation[securityStrategy || 'cookieStore'].getToken() }
export function setToken (token) { return tokenOperation[securityStrategy || 'cookieStore'].setToken(token) }
export function removeToken () { return tokenOperation[securityStrategy || 'cookieStore'].removeToken() }
