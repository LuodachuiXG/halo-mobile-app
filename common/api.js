import request from "./request.js";

/**
 * 加载最近文章
 * @param {Object} top 加载的文章数量
 */
export function getLatestPosts(top) {
	return request({
		url: "/api/admin/posts/latest",
		method: "GET",
		data: {
			top: top
		}
	});
}

/**
 * 根据页数和每页显示数量获取文章
 * @param {Object} page
 * @param {Object} size
 */
export function getPosts(page, size) {
	return request({
		url: "/api/admin/posts?page=" + page + "&size=" + size,
		method: "GET"
	});
}

/**
 * 根据文章 id 删除文章
 * @param {Object} id
 */
export function deletePosts(id) {
	return request({
		url: "/api/admin/posts/" + id,
		method: "DELETE"
	});
}

/**
 * 根据文章 id 更改文章状态
 * @param {Object} id
 */
export function updatePostStatus(id, status) {
	return request({
		url: "/api/admin/posts/" + id + "/status/" + status,
		method: "PUT"
	});
}

/**
 * 根据文章 id 获取文章数据
 * @param {Object} id
 */
export function getPost(id) {
	return request({
		url: "/api/admin/posts/" + id,
		method: "GET"
	});
}

/**
 * 修改文章数据
 * @param {Object} id
 * @param {Object} json
 */
export function updatePost(id, json, autoSave = "false") {
	return request({
		url: "/api/admin/posts/" + id + "?autoSave=" + autoSave,
		method: "PUT",
		data: json
	});
}

/**
 * 新增文章
 * @param {Object} json
 * @param {Object} autoSave
 */
export function addPost(json, autoSave = "false") {
	return request({
		url: "/api/admin/posts",
		method: "POST",
		data: json
	});
}

/**
 * 根据文章 id 获取文章预览 link
 * @param {Object} id
 */
export function getPostPreviewLink(id) {
	return request({
		url: "/api/admin/posts/preview/" + id,
		method: "GET",
		allData: true
	});
}

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

/**
 * 获取用户信息
 */
export function getUserProfiles() {
	return request({
		url: "/api/admin/users/profiles",
		method: "GET"
	});
}

/**
 * 修改用户数据
 * @param {Object} json
 */
export function updateUserProfiles(json) {
	return request({
		url: "/api/admin/users/profiles",
		method: "PUT",
		data: json
	});
}

/**
 * 获取用户统计数据
 */
export function getUserStatistics() {
	return request({
		url: "/api/admin/statistics/user",
		method: "GET"
	});
}

/**
 * 注销登录状态
 */
export function logout() {
	return request({
		url: "/api/admin/logout",
		method: "POST"
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

