import request from "./Request.js";

/**
 * 获取链接数据
 */
export function getLinks() {
	return request({
		url: "/api/admin/links",
		method: "GET"
	});
}

/**
 * 根据链接 id 删除链接
 */
export function deleteLink(id) {
	return request({
		url: "/api/admin/links/" + id,
		method: "DELETE"
	});
}

/**
 * 添加链接
 */
export function createLink(link) {
	return request({
		url: "/api/admin/links",
		method: "POST",
		data: {
			name: link.name,
			url: link.url,
			logo: link.logo,
			team: link.team,
			priority: link.priority,
			description: link.description
		}
	});
}

/**
 * 修改链接
 */
export function updateLink(link) {
	return request({
		url: "/api/admin/links/" + link.id,
		method: "PUT",
		data: {
			name: link.name,
			url: link.url,
			logo: link.logo,
			team: link.team,
			priority: link.priority,
			description: link.description
		}
	});
}

