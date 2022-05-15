import request from "./Request.js";

/**
 * 根据不同条件获取日志
 * @param {*} page 
 * @param {*} size 
 * @param {*} keyword 
 * @param {*} status 
 */
export function getJournals(page, size, keyword = "", type = "") {
	return request({
		url: "/api/admin/journals?page=" + page + "&size=" + size + 
		"&keyword=" + keyword + "&type=" + type,
		method: "GET"
	});
}

/**
 * 根据日志 id 来删除日志
 * @param {*} id 
 */
export function deleteJournal(id) {
	return request({
		url: "/api/admin/journals/" + id,
		method: "DELETE"
	});
}

/**
 * 创建新日志
 * @param {*} sourceContent 
 * @param {*} type 
 * @param {*} keepRaw 
 */
export function createJournal(sourceContent, type, keepRaw = "") {
	return request({
		url: "/api/admin/journals",
		method: "POST",
		data: {
			sourceContent: sourceContent,
			type: type
		}
	});
}

/**
 * 修改日志
 * @param {*} sourceContent 
 * @param {*} type 
 * @param {*} keepRaw 
 */
export function updateJournal(id, content, type) {
	return request({
		url: "/api/admin/journals/" + id,
		method: "PUT",
		data: {
			sourceContent: content,
			type: type
		}
	});
}

