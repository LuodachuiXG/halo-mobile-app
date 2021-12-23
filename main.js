// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import {
	encrypt,
	decrypt
} from './utils/encryp.js';


Vue.config.productionTip = false

App.mpType = 'app'

const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
import App from './App.vue'
export function createApp() {
	const app = createSSRApp(App)
	return {
		app
	}
}
// #endif


Vue.prototype.encrypt = encrypt
Vue.prototype.decrypt = decrypt

Vue.prototype.toast = function(mTitle, mIcon = "none", mPosition = "bottom") {
	uni.showToast({
		title: mTitle,
		icon: mIcon,
		position: mPosition
	})
}

/**
 * 保存数据前先进行加密
 * @param {Object} key
 * @param {Object} data
 */
Vue.prototype.setData = function(key, data) {
	uni.setStorageSync(key, this.encrypt(data))
}

/**
 * 获取数据前先解密
 * @param {Object} key
 */
Vue.prototype.getData = function(key) {
	return this.decrypt(uni.getStorageSync(key))
}

/**
 * 判断用户是否登录以及登录信息是否过期
 * 未登录或登录过期都返回true
 */
Vue.prototype.isExpired = function() {
	// 判断是否登录
	let isLogin = this.getData("isLogin") === null ? "" : this.getData("isLogin")
	if (isLogin === "" || isLogin === "false") {
		// 用户未登录
		return true
	}

	let expired_date = this.getData("expired_date")
	let now = Number(Date.parse(new Date()))
	let expiredDate = Number(expired_date)
	if (now < expiredDate) {
		// token没有过期
		return false
	} else {
		// token已经过期，将本地登录状态更改
		this.setData("isLogin", "false")
		return true
	}
}

function add0(m) {
	return m < 10 ? '0' + m : m
}

/**
 * 时间戳格式化
 * @param {Object} time
 */
Vue.prototype.format = function(time) {
	//time是整数，否则要parseInt转换
	var time = new Date(time);
	var y = time.getFullYear();
	var m = time.getMonth() + 1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	// var s = time.getSeconds();
	return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) ;
	// + ':' + add0(s);
}

Vue.prototype.openURL = function(url) {
	// #ifdef APP-PLUS
	plus.runtime.openURL(url);
	// #endif
	// #ifdef H5
	window.open(url)
	// #endif
	// #ifdef MP
	uni.setClipboardData({
		data: url
	});
	// #endif
}
