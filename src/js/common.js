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

/**
 * 获取博客 Url
 * @returns {string}
 */
export function getUrl () {
  return getData('url')
}

/**
 * 设置博客 Url
 */
export function setUrl (url) {
  setData('url', url)
}

/**
 * 获取指定长度随机字符
 * @param {*} length 字符长度
 * @returns 随机字符
 */
function getRandomChat (length) {
  const str = 'abcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += str[Math.floor(Math.random() * 36)]
  }
  return result
}

/**
 * 生成 Token
 * @returns Token
 */
export function generateToken () {
  return (
    getRandomChat(8) +
    '-' +
    getRandomChat(4) +
    '-' +
    getRandomChat(4) +
    '-' +
    getRandomChat(4) +
    '-' +
    getRandomChat(12)
  )
}
