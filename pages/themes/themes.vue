<template>
	<view class="main">
		<u-notify ref="popup"></u-notify>
		<view class="block" v-for="(theme, i) in themes">
			<view class="block-name">
				<view class="currentTheme tips-info" v-if="theme.activated">当前启用</view>
				{{ theme.name }}
			</view>
			<view class="block-screenshots">
				<image :src="getUrl() + theme.screenshots"></image>
			</view>
			<view class="block-action">
				<uni-row>
					<uni-col :span="8">
						<view class="block-action-item" :class="theme.activated ? 'activated':''"
							@click="onActivatedClick(i)">
							<image :src="theme.activated ? '../../static/images/unlock.png' : 
								'../../static/images/lock.png'"></image>
							{{theme.activated ? "已启用" : "启用"}}
						</view>
					</uni-col>
					<uni-col :span="8">
						<view class="block-action-item border" @click="onSettingClick(i)">
							<image src="../../static/images/setting.png"
								style="width: 35rpx; height: 35rpx;top: 8rpx;margin-top: -8rpx;"></image>
							设置
						</view>
					</uni-col>
					<uni-col :span="8">
						<view class="block-action-item" @click="onMoreClick(i)">
							<image src="../../static/images/more.png"></image>
							更多
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getThemes,
		updateThemeActivation,
		deleteTheme,
	} from "@/network/ThemeApi.js";
	export default {
		data() {
			return {
				themes: null,
			}
		},
		mounted() {
			this.refreshData()
		},

		/**
		 * 下拉刷新事件
		 */
		onPullDownRefresh() {
			this.refreshData()
		},
		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let that = this;
				getThemes().then(data => {
					that.themes = data;
					// 将已经启用的主题排在第一个
					for (var i = 0; i < that.themes.length; i++) {
						if (that.themes[i].activated) {
							let current = that.themes[i];
							that.themes[i] = that.themes[0];
							that.themes[0] = current;
						}
					}
					uni.stopPullDownRefresh();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});

			},

			/**
			 * 启用主题单击事件
			 * @param {Object} i
			 */
			onActivatedClick: function(i) {
				let that = this;
				let currentThemeId = this.themes[0].id;
				let clickThemeId = this.themes[i].id;
				if (currentThemeId === clickThemeId) {
					// 如果点击的主题就是目前启用的主题就直接返回
					return;
				}

				// 提交激活主题请求
				updateThemeActivation(clickThemeId).then(data => {
					that.popup("启用主题成功", "success");
					that.refreshData();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "启用主题失败",
						content: err
					});
				});
			},

			/**
			 * 更多单击事件
			 * @param {Object} i
			 */
			onMoreClick: function(i) {
				let that = this;
				uni.showActionSheet({
					itemList: ['删除'],
					success: function(res) {
						let thatt = that;
						switch (res.tapIndex) {
							// 删除
							case 0:
								// 如果当前删除的是启用的主题就报错
								if (i == 0) {
									that.toast("无法删除启用的主题");
									return;
								}
								uni.showModal({
									title: '提示',
									content: '确定要删除 ' + thatt.themes[i].name + ' 吗？',
									success: function(res) {
										if (res.confirm) {
											deleteTheme(thatt.themes[i].id).then(data => {
												thatt.popup("删除成功", "success");
												thatt.refreshData();
											}).catch(err => {
												uni.stopPullDownRefresh();
												uni.showModal({
													title: "删除主题失败",
													content: err
												});
											});
										}
									}
								});
								break;
						};
					}
				});
			},
			
			
			/**
			 * 设置单击事件
			 * @param {Object} i
			 */
			onSettingClick: function(i) {
				// uni.navigateTo({
				// 	url: "./themeSetting/themeSetting?id=" + this.themes[i].id
				// })
				this.popup("暂不支持主题设置，请前往 Web 端设置");
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
	.block {
		overflow: hidden;
	}

	.block:last-child {
		margin-bottom: 40rpx;
	}

	.block-name {
		padding: 30rpx;
		border-bottom: 1px solid #ececec;
		font-weight: bold;
		font-size: 1.1em;
	}

	.block-screenshots {}

	.block-screenshots image {
		vertical-align: middle;
		width: 100%;
	}

	.block-action {
		border-top: 1px solid #ececec;
		color: #898989;
	}

	.block-action-item {
		position: relative;
		padding: 30rpx;
		text-align: center;
	}

	.block-action-item:active {
		background-color: var(--activatedColor);
	}

	.block-action-item image {
		position: relative;
		top: 10rpx;
		margin-top: -10rpx;
		width: 40rpx;
		height: 40rpx;
	}

	.border {
		border-left: 1px solid #ececec;
		border-right: 1px solid #ececec;
	}

	.activated {
		color: #1890FF;
	}

	.currentTheme {
		margin-right: 20rpx;
	}
</style>
