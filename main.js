// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import uView from './uni_modules/uview-ui'
import {
	encrypt,
	decrypt
} from './utils/encryp.js';

Vue.use(uView);

Vue.config.productionTip = false

App.mpType = 'app'

const app = new Vue({
	...App
})
app.$mount()

export default app;
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


/**
 * 加密和解密方法
 */
Vue.prototype.encrypt = encrypt;
Vue.prototype.decrypt = decrypt;

/**
 * 获取博客 url
 */
Vue.prototype.getUrl = function() {
	return this.getData("url");
}

/**
 * 获取登录 accessToken
 */
Vue.prototype.getAccessToken = function() {
	return this.getData("access_token");
}

/**
 * toast 方法封装
 */
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
		uni.hideTabBar();
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
	var s = time.getSeconds();
	return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) + ':' + add0(s);
	// + ':' + add0(s);
}

/**
 * 打开网址
 * @param {Object} url
 */
Vue.prototype.openURL = function(url) {
	// #ifdef APP-PLUS
	plus.runtime.openWeb(url);
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

/**
 * 根据网络请求返回的信息判断 Token 是否过期
 * @param {Object} res
 */
Vue.prototype.isExpiredByRequest = function(res) {
	if (res.data.message === "未登录，请登录后访问" || 
		res.data.message === "Token 已过期或不存在" ||
		res.data.message === undefined) {
		// token已经过期，将本地登录状态更改
		this.setData("isLogin", "false")
		return true;
	}
	return false;
}

/**
 * 删除数组中指定元素并返回修改后的数组
 * @param {Object} val
 */
Vue.prototype.arrayRemove = function(array, value) {
	var index = array.indexOf(value);
	if (index > -1) {
		array.splice(index, 1);
	}
	return array;
};

/**
 * 将 hex 转为 rgb
 * @param {Object} hex
 * @param {Object} opacity
 */
Vue.prototype.hexToRgb = function(hex) {
    return {
        r: parseInt("0x" + hex.slice(1, 3)),
        g: parseInt("0x" + hex.slice(3, 5)),
        b: parseInt("0x" + hex.slice(5, 7)),
		a: 1
    }
};

/**
 * 获取版本号
 */
Vue.prototype.getVersion = function() {
	// #ifdef APP-PLUS
	return plus.runtime.version;
	// #endif
}


