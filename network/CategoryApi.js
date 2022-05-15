import request from "./Request.js";

/**
 * 获取分类目录数据
 */
export function getCategories(more = "true") {
	return request({
		url: "/api/admin/categories?more=" + more,
		method: "GET"
	});
}

/**
 * 根据分类目录的 id 来删除分类目录
 */
export function deleteCategory(id) {
	return request({
		url: "/api/admin/categories/" + id,
		method: "DELETE"
	});
}

/**
 * 添加分类目录
 */
export function addCategory(json) {
	return request({
		url: "/api/admin/categories",
		method: "POST",
		data: json
	});
}

/**
 * 修改分类目录
 */
export function updateCategory(id, json) {
	return request({
		url: "/api/admin/categories/" + id,
		method: "PUT",
		data: json
	});
}