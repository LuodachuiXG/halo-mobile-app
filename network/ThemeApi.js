import request from "./Request.js";

/**
 * 获取主题数据
 */
export function getThemes() {
	return request({
		url: "/api/admin/themes",
		method: "GET"
	});
}

/**
 * 根据主题 id 启用主题
 * @param {Object} id
 */
export function updateThemeActivation(id) {
	return request({
		url: "/api/admin/themes/" + id + "/activation",
		method: "POST"
	});
}

/**
 * 根据主题 id 删除主题
 * @param {Object} id
 */
export function deleteTheme(id) {
	return request({
		url: "/api/admin/themes/" + id + "?deleteSettings=true",
		method: "DELETE"
	});
}