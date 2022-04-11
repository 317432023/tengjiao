import RsaEncrypt from 'jsencrypt'

// RSA 公钥加密 私钥解密

/* const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDR7axExkIfRNmXhb9g6ktyDiRi\n' +
    '9+cvHup8yZ65dpZFZGOZ4mU6U/0Rq4A7+bQRyW27GLtJcO72wZqSEtvg+4Shoaap\n' +
    '5l3HfdR2Go7G778W57l9YvZmwnJXHtgM8qS31m4uIUiIhVyoHcujl/fiF/myeVAZ\n' +
    'C5MmFRxnVn21ry7rSwIDAQAB' */

export default function (text, publicKey) {
  if (process.env.VUE_APP_PASSWORD_ENCRYPT === 0) {
    return text
  }
  // 加密
  const encryptor = new RsaEncrypt()
  encryptor.setPublicKey(publicKey || process.env.VUE_APP_SECRET) // 设置公钥
  return encryptor.encrypt(text) // 对需要加密的数据进行加密
}
