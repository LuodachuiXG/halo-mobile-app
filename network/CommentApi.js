import request from "./Request.js";

/**
 * 根据不同条件获取评论
 * @param {*} page 
 * @param {*} size 
 * @param {*} keyword 
 * @param {*} status 
 * @param {*} type 文章、日志、页面评论
 */
export function getComments(page, size, keyword = "", status = "", type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments?page=" + page + "&size=" + size + 
		"&keyword=" + keyword + "&status=" + status,
		method: "GET"
	});
}

/**
 * 根据评论 id 更改评论状态
 * @param {*} id
 * @param {*} status 
 * @param {*} type 
 */
export function updateCommentStatus(id, status, type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments/" + id + "/status/" + status,
		method: "PUT"
	});
}

/**
 * 根据评论 id 数组批量更改评论状态
 * @param {*} ids 
 * @param {*} status 
 * @param {*} type 
 */
export function updateCommentsStatus(ids, status, type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments/status/" + status,
		method: "PUT",
		data: ids
	});
}


/**
 * 根据文章和父评论 id 来回复评论
 * @param {*} postId 
 * @param {*} content 
 * @param {*} parentId 
 * @param {*} type 
 */
export function replyComment(postId, content, parentId = "", type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments",
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
 * @param {*} type 
 */
export function deleteComment(id, type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments/" + id,
		method: "DELETE"
	});
}

/**
 * 根据评论 id 数组来批量删除评论
 * @param {*} ids
 * @param {*} posts
 */
export function deleteComments(ids, type = "posts") {
	return request({
		url: "/api/admin/" + type + "/comments",
		method: "DELETE",
		data: ids
	});
}


