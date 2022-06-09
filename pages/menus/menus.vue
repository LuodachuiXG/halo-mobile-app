<template>
	<view class="container">
		<u-notify ref="popup" duration="1500"></u-notify>
		<u-action-sheet :actions="mode === 'team' ? teamOptions : menuOptions" :closeOnClickOverlay="true" :closeOnClickAction="true"
			:show="showActionSheet" cancelText="取消" @close="onActionSheetClose" @select="onActionSheetSelect">
		</u-action-sheet>
		<view v-if="mode === 'team'" class="bottom-blank">
			<view class="block" v-for="(team, i) in teams" :key="i">
				<uni-row class="row">
					<uni-col :span="20">
						<view class="block-left active" @click="onTeamClick(i)">
							{{team === "" ? "未分组（默认）" : team}}
							{{currentTeam === team ? "（默认）" : ""}}
						</view>
					</uni-col>
					<uni-col :span="4">
						<view class="block-right active" @click="onOptionClick(i)">
							选项
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
		
		<view v-if="mode === 'menu'" class="bottom-blank">
			<view style="margin: 30rpx;">
				<u-button type="warning" text="返回菜单分组" @click="teamMode"></u-button>
				<u-button color="#007AFF" text="保存" style="margin-top: 10px;"  @click=""></u-button>
			</view>
			<view class="active block block-menu" v-for="(menu, i) in menus" :key="i" @click="onMenuClick(i)">
				<view class="menu-top">
					<u-icon name="list-dot" :label="menu.name"></u-icon>
				</view>
				<view class="menu-bottom">
					<text class="menu-url">{{menu.url}}</text>
				</view>
			</view>
		</view>
		<uni-fab horizontal="right" vertical="bottom" @trigger=""></uni-fab>
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
				// 用于标记当前页面是什么模式，team/menu
				mode: "team",
				
				// 菜单分组
				teams: [],
				// 当前使用的菜单分组
				currentTeam: "",
				// 当前点击的分组索引
				currentTeamIndex: -1,
				
				// 当前点击的菜单索引
				currentMenuIndex: -1,

				// 分组的 ActionSheet 内容
				teamOptions: [{
						name: "设为默认分组"
					},
					{
						name: "重命名分组"
					},
					{
						name: "删除当前分组"
					}
				],
				// 菜单的 ActionSheet 内容
				menuOptions: [{
						name: "上移"
					},
					{
						name: "下移"
					},
					{
						name: "编辑"
					},
					{
						name: "删除"
					},
					{
						name: "父菜单"
					}
				],

				// 控制操作菜单是否显示
				showActionSheet: false,
				
				
				// 存储所选分组下面的所有菜单
				menus: [],
				
			}
		},

		mounted() {
			this.refreshData();
		},

		onPullDownRefresh() {
			this.refreshData();
		},

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				
				if (this.mode === "team"){
					this.refreshMenuTeam();
				} else if (this.mode === "menu") {
					this.refreshMenuByTeam(this.currentTeam);
				}
			},
			
			/**
			 * 获取菜单分组
			 */
			refreshMenuTeam: function() {
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
			 * 根据分组名获取菜单
			 * @param {Object} name
			 */
			refreshMenuByTeam: function(name) {
				getMenusByTeam(name).then(data => {
					this.menus = data;
					this.menuMode(name);
					uni.stopPullDownRefresh();
					uni.hideLoading();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
					uni.showModal({
						title: "获取菜单数据失败",
						content: err
					});
				});
			},

			/**
			 * 菜单分组的右侧选项按钮点击事件
			 */
			onOptionClick: function(i) {
				this.currentTeamIndex = i;
				this.showActionSheet = true;
			},

			/**
			 * 操作菜单选择事件
			 * @param {Object} e
			 */
			onActionSheetSelect: function(e) {
				let that = this;
				let teamName = this.teams[this.currentTeamIndex];
				let optionName = e.name;
				if (this.mode === 'team') {
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
				} else if (mode === 'menu') {
					switch (optionName) {
						case "上移":
							break;
						case "下移":
							break;
						case "编辑":
							break;
						case "删除":
							break;
						case "父菜单":
							break;
					}
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
						that.refreshData();
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
					this.refreshData();
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
							thatt.refreshData();
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
			 * 分组点击事件
			 * @param {Object} i
			 */
			onTeamClick: function(i) {
				this.currentTeamIndex = i;
				this.currentTeam = this.teams[i];
				// 切换到菜单模式
				this.menuMode(this.currentTeam);
				// 刷新数据
				this.refreshData();
			},
			
			/**
			 * 切换到分组模式
			 */
			teamMode: function() {
				this.mode = "team";
				uni.setNavigationBarTitle({
					title: "菜单分组"
				});
			},
			
			/**
			 * 切换到菜单模式
			 * @param {Object} menuName 菜单名
			 */
			menuMode: function(menuName) {
				this.mode = "menu";
				uni.setNavigationBarTitle({
					title: menuName
				});
			},
			
			/**
			 * 菜单点击事件
			 * @param {Object} i
			 */
			onMenuClick: function(i) {
				this.currentMenuIndex = i;
				this.showActionSheet = true;
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
	
	.block-menu {
		padding: 30rpx;
	}
	
	.menu-url {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 1;
		color: var(--textContentColor);
	}
	
	.bottom-blank {
		margin-bottom: 120px;
	}
</style>
