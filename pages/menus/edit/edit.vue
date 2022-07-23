<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>

		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>名称：</view>
				<input class="input" type="text" v-model="name" />
				<text class="view-input-text">
					* 页面上所显示的名称
				</text>
			</view>
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>地址：</view>
				<input class="input" type="text" v-model="mUrl" />
				<text class="view-input-text">
					* 菜单的地址
				</text>
			</view>
			<view class="view-input">
				<view class="view-input-titleView">图标：</view>
				<input class="input" type="text" v-model="icon" />
				<text class="view-input-text">
					* 请根据主题的支持情况选填
				</text>
			</view>
			<view class="view-input">
				<view class="view-input-titleView">打开方式：</view>
				<radio-group style="margin-top: 10px;" @change="onRadioChange">
					<label>
						<radio value="_self" :checked="target === '_self' ? true: false"/>
						当前窗口
					</label>
					<label style="margin-left: 15px;">
						<radio value="_blank" :checked="target === '_blank' ? true: false"/>
						新窗口
					</label>
				</radio-group>
			</view>
			<view class="view-input" style="position: relative;margin-top: 20px;">
				<button class="btn" type="primary" @click="releaseMenu">保存</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getMenuById,
		addMenu,
		updateMenu
	} from "@/network/MenuApi.js";
	export default {
		data() {
			return {
				// 当前是编辑还是新增菜单，add/update
				type: "",

				// 当前操作的菜单所属的分组
				teamName: "",
				// 菜单 id
				menuId: 0,
				
				// 菜单对象
				mMenu: [],
				
				name: "",
				mUrl: "",
				icon: "",
				
				// 打开方式默认在当前窗口打开
				target: "_self",
			}
		},
		onLoad(e) {
			this.type = e.type;
			this.teamName = e.teamName;
			if (this.type === "add") {
				uni.setNavigationBarTitle({
					title: "新增菜单"
				});
			} else {
				uni.setNavigationBarTitle({
					title: "编辑菜单"
				});
				this.menuId = e.id;
				this.refreshData();
			}
		},
		onShow() {
			
		},
		methods: {
			/**
			 * 获取菜单数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				getMenuById(this.menuId).then(data => {
					this.name = data.name;
					this.mUrl = data.url;
					this.icon = data.icon;
					this.target = data.target;
					this.mMenu = data;
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},


			/**
			 * 提交按钮点击事件
			 */
			releaseMenu: function() {
				let that = this;
				if (this.name.length <= 0) {
					this.popup("菜单名称不能为空");
					return ;
				}
				
				if (this.mUrl.length <= 0) {
					this.popup("菜单地址不能为空");
					return ;
				}
				
				
				if (this.type === "add") {
					// 添加菜单
					let json = {
						"name": this.name,
						"url": this.mUrl,
						"icon": this.icon,
						"team": this.teamName,
						"target": this.target
					};
					addMenu(json).then(data => {
						that.toast("添加成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "添加菜单失败",
							content: err
						});
					});
				} else {
					let json = {
						"id": Number(this.menuId),
						"name": this.name,
						"url": this.mUrl,
						"icon": this.icon,
						"team": this.mMenu.team,
						"target": this.target,
						"parentId": this.mMenu.parentId,
						"priority": this.mMenu.priority
					};
					updateMenu(json).then(data => {
						that.toast("修改成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "修改菜单失败",
							content: err
						});
					});
				}
			},
			
			/**
			 * 单选按钮改变事件
			 * @param {Object} event
			 */
			onRadioChange: function(event)  {
				this.target = event.detail.value;
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
	.content {
		padding-bottom: 50px;
	}

	.block {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.btn {
		color: #FFFFFF;
		height: 60rpx;
		width: 100%;
		line-height: 60rpx;
		margin-right: 20rpx;
		font-size: .9em;
	}
</style>
