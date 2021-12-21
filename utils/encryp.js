/**
 * 工具类
 */
import Vue from 'vue'
import CryptoJS from './crypto-js-4.1.1/crypto-js.js'
let keyStr = "JXU5NkM2JXU1NkUyJXU9RkQSJXU4NDI1JXU2NTcwKXU1QjU3JXU1MzE2JXU1MjA2JXU2NzkwJXU1RTczJXU1M0Yw"
 
//加密
// export function encrypt(word, keyStr){ 
export function encrypt(word){ 
	keyStr = keyStr ? keyStr : 'abcdefgabcdefg12';
	var key  = CryptoJS.enc.Utf8.parse(keyStr);//Latin1 w8m31+Yy/Nw6thPsMpO5fg==
	var srcs = CryptoJS.enc.Utf8.parse(word);
	var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
	return encrypted.toString();
}
 
//解密
// export function decrypt(word, keyStr){  
export function decrypt(word){  
	keyStr = keyStr ? keyStr : 'abcdefgabcdefg12';
	var key  = CryptoJS.enc.Utf8.parse(keyStr);//Latin1 w8m31+Yy/Nw6thPsMpO5fg==
	var decrypt = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
	return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}