import Vue from "../main.js"
function service(options = {}) {
	options.url = Vue.getUrl() + options.url;
	options.header = {
		"Content-Type": "application/json",
		"ADMIN-Authorization": Vue.getAccessToken()
	};
	options.dataType = "json";
	return new Promise((resolved, rejected) => {
		options.success = (res) => {
			if (res.statusCode === 200) {
				// 如果 allData 设为 true，则返回网络请求的所有数据
				if (options.allData != undefined && options.allData) {
					resolved(res);
				} else {
					resolved(res.data.data);
				}
				
			} else if (Vue.isExpiredByRequest(res)){
				rejected("登录已过期");
			} else {
				rejected(res.data.message);
			}
		}
		options.fail = (err) => {
			rejected(err.errMsg);
		}
		uni.request(options);
	});
}
export default service;
