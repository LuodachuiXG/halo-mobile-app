import request from "./Request.js";

/**
 * 新增页面
 * @param {Object} page
 */
export function addPage(page) {
	return request({
		url: "/api/admin/sheets",
		method: "POST",
		data: page
	});
}

/**
 * 根据页数和每页显示数量获取页面
 * @param {Object} page
 * @param {Object} size
 */
export function getPages(page, size) {
	return request({
		url: "/api/admin/sheets",
		method: "GET",
		data: {
			page: page,
			size: size
		}
	});
}

/**
 * 根据页面 id 获取单个页面数据
 * @param {Object} id
 */
export function getPage(id) {
	return request({
		url: "/api/admin/sheets/" + id,
		method: "GET"
	});
}

/**
 * 更新页面设置
 * @param {Object} id
 * @param {Object} page
 */
export function updatePage(id, page) {
	return request({
		url: "/api/admin/sheets/" + id,
		method: "PUT",
		data: page
	});
}

/**
 * 根据页面 id 设置页面状态
 * @param {Object} id
 * @param {Object} status
 */
export function updatePageStatus(id, status) {
	return request({
		url: "/api/admin/sheets/" + id + "/" + status,
		method: "PUT"
	});
}

/**
 * 根据页面 id 删除页面
 * @param {Object} id
 */
export function deletePage(id, status) {
	return request({
		url: "/api/admin/sheets/" + id,
		method: "DELETE"
	});
}