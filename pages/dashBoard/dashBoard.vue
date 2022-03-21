<template>
	<view class="container">
		<u-notify ref="popup"></u-notify>

		<uni-collapse accordion class="uni-collapse">
			<uni-collapse-item title="总览" open>
				<view class="view-block">
					<uni-row class="row">
						<uni-col :span="10">
							<view class="view-block-view" style="width: 100%;" @click="onClick(0)">
								<text class="view-block-title">文章</text>
								<uni-icons class="view-block-icon" type="plus" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="postCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
						<uni-col :span="10" :push="2" @click="onClick(1)">
							<view class="view-block-view">
								<text class="view-block-title">评论</text>
								<uni-icons class="view-block-icon" type="list" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="commentCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
					</uni-row>
					
					<uni-row class="row">
						<uni-col :span="10">
							<view class="view-block-view" @click="onClick(2)">
								<text class="view-block-title">分类</text>
								<uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="categoryCount"
									color="#000000" fontSize="2em"></u-count-to>
							</view>
						</uni-col>
						<uni-col :span="10" :push="2">
							<view class="view-block-view" @click="onClick(3)">
								<text class="view-block-title">标签</text>
								<uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="tagCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
					</uni-row>
					
					<uni-row class="row">
						<uni-col :span="10">
							<view class="view-block-view">
								<text class="view-block-title" @click="onClick(4)">日志</text>
								<uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="journalCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
						<uni-col :span="10" :push="2">
							<view class="view-block-view">
								<text class="view-block-title" @click="onClick(5)">链接</text>
								<uni-icons class="view-block-icon" type="link" color="#2F54EB" size="20"></uni-icons>
								<u-count-to class="view-block-text" :startVal="0" :endVal="linkCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
						
					</uni-row>

					<uni-row class="row">
						<uni-col :span="10">
							<view class="view-block-view">
								<text class="view-block-title">阅读量</text>
								<!-- <uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons> -->
								<u-count-to class="view-block-text" :startVal="0" :endVal="visitCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
						<uni-col :span="10" :push="2">
							<view class="view-block-view">
								<text class="view-block-title">建立天数</text>
								<!-- <uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons> -->
								<u-count-to class="view-block-text" :startVal="0" :endVal="establishDays"
									color="#000000" fontSize="2em"></u-count-to>
							</view>
						</uni-col>
					</uni-row>
					
					

					<uni-row class="row">
						<uni-col :span="10">
							<view class="view-block-view">
								<text class="view-block-title">点赞</text>
								<!-- <uni-icons class="view-block-icon" type="info" color="#2F54EB" size="20"></uni-icons> -->
								<u-count-to class="view-block-text" :startVal="0" :endVal="likeCount" color="#000000"
									fontSize="2em"></u-count-to>
							</view>
						</uni-col>
					</uni-row>
				</view>
			</uni-collapse-item>
			<uni-collapse-item title="最近文章">
				<view class="view-latestPost">
					<view class="view-latestPost-block" v-for="(post, i) in posts" @click="selectPost(i)">
						<uni-row>
							<uni-col :span="15">
								<text>{{ post.title }}</text>
							</uni-col>
							<uni-col :span="9" :push="0" style="text-align: right;font-size: .9em;">
								<text>{{ format(Number(post.createTime)) }}</text>
							</uni-col>
						</uni-row>
					</view>
				</view>
			</uni-collapse-item>
		</uni-collapse>
	</view>
</template>

<script>
	import {
		getBlogStatistics,
		getLatestPosts
	} from "../../common/api.js";
	export default {
		data() {
			return {
				// popup弹窗类型和信息
				popupType: "error",
				popupMessage: "",

				// 博客统计信息变量
				postCount: "",
				commentCount: "",
				categoryCount: "",
				tagCount: "",
				journalCount: "",
				// birthday: "",
				establishDays: "",
				linkCount: "",
				visitCount: "",
				likeCount: "",

				// 最近文章变量
				posts: {},

			}
		},

		onShow: function() {
			// 如果未登录或过期就直接跳转到登录页
			if (this.isExpired()) {
				this.toast("请先登录")
				uni.switchTab({
					url: "/pages/me/me"
				})
				return
			}

			// 加载博客统计信息
			this.loadBlogStatistics()
			// 加载最近文章
			this.loadLatestPosts()
		},

		/**
		 * 下拉刷新事件
		 */
		onPullDownRefresh() {
			// 加载博客统计信息
			this.loadBlogStatistics()
			// 加载最近文章
			this.loadLatestPosts()
		},

		methods: {
			/**
			 * 加载博客统计信息
			 */
			loadBlogStatistics: function() {
				let that = this;
				getBlogStatistics().then(data => {
					that.postCount = data.postCount
					that.commentCount = data.commentCount
					that.categoryCount = data.categoryCount
					that.tagCount = data.tagCount
					that.journalCount = data.journalCount
					that.establishDays = data.establishDays
					that.linkCount = data.linkCount
					that.visitCount = data.visitCount
					that.likeCount = data.likeCount
					// 停止下拉刷新
					uni.stopPullDownRefresh()
				}).catch(err => {
					uni.showModal({
						title: "获取仪表盘数据失败",
						content: err,
						showCancel: false
					});
					// 停止下拉刷新
					uni.stopPullDownRefresh()
				})
			},

			/**
			 * 加载最近10篇文章
			 */
			loadLatestPosts: function() {
				let that = this;
				getLatestPosts("10").then(data => {
					that.posts = data;
					// 停止下拉刷新
					uni.stopPullDownRefresh()
				}).catch(err => {
					uni.showModal({
						title: "获取仪表盘数据失败",
						content: err,
						showCancel: false
					});
					// 停止下拉刷新
					uni.stopPullDownRefresh()
				})
			},
		

			/**
			 * 最近文章中的每个文章的点击事件，打开网址
			 * @param {Object} i
			 */
			selectPost: function(i) {
				let url = this.getUrl() + this.posts[i].fullPath
				this.openURL(url)
			},

			/**
			 * 总览中方块点击事件
			 * @param {Object} i
			 */
			onClick: function(i) {
				switch (i) {
					// 文章
					case 0:
						uni.navigateTo({
							url: '../posts/posts'
						});
						break;
						// 分类
					case 2:
						uni.navigateTo({
							url: '../categories/categories'
						});
						break;

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

			a: function() {
				return 2
			}
		}
	}
</script>

<style>
	.container {
		background-color: #F0F2F5;
		padding: 30rpx;
	}

	.uni-collapse {
		margin-bottom: 150rpx;
	}

	.view-block {
		width: 100%;
		display: block;
		margin-top: 10rpx;
		margin-bottom: 20rpx;
		padding: 10rpx;
	}


	.view-block-view {
		background-color: #FFFFFF;
		width: 100%;
		box-shadow: 1px 1px 10px rgba(216, 216, 216, .6);
		padding: 20rpx;
		position: relative;
	}

	.view-block-title {
		display: block;
		margin-top: 20rpx;
		margin-left: 20rpx;
		color: #8C8C8C;
	}


	.view-block-icon {
		position: absolute;
		top: 38rpx;
		right: 30rpx;
	}

	.view-block-text {
		margin-left: 20rpx;
		margin-top: 20rpx;
		margin-bottom: 10rpx;
	}

	.row {
		margin-bottom: 20rpx;
	}

	.view-latestPost {
		position: relative;
	}

	.view-latestPost-title {
		font-size: 1.2em;
	}

	.view-latestPost-block {
		margin-left: 10rpx;
		margin-right: 10rpx;
		padding: 20rpx;
		padding-top: 30rpx;
		padding-bottom: 30rpx;
		border-bottom: 1px solid #eeeeee;
	}

	.view-latestPost-block:last-child {
		border-bottom: none;
	}

	.view-latestPost-block:active {
		background-color: #e9ebec;
	}
</style>
