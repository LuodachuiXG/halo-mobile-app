import request from "./Request.js";

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
 * 根据不同条件获取文章
 * @param {*} page 
 * @param {*} size 
 * @param {*} keyword 
 * @param {*} status 
 * @param {*} categoryId 
 */
export function getPosts(page, size, keyword = "", status = "", categoryId = "") {
	// 默认显示除回收站外其他所有状态的文章
	let mStatus = status;
	if (mStatus.length <= 0) {
		mStatus = "&statuses=PUBLISHED&statuses=DRAFT&statuses=INTIMATE";
	} else {
		mStatus = "&status=" + mStatus;
	}
	return request({
		url: "/api/admin/posts?page=" + page + "&size=" + size + 
		"&keyword=" + keyword + "&categoryId=" + categoryId + mStatus,
		method: "GET"
	});
}

/**
 * 根据文章 id 删除文章
 * @param {Object} id
 */
export function deletePost(id) {
	return request({
		url: "/api/admin/posts/" + id,
		method: "DELETE"
	});
}

/**
 * 根据文章 id 数组批量删除文章
 * @param {Object} id
 */
export function deletePosts(ids) {
	return request({
		url: "/api/admin/posts",
		method: "DELETE",
		data: ids
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
 * 根据文章 id 数组批量更改文章状态
 * @param {Object} id
 */
export function updatePostsStatus(ids, status) {
	return request({
		url: "/api/admin/posts/status/" + status,
		method: "PUT",
		data: ids
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