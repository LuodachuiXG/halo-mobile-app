<template>
	<view class="container">
		<u-notify ref="popup" duration="1500"></u-notify>
		<!-- 从页面下方弹出的操作菜单 -->
		<u-action-sheet :actions="mode === 'team' ? teamOptions : menuOptions" :closeOnClickOverlay="true"
			:closeOnClickAction="true" :show="showActionSheet" cancelText="取消" @close="onActionSheetClose"
			@select="onActionSheetSelect">
		</u-action-sheet>
		<!-- 用于给菜单选择父菜单的选择器 -->
		<u-picker :show="showPicker" :columns="menusName" :defaultIndex="pickerValue"
			:title="'选择【' + currentMenuName + '】的父菜单'" 
			@confirm="onPickerChange" @close="onPickerClose" @cancel="onPickerClose"
			:closeOnClickOverlay="true"></u-picker>
		</picker>
		<view v-if="mode === 'team'" class="bottom-blank">
			<view class="block" v-for="(team, i) in teams" :key="i">
				<uni-row class="row">
					<uni-col :span="20">
						<view class="block-left active" @click="onTeamClick(i)">
							{{team === "" ? "未分组（默认）" : team}}
							{{currentTeamName === team ? "（默认）" : ""}}
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
				<u-button color="#007AFF" text="保存" style="margin-top: 10px;" 
					@click="onMenuSaveClick" :disabled="menus.length === 0 ? true : false"></u-button>
			</view>
			<u-empty mode="data" v-if="menus.length === 0"
				icon="http://cdn.uviewui.com/uview/empty/data.png"
				text="菜单为空,快创建一个吧"></u-empty>
			<view 
				v-for="(menu, i) in menus" 
				class="active block block-menu"
				:key="i"
				:style="'margin-left:' + getMenuMarginLeft(i)"
				 @click="onMenuClick(i)">
				<view class="menu-top">
					<u-icon name="list-dot" :label="menu.name"></u-icon>
				</view>
				<view class="menu-bottom">
					<text class="menu-url">{{menu.url}}</text>
				</view>
			</view>
		</view>
		<!-- 悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onFabClick"></uni-fab>
	</view>
</template>

<script>
	import {
		getMenuTeams,
		getMenusByTeam,
		deleteMenusById,
		deleteMenuById,
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
				currentTeamName: "",
				// 当前点击的分组索引
				currentTeamIndex: -1,


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


				// 父菜单选择 picker 是否显示
				showPicker: false,
				// picker 当前索引
				pickerValue: [0],
				// 由菜单名组成的一个字符串数组
				menusName: [],
				// 由菜单 id 组成的一个字符串数组
				menusId: [],
				
				
				// 存储所选分组下面的所有菜单
				menus: [],
				// 刷新时菜单数据存档，用于比对 menus 是否修改
				originalMenus: [],
				
				// 当前点击的菜单名
				currentMenuName: "",
				// 当前点击的菜单索引
				currentMenuIndex: -1,
				
				// 临时变量
				temp: null,
			}
		},

		onShow() {
			this.refreshData();
		},

		onPullDownRefresh() {
			this.refreshData();
		},
		
		onBackPress(event) {
			let that = this;
			if (event.from === "backbutton" && this.mode === "menu") {
				// 当触发返回并且当前处于菜单模式，就转为分组模式并取消返回事件
				if (this.menus != this.originalMenus) {
					uni.showModal({
						title: "数据已改变",
						content: "菜单数据已经修改，还没有保存，确定要返回吗？",
						complete: function(res) {
							if (res.confirm) {
								that.teamMode();
								return true;
							} else if (res.cancel) {
								return true;
							}
						}
					});
					return true;
				} else {
					this.teamMode();
					return true;
				}
			} else {
				return false;
			}
			
		},

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});

				if (this.mode === "team") {
					this.refreshMenuTeam();
				} else if (this.mode === "menu") {
					this.refreshMenuByTeam(this.currentTeamName);
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
					// 将获取的菜单进行格式化
					let mMenus = this.formatMenus(data);
					this.menus = mMenus;
					this.originalMenus = mMenus;
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
			 * @param {Object} i
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
					// 当前是菜单分组模式
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
				} else if (this.mode === 'menu') {
					// 当前是菜单模式
					let index = this.currentMenuIndex;
					let currentMenu = this.menus[index];
					let menuId = currentMenu.id;
					switch (optionName) {
						case "上移":
							// 首先判断当前菜单是否有父菜单
							if (currentMenu.parentId === 0) {
								// 无父菜单
								if (index > 0) {
									// 当前菜单不在首位
									let preMenu = this.menus[index - 1];
									
									if (preMenu.parentId !== undefined && preMenu.parentId !== 0) {
										// 上一个菜单是其他菜单的子菜单
										// 获取上一个菜单的父菜单（包括嵌套顶级父菜单）
										let parentMenu = this.getNestedParentMenuById(this.menus, preMenu.id);
										let parentIndex = this.getIndexById(this.menus, parentMenu.id);
										
										// 交换当前菜单和上一个菜单的父菜单的位置
										this.menus[index] = parentMenu;
										this.menus[parentIndex] = currentMenu;
										this.formatMenuLocation(this.menus);
										return ;
									}
								
									this.menus[index] = preMenu;
									this.menus[index - 1] = currentMenu;
								} else if (index === 0) {
									// 当前菜单在首位
									let preMenu = this.menus[this.menus.length - 1];
									
									// 删除 menus 中首位 menu
									this.menus.splice(0, 1);
									// 将 menu 添加到 menus 末尾
									this.menus.push(currentMenu);
								}
							} else {
								// 有父菜单
								// 判断当前子菜单是否只有自己一个兄弟子菜单
								let childrenCount = this.getChildrenCountByParentId(this.menus, currentMenu.parentId);
								if (childrenCount === 1) {
									// 父菜单只有一个子菜单，无法上移，提示用户
									this.popup("上移失败，没有同级子菜单");
									return ;
								} else {
									// 父菜单有大于 1 个子菜单
									// 判断上一个菜单是否是父菜单
									let preMenu = this.menus[index - 1];
									if (preMenu.id === currentMenu.parentId) {
										// 上一个菜单是当前菜单的父菜单
										
										// 在 menus 中删除当前子菜单
										this.menus.splice(index, 1);
										// 获取最后一个兄弟子菜单出现索引，并将当前菜单移动到最后一个子菜单后方
										let lastChildIndex = this.getLastChildIndex(this.menus, preMenu.id);
										
										// 将 menu 添加到 lastChildIndex 后方
										this.menus.splice(lastChildIndex + 1, 0, currentMenu);
										
									} else if (preMenu.parentId === currentMenu.parentId){
										// 上一个菜单是同级子菜单
										// 跟上一个子菜单交换位置
										this.menus[index - 1] = currentMenu;
										this.menus[index] = preMenu;
									} else {
										// 上一个菜单既不是父菜单，也不是同级子菜单
										// 证明上一个菜单可能是同级子菜单的子菜单
										
										// 获取上一个菜单的父菜单（和当前菜单是兄弟子菜单）
										preMenu = this.getNestedParentMenuById(this.menus, preMenu.id, currentMenu.parentId);
										let preMenuIndex = this.getIndexById(this.menus, preMenu.id);
										
										// 和上一个同级子菜单交换位置
										this.menus[preMenuIndex] = currentMenu;
										this.menus[index] = preMenu;
									}
								}
							}
							// 格式化菜单位置
							this.formatMenuLocation(this.menus);
							break;
						case "下移":
							// 获取当前菜单的最后一个子菜单索引
							let lastNestedChildIndex = this.getLastNestedChildIndex(this.menus, currentMenu.id);
							
							// 首先判断当前菜单是否有父菜单
							if (currentMenu.parentId === 0) {
								// 无父菜单
								if (index < this.menus.length - 1) {
									// 当前菜单不在尾位
									let nextMenu = this.menus[index + 1];
	
									if (nextMenu.parentId === currentMenu.id) {
										// 下一个菜单是当前菜单的子菜单
										if (lastNestedChildIndex === this.menus.length - 1) {
											// 当前菜单的最后一个子菜单所在位置是 menus 末尾
											// 证明当前菜单下移实际上是要移动到 menus 首位
											nextMenu = this.menus[0];
											
											// 删除 menus 中当前菜单
											this.menus.splice(index, 1);
											// 将 menu 添加到 menus 首位
											this.menus.splice(0, 0, currentMenu);
											// 格式化菜单位置
											this.formatMenuLocation(this.menus);
											return ;
										}

										// 将 nextMenu 设置为最后一个子菜单后方的 menu
										nextMenu = this.menus[lastNestedChildIndex + 1];
										this.menus[lastNestedChildIndex + 1] = currentMenu;
									} else {
										// 下一个菜单不是当前菜单的子菜单
										this.menus[index + 1] = currentMenu;
									}
									this.menus[index] = nextMenu;
									
								} else if (index === this.menus.length - 1) {
									// 当前菜单在尾位
									let nextMenu = this.menus[0];
									
									// 删除 menus 中尾位 menu
									this.menus.splice(this.menus.length - 1, 1);
									// 将 menu 添加到 menus 首位
									this.menus.splice(0, 0, currentMenu);
								}
							} else {
								// 有父菜单
								// 判断当前子菜单是否只有自己一个兄弟子菜单
								let childrenCount = this.getChildrenCountByParentId(this.menus, currentMenu.parentId);
								if (childrenCount === 1) {
									// 父菜单只有一个子菜单，无法下移，提示用户
									this.popup("下移失败，没有同级子菜单");
									return ;
								} else {
									// 父菜单有大于 1 个子菜单
									let lastChildIndex = this.getLastChildIndex(this.menus, currentMenu.parentId);
									let nextMenu = this.menus[index + 1];
									if (index === lastChildIndex) {
										// 当前菜单是父菜单的最后一个子菜单
										
										// 删除当前菜单
										this.menus.splice(index, 1);
										// 获取第一个兄弟子菜单出现索引，并将当前菜单移动到首个兄弟子菜单前方
										let firstChildIndex = this.getFirstChildIndex(this.menus, currentMenu.parentId);
										this.menus.splice(firstChildIndex, 0, currentMenu);
									} else if (nextMenu.parentId === currentMenu.parentId) {
										// 下一个菜单是兄弟子菜单，直接交换位置
										this.menus[index + 1] = currentMenu;
										this.menus[index] = nextMenu;
									} else {
										// 当前菜单不是最后一个同级子菜单，下一个菜单也不是兄弟菜单
										// 证明当前菜单还有子菜单，下一个菜单是当前菜单的子菜单
										// 获取当前菜单的最后一个子菜单索引
										lastChildIndex = this.getLastNestedChildIndex(this.menus, currentMenu.id);
										nextMenu = this.menus[lastChildIndex + 1];
										if (nextMenu.parentId === currentMenu.parentId) {
											// 当前菜单的最后一个子菜单的下一个菜单是当前菜单同级兄弟菜单
											// 交换位置
											this.menus[lastChildIndex + 1] = currentMenu;
											this.menus[index] = nextMenu;
										}
									} 
								}
							}
							// 格式化菜单位置
							this.formatMenuLocation(this.menus);
							break;
						case "编辑":
							// 编辑菜单
							uni.navigateTo({
								url: "./edit/edit?type=update&teamName=" + 
									this.currentTeamName + "&id=" + menuId
							});
							break;
						case "删除":
							uni.showModal({
								title: "确定要删除【" + currentMenu.name + "】菜单吗？",
								success(res) {
									if (res.confirm) {
										// 删除菜单
										deleteMenuById(menuId).then(data => {
											// 删除成功，修改菜单变量，不重新从服务器读取
											that.menus.splice(index, 1);
										}).catch(err => {
											uni.showModal({
												title: "删除失败",
												content: err
											});
										});
									}
								}
							});
							break;
						case "父菜单":
							// 先将所有菜单名和 id 组成一个字符串数组
							let arrayName = ["无"];
							let arrayId = [0];
							this.menusName = [];
							this.menusId = [];
							for (let i = 0; i < this.menus.length; i++) {
								// 不能设置自己为父菜单
								if (i === index) continue;
								// 不能设置自己的子菜单为父菜单
								if (this.isParentMenu(this.menus, this.menus[i].id, currentMenu.id)) continue;
								
								arrayName.push(this.menus[i].name);
								arrayId.push(Number(this.menus[i].id));
							}
							// 此变量用于设置 u-picker
							// 因为 u-picker 组件需要二维数组，所以这里 push 而不是直接赋值
							this.menusName.push(arrayName);
							this.menusId = arrayId;
							this.showPicker = true;
							if (currentMenu.parentId !== 0) {
								// 当前菜单有父菜单，所以需要设置 picker 默认选择项
								let parentName = this.getMenuNameById(currentMenu.parentId);
								this.pickerValue = [arrayName.indexOf(parentName)];
							} else {
								this.pickerValue = [0];
							}
							break;
					}
				}
			},

			/**
			 * 父菜单 picker 选项更改事件
			 * @param {Object} e
			 */
			onPickerChange: function(e) {
				// 选择的菜单名
				let selectMenuName = e.value[0];
				let selectIndex = e.indexs[0];
				let selectMenuId = this.menusId[selectIndex];
				let menu = this.menus[this.currentMenuIndex];
				if (selectIndex === 0) {
					// 清除父菜单
					menu.parentId = 0;
					this.menus[this.currentMenuIndex] = menu;
				} else {
					// 设置父菜单
					menu.parentId = selectMenuId;
					this.menus[this.currentMenuIndex] = menu;
				}
				this.showPicker = false;
				// 格式化菜单位置
				this.formatMenuLocation(this.menus);
				
			},
			
			/**
			 * 父菜单 picker 关闭事件
			 */
			onPickerClose: function() {
				this.showPicker = false;
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
					this.currentTeamName = data.default_menu_team;
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
			 * @param {Object} oldName
			 * @param {Object} newName
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
						if (thatt.currentTeamName === oldName) {
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
				this.currentTeamName = this.teams[i];
				// 切换到菜单模式
				this.menuMode(this.currentTeamName);
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
				this.menus = [];
				this.refreshData();
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
				this.currentMenuName = this.menus[i].name;
				this.showActionSheet = true;
			},
			
			/**
			 * 将循环嵌套的父子菜单的所有子菜单全部取到第一层
			 * 并且将父菜单的 children 赋空
			 * @param {Object} mMenus 要格式化的 menus 数据
			 */
			formatMenus: function(mMenus) {
				this.temp = [];
				let result = this._formatMenus(mMenus);
				this.temp = [];
				return result;
			},
			
			_formatMenus: function(mMenus) {
				for (let i = 0; i < mMenus.length; i++) {
					let menu = mMenus[i];
					if (menu.children !== undefined && menu.children.length > 0) {
						// menu 的子菜单不为空，取出来处理并将 menu 的子菜单赋空加入 temp 数组
						let children = menu.children;
						menu.children = [];
						this.temp.push(menu);
						this._formatMenus(children);
					} else {
						// menu 的子菜单为空，直接加入 temp 数组
						this.temp.push(menu);
					}
				}
				return this.temp;
			},
			
			/**
			 * 根据菜单 id 获取菜单名
			 * @param {Object} id
			 */
			getMenuNameById: function(id) {
				for (let i = 0; i < this.menus.length; i++) {
					if (this.menus[i].id === id) {
						return this.menus[i].name;
					}
				}
				return "";
			},
			
			/**
			 * 根据菜单 id 获取菜单
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getMenuById: function(mMenus, id) {
				for (let i = 0; i < mMenus.length; i++) {
					if (mMenus[i].id === id) {
						return mMenus[i];
					}
				}
				return [];
			},
			
			
			/**
			 * 根据父菜单 id，获取所有子菜单 id 组成的数组
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getChildrenIdByParentId: function(mMenus, id) {
				let array = [];
				if (this.getChildrenCountByParentId(mMenus, id) <= 0)  return array;
				
				for (let i = 0; i < mMenus.length; i++) {
					let menu = mMenus[i];
					if (menu.parentId === id) {
						array.push(menu.id);
					}
				}
				return array;
			},
			
			
			/**
			 * 根据父菜单 id 获取子菜单数量
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getChildrenCountByParentId: function(mMenus, id) {
				let count = 0;
				for (let i = 0; i < mMenus.length; i++) {
					let menu = mMenus[i];
					if (menu.parentId != undefined && menu.parentId === id) {
						count++;
					}
				}
				return count;
			},
			
			/**
			 * 根据菜单 id 获取索引
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getIndexById: function(mMenus, id) {
				for (let i = 0; i < mMenus.length; i++) {
					if (mMenus[i].id === id) return i;
				}
				return -1;
			},
			
			/**
			 * 根据子菜单 id，获取当前子菜单在所有同级兄弟菜单中的位置
			 * @param {Object} mMenus
			 * @param {Object} childId
			 */
			getChildPositionByChildId: function(mMenus, childId) {
				let index = -1;
				let menu = this.getMenuById(mMenus, childId);
				if (menu.parentId === undefined || menu.parentId === 0) {
					return index;
				}
				
				// 获取所有同级子菜单数组
				let childrenIds = this.getChildrenIdByParentId(mMenus, menu.parentId);
				index = childrenIds.indexOf(childId);
				return index;
			},
			
			
			/**
			 * 根据父菜单 id 获取子菜单在 menus 最先出现位置
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getFirstChildIndex: function(mMenus, id) {
				for (let i = 0; i < mMenus.length; i++) {
					if (mMenus[i].parentId != undefined && mMenus[i].parentId === id) {
						return i;
					}
				}
				return -1;	
			},
			
			/**
			 * 根据父菜单 id 获取子菜单在 mMenus 最后出现位置
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getLastChildIndex: function(mMenus, id) {
				let index = -1;
				for (let i = 0; i < mMenus.length; i++) {
					if (mMenus[i].parentId != undefined && mMenus[i].parentId === id) {
						index = i;
					}
				}
				return index;	
			},
			
			/**
			 * 根据父菜单 id 获取子菜单在 mMenus 最后出现位置（包括嵌套子菜单）
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getLastNestedChildIndex: function(mMenus, id) {
				// 获取最后一个子菜单
				let index = this.getLastChildIndex(mMenus, id);
				if (index === mMenus.length - 1) {
					// 最后一个子菜单已经位于 mMenus 末尾，返回 index
					return index;
				} else {
					// 获取最后一个子菜单的下一个菜单
					let menu = mMenus[++index];
					// 判断是否是嵌套子菜单
					while (this.isParentMenu(mMenus, menu.id, id)) {
						// 当前菜单是嵌套子菜单
						if (index >= mMenus.length - 1) {
							// 当前菜单 index 已经位于 mMenus 末尾，返回 index
							return index;
						}
						
						// 继续获取下一个菜单
						menu = mMenus[++index];
					}
					
					// 如果走到这，说明上面 while 判断否，一次都没循环
					// 证明前一个菜单就是最后一个嵌套子菜单，所以这里 -1
					return index - 1;
				}
			},
			
			
			/**
			 * 根据当前 menu 菜单父子状态返回对应的左边距
			 * @param {Object} i
			 */
			getMenuMarginLeft: function(i) {
				// 第一个 menu 不做处理
				if (i === 0) return "";
				let menu = this.menus[i];
				// 因为菜单默认的 margin-left 就是30，所以这里再加 30
				return (this.getMenuParentCount(this.menus, menu.id) * 50 + 30) + "rpx;";
			},
			
			/**
			 * 获取菜单的父菜单数量（当前菜单所在层级数）
			 * @param {Object} mMenus
			 * @param {Object} id
			 */
			getMenuParentCount: function(mMenus, id) {
				let count = 0;
				let menu = this.getMenuById(mMenus, id);
				let name = menu.name;
				while (menu.parentId != undefined && menu.parentId !== 0) {
					menu = this.getMenuById(mMenus, menu.parentId);
					count++;
				}
				return count;
			},
			
			/**
			 * 根据子菜单 id 获取嵌套父菜单（嵌套最顶级父菜单）
			 * @param {Object} mMenus
			 * @param {Object} id
			 * @param {Object} parentId 指定一个父菜单 id，当此项满足时就返回满足的 menu，而不是顶级父菜单 menu
			 */
			getNestedParentMenuById: function(mMenus, id, parentId = -1) {
				let menu = this.getMenuById(mMenus, id);
				while (menu.parentId != undefined && menu.parentId !== 0 && menu.parentId !== parentId) {
					menu = this.getMenuById(mMenus, menu.parentId);
				}
				return menu;
			},
			
			/**
			 * 在 mMenus 中是否还有指定层级（父菜单数量）的子菜单
			 * @param {Object} mMenus
			 * @param {Object} count
			 */
			isHaveSpecifiedParentCount: function(mMenus, count) {
				for (let i = 0; i < mMenus.length; i++) {
					let menu = mMenus[i];
					let parentCount = this.getMenuParentCount(this.menus, menu.id);
					if (parentCount === count) {
						return true;
					}
				}
				return false;
			},
			
			/**
			 * 根据 id 判断某菜单是否是某菜单的父菜单（包括嵌套子菜单）
			 * @param {Object} mMenus
			 * @param {Object} childId
			 * @param {Object} parentId
			 */
			isParentMenu: function(mMenus, childId, parentId) {
				if (childId === 0 || parentId === 0) return false;
				
				let menu = this.getMenuById(mMenus, childId);
				while (menu.parentId != undefined && menu.parentId !== 0) {
					if (menu.parentId === parentId) return true;
					menu = this.getMenuById(mMenus, menu.parentId);
				}
				return false;
			},
			
			
			
			/**
			 * 整理菜单位置
			 * 1.子菜单位于父菜单下方；
			 * 2.非子菜单不应位于子菜单之中；
			 * @param {Object} mMenus
			 */
			formatMenuLocation: function(mMenus) {
				// 传递值而不是引用
				let menus = JSON.parse(JSON.stringify(mMenus));
				let result = [];
				
				// 处理子菜单时先处理只有一个父菜单的子菜单
				let parentCount = 1;
				while(menus.length > 0) {
					let menu = menus[0];
					if (menu.parentId != undefined && Number(menu.parentId) === 0) {
						// 没有父菜单
						result.push(menu);
						menus.splice(0, 1);
					} else {
						// 有父菜单
						let parentIndex = this.getIndexById(menus, menu.parentId);
						if (parentIndex === -1 && this.getIndexById(result, menu.parentId) !== -1) {
							// menus 中没有父菜单，并且父菜单已经在 result 中
							
							
							let currentMenuParentCount = this.getMenuParentCount(mMenus, menu.id);
							if (currentMenuParentCount === parentCount) {
								// 当前菜单父菜单数量和正在处理的指定数量相同
								
								// 获取父菜单在 result 中索引
								parentIndex = this.getIndexById(result, menu.parentId);
								// 获取父菜单在 result 中的子菜单最后索引
								let lastChildIndex = this.getLastChildIndex(result, menu.parentId);
								if (lastChildIndex !== -1) {
									// 插入菜单到同级子菜单最后出现索引 + 1
									result.splice(lastChildIndex + 1, 0, menu);
								} else {
									// 插入菜单到父菜单索引 + 1
									result.splice(parentIndex + 1, 0, menu);
								}
								menus.splice(0, 1);
							} else {
								// 当前菜单父菜单数量和正在处理的指定数量不同
								// 先将菜单移动到 menus 最后，直到正在处理的指定数量相同
								menus.push(menus[0]);
								menus.splice(0, 1);
							}
							
							if (!this.isHaveSpecifiedParentCount(menus, parentCount)) {
								// 当前在 menus 中指定处理的父菜单数量的菜单已经处理完成
								// 处理数量 + 1，处理父菜单更多的菜单（处理嵌套子菜单）
								parentCount = parentCount + 1;
							}
						} else {
							// menus 有父菜单，并且父菜单不在 result 中
							// 先将当前子菜单移动到 menus 最后，直到父菜单已经在 result 中
							menus.push(menus[0]);
							menus.splice(0, 1);
						}
						
					}
				}
				this.menus = result;
				this.formatMenuPriority();
			},
			
			/**
			 * 根据 mMenus 中菜单位置以及父子关系，设置 priority 
			 * 在执行前需执行一次 formatMenuLocation 方法，防止数据出错
			 * @param {Object} mMenus
			 */
			formatMenuPriority: function() {
				// 传递值而不是引用
				let result = [];
				let menus = JSON.parse(JSON.stringify(this.menus));
				let menuPriority = 0;
				while(menus.length > 0) {
					let menu = menus[0];
					if (menu.parentId !== undefined && Number(menu.parentId) === 0) {
						// 没有父菜单
						menu.priority = menuPriority;
						result.push(menu);
						menus.splice(0, 1);
						menuPriority++;
					} else {
						// 有父菜单
						// 获取当前子菜单在所有同级子菜单中的位置
						let position = this.getChildPositionByChildId(this.menus, menu.id);
						menu.priority = position;
						result.push(menu);
						menus.splice(0, 1);
					}
				}
				this.menus = result;
			},
			
			/**
			 * 保存菜单按钮点击事件
			 */
			onMenuSaveClick: function() {
				let that = this;
				updateMenus(this.menus).then(data => {
					// 保存成功后不重新读取数据，直接给 originalMenus 赋 menus 的值
					this.originalMenus = this.menus;
					that.popup("保存成功", "success");
				}).catch(err => {
					uni.showModal({
						title: "保存数据失败",
						content: err
					});
				});
			},
			
			/**
			 * 添加菜单分组
			 * @param {Object} teamName
			 */
			addMenuTeam: function(teamName) {
				// 实际上并没有添加任何东西
				// 这里只是获取 teamName 分组下的菜单并切换到 menu 模式
				this.currentTeamName = teamName;
				this.refreshMenuByTeam(teamName);
			},
			
			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function() {
				let that =this;
				if (this.mode === "team") {
					uni.showModal({
						title: "添加菜单分组",
						editable: true,
						success: function(res) {
							if (res.confirm && res.content !== undefined && res.content.length > 0) {
								that.addMenuTeam(res.content);
							}
						}
					});
				} else if (this.mode === "menu") {
					// 新建菜单
					uni.navigateTo({
						url: "./edit/edit?type=add&teamName=" + this.currentTeamName
					});
				}
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
		margin-bottom: -10rpx;
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
	
	.marginLeft {
		margin-left: 60rpx;
	}
</style>
