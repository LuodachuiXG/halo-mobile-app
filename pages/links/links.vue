<template>
	<view class="container">
		<u-notify ref="popup" duration="1500"></u-notify>

		<u-empty v-if="links.length === 0" mode="list" icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>


		<view class="block" v-for="(link, i) in links">
			<view @click="onBlockClick(i)">
				<!-- 链接名称 -->
				<view class="block-name-view">
					<view class="block-name">
						<text>{{ link.name }}</text>
					</view>
				</view>

				<uni-row>
					<uni-col :span="10">
						<view class="link-logo">
							<image :src="link.logo"></image>
						</view>
					</uni-col>


					<uni-col :span="14" :pull="1">
						<!-- 链接 -->
						<view class="block-content">
							<view class="block-content-view">
								<u--text :lines="1" :text="'描述：' + link.description"></u--text>
							</view>
							<view class="block-content-view">
								<u--text :lines="1" :text="'分组：' + ((link.team === '') ? '无' : link.team)"></u--text>
							</view>
							<view class="block-content-view">
								<u--text :lines="1" :text="'排序：' + link.priority"></u--text>
							</view>
							<view class="block-content-view">
								<u--text :lines="1" :text="'链接：' + link.url"></u--text>
							</view>
						</view>
					</uni-col>

				</uni-row>
			</view>


			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>
					<uni-col :span="12">
						<view @click="onEditClick(i)">
							<view class="block-action-item">
								<text class="iconfont item-icon">&#xe892;</text>
								<text>
									编辑
								</text>
							</view>
						</view>
					</uni-col>


					<uni-col :span="12">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<text class="iconfont item-icon">&#xe74e;</text>
							<text>
								删除
							</text>
						</view>
					</uni-col>

				</uni-row>
			</view>
		</view>

		<!-- 悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onFabClick">
		</uni-fab>

	</view>
</template>

<script>
	import {
		getLinks,
		deleteLink
	} from "@/network/LinkApi.js";
	export default {
		data() {
			return {
				// 存放链接信息
				links: [],
			}
		},

		mounted() {

		},

		onShow() {
			this.refreshData();
		},

		// 下拉刷新事件
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
				let that = this;
				// 获取链接数据
				getLinks().then(data => {
					// 保存链接数组
					that.links = data;

					uni.hideLoading();
					uni.stopPullDownRefresh();
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
			 * 编辑链接
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				uni.navigateTo({
					url: "./edit/edit?type=update&i=" + i
				});
			},


			/**
			 * 链接删除单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				let link = this.links[i];
				uni.showModal({
					title: '提示',
					content: '确定要删除此链接吗。',
					success: function(res) {
						if (res.confirm) {
							deleteLink(link.id).then(data => {
								that.popup("删除成功", "success");
								that.refreshData();
							}).catch(err => {
								uni.showModal({
									title: "删除失败",
									content: err
								});
							});
						}
					}
				});
			},
			
			/**
			 * 链接点击事件
			 * @param {Object} i
			 */
			onBlockClick: function(i) {
				// 跳转链接
				this.openURL(this.links[i].url);
			},


			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function() {
				// 写日志
				uni.navigateTo({
					url: "./edit/edit?type=add"
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
	.container {
		padding-bottom: 40px;
	}
	.block {
		position: relative;
		margin-bottom: 10px;
	}

	.block-name-view {
		padding: 20rpx;
		padding-bottom: 10rpx;
		padding-left: 30rpx;
		border-bottom: 1px solid #ececec;
	}

	.block-name {
		font-weight: bold;
		font-size: 1.1em;
		display: inline-block;
		white-space: nowrap;
		width: 100%;
		overflow: hidden;
		text-overflow: ellipsis;
	}


	.block-content {
		padding: 30rpx;
		padding-bottom: 10rpx;
		padding-top: 20rpx;
	}

	
	.block-content-view {
		margin-top: 10px;
	}
	
	.block-content-view:first-child {
		margin-top: 0px;
	}

	.block-action {
		margin-top: 20rpx;
		border-top: 1px solid #ececec;
		color: #898989;
	}
	
	.block-action text {
		color: #898989;
	}

	.block-action-item {
		position: relative;
		padding: 20rpx;
		text-align: center;
	}

	.block-action-item:active {
		background-color: var(--activatedColor);
	}

	.block-action-icon {
		font-size: 40rpx;
		position: relative;
		top: 5rpx;
	}

	.border {
		border-left: 1px solid #ececec;
	}

	.link-logo {
		/* background-color: red; */
		text-align: center;
		margin-top: 15px;
	}

	.link-logo image {
		width: 110px;
		height: 110px;
		border-radius: 99px;
		border: 1px solid var(--activatedColor);
	}
	
	.block-content-row {
		margin-top: 5px;
	}
	
</style>
