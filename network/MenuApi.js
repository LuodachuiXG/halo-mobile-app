import request from "./Request.js";

/**
 * 获取菜单分组
 */
export function getMenuTeams() {
	return request({
		url: "/api/admin/menus/teams",
		method: "GET"
	});
}

/**
 * 根据分组来获取菜单
 */
export function getMenusByTeam(team = "") {
	return request({
		url: "/api/admin/menus/team/tree_view?team=" + team,
		method: "GET"
	});
}

/**
 * 根据菜单 id 数组删除菜单
 */
export function deleteMenusById(ids) {
	return request({
		url: "/api/admin/menus/batch",
		method: "DELETE",
		data: ids
	});
}

/**
 * 根据菜单 id 删除菜单
 */
export function deleteMenuById(id) {
	return request({
		url: "/api/admin/menus/" + id,
		method: "DELETE",
	});
}

/**
 * 批量修改菜单
 */
export function updateMenus(menus) {
	return request({
		url: "/api/admin/menus/batch",
		method: "PUT",
		data: menus
	});
}