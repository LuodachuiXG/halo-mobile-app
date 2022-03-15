<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>

		<view class="block" v-for="(category, i) in categories">
			<!-- 分类目录名 -->
			<view class="block-name">
				<text>{{ category.name }}</text>
				<text>{{ category.slug }}</text>
			</view>

			<!-- 缩略图 -->
			<view class="block-thumbnail">
				<!-- 判断缩略图是否是绝对地址 -->
				<image v-if="category.thumbnail" :src="category.thumbnail.indexOf('http') < 0  ? 
					url + category.thumbnail : category.thumbnail"></image>
			</view>

			<!-- 分类目录的一些信息 -->
			<view class="block-data"
				:style="category.thumbnail == '' ? 'border-top:none' : 'border-top: 1px solid #ececec;'">
				<view class="block-tag">
					<uni-tag class="block-tag-item" type="primary" :text="'文章数：' + category.postCount"></uni-tag>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>
					<uni-col :span="12">
						<view class="block-action-item" @click="onEditClick(i)">
							<image src="../../static/images/edit.png"
								style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;">
							</image>
							编辑
						</view>
					</uni-col>
					<uni-col :span="12">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<image src="../../static/images/trash.png"
								style="width: 35rpx; height: 35rpx;top: 8rpx;margin-top: -8rpx;"></image>
							删除
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onAddCategoryClick"></uni-fab>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				accessToken: "",
				url: "",
				categories: [],

				popupType: "",
				popupMessage: "",
			}
		},

		mounted() {
			// 获取token和url
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
		},

		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData()
		},
		
		onShow() {
			let that = this;
			// 延迟 100 防止 URL 还未读取成功就刷新数据
			setTimeout(function() {
				that.refreshData()
			}, 100)
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
					url: this.url + "/api/admin/categories?more=true",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("获取数据失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return;
						}

						that.categories = res.data.data;

						uni.stopPullDownRefresh();
					},
					fail: function(e) {
						uni.stopPullDownRefresh();
						uni.showModal({
							title: "获取数据失败",
							content: e.errMsg
						});
					}
				})
			},

			/**
			 * 编辑分类目录单击事件
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				let that = this;
				uni.navigateTo({
					url: './updateCategory/updateCategory?type=update&id=' + that.categories[i].id + 
						"&index=" + i
				})
			},

			/**
			 * 删除分类目录单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要删除【' + this.categories[i].name + '】分类吗？',
					success: function(res) {
						if (res.confirm) {
							uni.request({
								method: "DELETE",
								dataType: "json",
								url: that.url + 
									"/api/admin/categories/" + that.categories[i].id,
								header: {
									"Content-Type": "application/json",
									"ADMIN-Authorization": that.accessToken
								},
								success: function(res) {
									if (res.statusCode !== 200) {
										that.popup("删除失败")
										// 登录过期
										if (that.isExpiredByRequest(res)) {
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
										title: "删除失败",
										content: e.errMsg
									})
								}
							})
						}
					}
				});
			},
			
			/**
			 * 添加分类目录单击事件
			 */
			onAddCategoryClick: function() {
				uni.navigateTo({
					url: './updateCategory/updateCategory?type=add'
				})
			},


			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				this.popupMessage = message
				this.popupType = type
				this.$refs.popup.open()
			},
		}
	}
</script>

<style>
	.block-name {
		padding: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #ececec;
	}

	.block-name text:first-child {
		font-weight: bold;
		font-size: 1.1em;
	}

	.block-name text:last-child {
		font-weight: bold;
		font-size: .8em;
		margin-left: 20rpx;
		color: var(--textPrimaryColor);
	}

	.block-thumbnail {}

	.block-thumbnail image {
		vertical-align: middle;
		width: 100%;
	}

	.block-data {
		padding-top: 20rpx;
	}

	.block-tag {
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 20rpx;
	}

	.block-tag-item {
		margin-left: 20rpx;
		font-weight: bold;
	}

	.block-tag-item:first-child {
		margin-left: 0rpx;
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
	}
</style>
