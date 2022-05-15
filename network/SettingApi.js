import request from "./Request.js";

/**
 * 根据 mapKeys 获取博客设置数据
 * @param {Object} map
 */
export function getOptionsByMapViewsKeys(map) {
	return request({
		url: "/api/admin/options/map_view/keys",
		method: "POST",
		data: map
	});
}

/**
 * 根据 map 保存博客设置数据
 * @param {Object} map
 */
export function updateOptionsByMapViews(map) {
	return request({
		url: "/api/admin/options/map_view/saving",
		method: "POST",
		data: map
	});
}