import CryptoJS from 'crypto-js/crypto-js'
let keyStr = 'JXU5NkM2JXU1NkUyJXU9RkQSJXU4NDI1JXU2NTcwKXU1QjU3JXU1MzE2JXU1MjA2JXU2NzkwJXU1RTczJXU1M0Yw'

/**
 * 加密
 * @param word
 * @returns {string}
 */
export function encrypt (word) {
  keyStr = keyStr || 'abcdefgabcdefg1234567890'
  const key = CryptoJS.enc.Utf8.parse(keyStr)
  const src = CryptoJS.enc.Utf8.parse(word)
  const encrypted = CryptoJS.AES.encrypt(src, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 })
  return encrypted.toString()
}

/**
 * 解密
 * @param word
 * @returns {*}
 */
export function decrypt (word) {
  keyStr = keyStr || 'abcdefgabcdefg12'
  const key = CryptoJS.enc.Utf8.parse(keyStr)
  const decrypt = CryptoJS.AES.decrypt(word, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 })
  return CryptoJS.enc.Utf8.stringify(decrypt).toString()
}
