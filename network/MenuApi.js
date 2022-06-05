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