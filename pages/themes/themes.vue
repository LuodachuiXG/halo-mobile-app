<template>
	<view class="main">
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block" v-for="(theme, i) in themes">
			<view class="block-name">
				{{ theme.name }}
				<view class="currentTheme tips-info" v-if="theme.activated">当前启用</view>
			</view>
			<view class="block-screenshots">
				<image :src="url + theme.screenshots"></image>
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
	export default {
		data() {
			return {
				url: "",
				accessToken: "",
				themes: null,

				popupType: "",
				popupMessage: ""
			}
		},
		mounted() {
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
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
				let that = this
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.url + "/api/admin/themes",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						uni.stopPullDownRefresh();
						if (res.statusCode !== 200) {
							that.popup("获取数据失败");
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								});
							}
							return;
						}

						that.themes = res.data.data;
						// 将已经启用的主题排在第一个
						for (var i = 0; i < that.themes.length; i++) {
							if (that.themes[i].activated) {
								let current = that.themes[i];
								that.themes[i] = that.themes[0];
								that.themes[0] = current;
							}
						}
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "获取数据失败",
							content: e.errMsg
						})
					}
				})
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
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/themes/" + clickThemeId + "/activation",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("启用主题失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								});
							}
							return;
						}
						console.log(res)
						that.popup("启用主题成功", "success");
						that.refreshData();

					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "启用主题失败",
							content: e.errMsg
						})
					}
				})
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
									content: '确定要删除 ' + that.themes[i].name + ' 吗？',
									success: function(res) {
										if (res.confirm) {
											uni.request({
												method: "DELETE",
												dataType: "json",
												url: that.url + "/api/admin/themes/" + 
													that.themes[i].id + "?deleteSettings=true",
												header: {
													"Content-Type": "application/json",
													"ADMIN-Authorization": that.accessToken
												},
												success: function(res) {											if (res.statusCode !== 200) {
														that.popup("删除失败")
														// 登录过期
														if (that
															.isExpiredByRequest(res)) {
															that.setData("isLogin","false")
															uni.reLaunch({
																url: "../../me/me"
															});
														}
														return;
													}
													that.popup("删除成功", "success");
													that.refreshData();
												},
												fail: function(e) {
													uni.stopPullDownRefresh()
													uni.showModal({
														title: "删除主题失败",
														content: e.errMsg
													})
												}
											})
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
				this.popup("暂不支持主题设置，请前往 Web 端设置")
			},

			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				this.popupMessage = message
				this.popupType = type
				this.$refs.popup.open()
			}
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
		margin-left: 20rpx;
	}
</style>
