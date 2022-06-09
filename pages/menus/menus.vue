<template>
	<view class="container">
		<u-notify ref="popup" duration="1500"></u-notify>
		<u-action-sheet :actions="options" :closeOnClickOverlay="true" :closeOnClickAction="true"
			:show="showActionSheet" cancelText="取消" @close="onActionSheetClose" @select="onActionSheetSelect">
		</u-action-sheet>
		<view class="list">
			<view class="block" v-for="(team, i) in teams" :key="i">
				<uni-row class="row">
					<uni-col :span="20">
						<view class="block-left active">
							{{team === "" ? "未分组（默认）" : team}}
							{{currentTeam === team ? "（默认）" : ""}}
						</view>
					</uni-col>
					<uni-col :span="4">
						<view class="block-right active" @click="onSelectOption(i)">
							选项
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getMenuTeams,
		getMenusByTeam,
		deleteMenusById,
		updateMenus
	} from "@/network/MenuApi.js";

	import {
		getOptionsByMapViewsKeys,
		updateOptionsByMapViews
	} from "@/network/SettingApi.js";
	export default {
		data() {
			return {
				// 菜单分组
				teams: [],
				// 当前使用的菜单分组
				currentTeam: "",

				// 选项的内容
				options: [{
						name: "设为默认分组"
					},
					{
						name: "重命名分组"
					},
					{
						name: "删除当前分组"
					}
				],

				// 控制操作菜单是否显示
				showActionSheet: false,
				// 标记当前点击的是哪个分组的“选项”按钮
				currentIndex: -1,
			}
		},

		mounted() {
			this.refreshMenuTeam();
		},

		onPullDownRefresh() {
			this.refreshMenuTeam();
		},

		methods: {
			/**
			 * 获取菜单分组
			 */
			refreshMenuTeam: function() {
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				getMenuTeams().then(data => {
					this.teams = data;
					// 获取当前使用的分组
					this.getDefaultMenuTeam();
					uni.stopPullDownRefresh();
					uni.hideLoading();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},

			/**
			 * 菜单分组的右侧选项按钮点击事件
			 */
			onSelectOption: function(i) {
				this.currentIndex = i;
				this.showActionSheet = true;
			},

			/**
			 * 操作菜单选择事件
			 * @param {Object} e
			 */
			onActionSheetSelect: function(e) {
				let that = this;
				let teamName = this.teams[this.currentIndex];
				let optionName = e.name;
				switch (optionName) {
					case "删除当前分组":
						uni.showModal({
							title: "确定要删除【" + teamName + "】分组和分组下所有菜单吗？",
							success(res) {
								if (res.confirm) {
									// 删除分组
									that.deleteTeamByName(teamName);
								}
							}
						});
						break;
					case "设为默认分组":
						this.setDefaultMenuTeam(teamName);
						break;
					case "重命名分组":
						uni.showModal({
							title: "设置分组名",
							editable: true,
							content: teamName,
							success(res) {
								if (res.confirm) {
									that.setTeamName(teamName, res.content);
								}
							}
						})
						break;
				}
			},

			/**
			 * 操作菜单取消按钮事件
			 */
			onActionSheetClose: function() {
				this.showActionSheet = false;
			},

			/**
			 * 根据分组名删除分组
			 * @param {Object} name
			 */
			deleteTeamByName: function(name) {
				// 先获取当前分组下的菜单
				let that = this;
				getMenusByTeam(name).then(data => {
					let menus = data;
					// 把菜单的 id 组一个数组
					let ids = new Array();
					for (let i = 0; i < menus.length; i++) {
						ids.push(menus[i].id);
					}
					// 将分组下的所有菜单删除，等价于删除分组
					deleteMenusById(ids).then(data => {
						that.popup("删除成功", "success");
						that.refreshMenuTeam();
					}).catch(err => {
						uni.showModal({
							title: "删除失败",
							content: err
						});
					});

				}).catch(err => {
					uni.showModal({
						title: "删除失败",
						content: err
					});
				})
			},


			/**
			 * 获取当前使用的菜单分组
			 */
			getDefaultMenuTeam: function() {
				let json = ["default_menu_team"];
				getOptionsByMapViewsKeys(json).then(data => {
					this.currentTeam = data.default_menu_team;
				}).catch(err => {
					uni.showModal({
						title: "获取当前使用分组失败",
						content: err
					});
				});
			},

			/**
			 * 设置当前使用的菜单分组
			 * @param {Object} name 
			 */
			setDefaultMenuTeam: function(name) {
				let map = {
					"default_menu_team": name
				};
				updateOptionsByMapViews(map).then(data => {
					// 设置成功
					this.refreshMenuTeam();
				}).catch(err => {
					uni.showModal({
						title: "设置失败",
						content: err
					});
				});
			},

			/**
			 * 设置分组名
			 * @param {Object} name
			 */
			setTeamName: function(oldName, newName) {
				/* 因为 Halo 分组本质上是一堆菜单
				   所以需要先获取当前分组下所有菜单
				   然后将每个菜单的 team 字段修改 */

				let that = this;
				let menus = [];
				getMenusByTeam(oldName).then(data => {
					menus = data;
					// 修改每个菜单的 team 字段
					for (let i = 0; i < menus.length; i++) {
						menus[i].team = newName;
					}
					// 将修改后的菜单提交
					let thatt = that;
					updateMenus(menus).then(data => {
						// 设置成功
						// 判断 oldName 之前是否是默认菜单
						if (thatt.currentTeam === oldName) {
							// oldName 之前是默认菜单
							// 将默认菜单名也修改成 newName
							thatt.setDefaultMenuTeam(newName);
						} else {
							thatt.refreshMenuTeam();
						}
						
					}).catch(err => {
						uni.showModal({
							title: "设置失败",
							content: err
						});
					});
				}).catch(err => {
					uni.showModal({
						title: "设置失败",
						content: err
					});
				});

			},

			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				if (type === "error") {
					this.$refs.popup.error(message);
				} else {
					this.$refs.popup.success(message);
				}
			},
		}
	}
</script>

<style>
	.container {}

	.block {
		position: relative;
		background-color: white;
		box-shadow: 0 1px 2px rgba(216, 216, 216, .5);
		height: 78rpx;
		line-height: 78rpx;
	}

	.block-left {
		padding-left: 30rpx;
		border-radius: 4px 0px 0px 4px;
		margin-right: 5rpx;
	}

	.block-right {
		width: 100%;
		text-align: center;
		float: right;
		border-left: 1px solid #eeeeee;
		padding-right: 4rpx;
		color: var(--textPrimaryColor);
		border-radius: 0px 4px 4px 0px;
	}
</style>
