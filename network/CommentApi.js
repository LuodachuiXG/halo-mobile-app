import request from "./Request.js";

/**
 * 根据不同条件获取评论
 * @param {*} page 
 * @param {*} size 
 * @param {*} keyword 
 * @param {*} status 
 */
export function getComments(page, size, keyword = "", status = "") {
	return request({
		url: "/api/admin/posts/comments?page=" + page + "&size=" + size + 
		"&keyword=" + keyword + "&status=" + status,
		method: "GET"
	});
}

/**
 * 根据评论 id 更改评论状态
 * @param {Object} id
 */
export function updateCommentStatus(id, status) {
	return request({
		url: "/api/admin/posts/comments/" + id + "/status/" + status,
		method: "PUT"
	});
}

/**
 * 根据评论 id 数组批量更改评论状态
 * @param {*} ids 
 * @param {*} status 
 */
export function updateCommentsStatus(ids, status) {
	return request({
		url: "/api/admin/posts/comments/status/" + status,
		method: "PUT",
		data: ids
	});
}


/**
 * 根据文章和父评论 id 来回复评论
 * @param {*} postId 
 * @param {*} content 
 * @param {*} parentId 
 */
export function replyComment(postId, content, parentId = "") {
	return request({
		url: "/api/admin/posts/comments",
		method: "POST",
		data: {
			content: content,
			postId: postId,
			parentId: parentId
		}
	});
}

/**
 * 根据评论 id 来删除评论
 * @param {*} id 
 */
export function deleteComment(id) {
	return request({
		url: "/api/admin/posts/comments/" + id,
		method: "DELETE"
	});
}

/**
 * 根据评论 id 数组来批量删除评论
 * @param {*} id 
 */
export function deleteComments(ids) {
	return request({
		url: "/api/admin/posts/comments",
		method: "DELETE",
		data: ids
	});
}


