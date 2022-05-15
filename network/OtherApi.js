import request from "./Request.js";

/**
 * 发送测试邮件
 * @param {Object} map
 */
export function sendMailTest(to, subject, content) {
	return request({
		url: "/api/admin/mails/test",
		method: "POST",
		data: {
			to: to,
			subject: subject,
			content: content
		}
	});
}


/**
 * 加载博客统计信息
 */
export function getBlogStatistics() {
	return request({
		url: "/api/admin/statistics",
		method: "GET"
	});
}