import request from "./Request.js";

/**
 * 根据页数和每页显示数量获取附件
 * @param {Object} page
 * @param {Object} size
 */
export function getAttachments(page, size) {
	return request({
		url: "/api/admin/attachments",
		method: "GET",
		data: {
			page: page,
			size: size
		}
	});
}