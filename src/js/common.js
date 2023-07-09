import { encrypt, decrypt } from './encryp.js'
import { LocalStorage } from 'quasar'

/**
 * 加密数据并保存到本地
 * @param key
 * @param value
 */
export function setData (key, value) {
  LocalStorage.set(key, encrypt(value))
}

/**
 * 从本地取出数据并解密
 * @param key
 * @returns { string }
 */
export function getData (key) {
  return decrypt(LocalStorage.getItem(key))
}

/**
 * 获取 AccessToken
 * @returns {string}
 */
export function getAccessToken () {
  return getData('access_token')
}

/**
 * 设置 AccessToken
 */
export function setAccessToken (token) {
  setData('access_token', token)
}
