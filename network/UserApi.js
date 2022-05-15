import request from "./Request.js";

/**
 * 获取用户信息
 */
export function getUserProfiles() {
	return request({
		url: "/api/admin/users/profiles",
		method: "GET"
	});
}

/**
 * 修改用户数据
 * @param {Object} json
 */
export function updateUserProfiles(json) {
	return request({
		url: "/api/admin/users/profiles",
		method: "PUT",
		data: json
	});
}

/**
 * 获取用户统计数据
 */
export function getUserStatistics() {
	return request({
		url: "/api/admin/statistics/user",
		method: "GET"
	});
}

/**
 * 注销登录状态
 */
export function logout() {
	return request({
		url: "/api/admin/logout",
		method: "POST"
	});
}