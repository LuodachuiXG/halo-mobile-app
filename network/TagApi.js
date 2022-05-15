import request from "./Request.js";

/**
 * 获取标签数据
 */
export function getTags(more = "true") {
	return request({
		url: "/api/admin/tags?more=" + more,
		method: "GET"
	});
}

/**
 * 添加标签
 */
export function addTag(json) {
	return request({
		url: "/api/admin/tags",
		method: "POST",
		data: json
	});
}

/**
 * 更新标签
 */
export function updateTag(id, json) {
	return request({
		url: "/api/admin/tags/" + id,
		method: "PUT",
		data: json
	});
}


/**
 * 删除标签
 */
export function deleteTag(id) {
	return request({
		url: "/api/admin/tags/" + id,
		method: "DELETE"
	});
}