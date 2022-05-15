<template>
	<view class="content">
		<u-parse :content="post.formatContent" :selectable="true"></u-parse>
	</view>
</template>

<script>
	import {
		getPost,
	} from "@/network/PostApi.js";
	export default {
		data() {
			return {
				postUrl: "",
				post: [],
				postId: 0,

			}
		},

		onLoad: function(res) {
			this.postId = res.id;
		},

		mounted() {
			this.refreshData()
		},

		onNavigationBarButtonTap: function(e) {
			if (e.index == 0) {
				// 单击小地球，在浏览器打开文章
				this.openURL(this.postUrl);
			}
		},
		

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let that = this;
				getPost(this.postId).then(data => {
					// 解析 markdown
					that.post = data;
					// that.markdownData = markdownFunc(data.originalContent, 'markdown');
					that.postUrl = that.getUrl() + data.fullPath;
					uni.stopPullDownRefresh()
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "获取文章失败",
						content: err
					});
				});
			},
			
			
		}
	}
</script>

<style>
	.content {
		padding: 40rpx;
	}
</style>
