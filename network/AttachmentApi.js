import request from "./Request.js";

/**
 * 根据条件获取附件
 * @param {Object} page
 * @param {Object} size
 * @param {Object} attachmentType
 * @param {Object} mediaType
 * @param {Object} keyword
 */
export function getAttachments(page, size, attachmentType = "", mediaType = "", keyword = "") {
	let mt = (mediaType === "" ? "" : "&mediaType=" + mediaType);
	return request({
		url: "/api/admin/attachments?page=" + page + "&size=" + size + 
		"&attachmentType=" + attachmentType + "&keyword=" + keyword + mt,
		method: "GET"
	});
}

/**
 * 获取所有附件存储位置
 */
export function getAttachementTypes() {
	return request({
		url: "/api/admin/attachments/types",
		method: "GET"
	});
}

/**
 * 获取所有附件类型
 */
export function getAttachementMediaTypes() {
	return request({
		url: "/api/admin/attachments/media_types",
		method: "GET"
	});
}

/**
 * 根据附件 id 删除附件
 * @param {Object} array
 */
export function deleteAttachmentById(id) {
	return request({
		url: "/api/admin/attachments/" + id,
		method: "DELETE"
	});
}

/**
 * 根据附件 id 数组删除附件
 * @param {Object} array
 */
export function deleteAttachmentByIds(array) {
	return request({
		url: "/api/admin/attachments",
		method: "DELETE",
		data: array
	});
}

/**
 * 根据附件 id 修改附件名
 * @param {Object} array
 */
export function updateAttachmentName(id, name) {
	return request({
		url: "/api/admin/attachments/" + id,
		method: "PUT",
		data: {
			name: name
		}
	});
}