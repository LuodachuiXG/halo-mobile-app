<template>
	<view class="content">
		<joMarkdown :nodes="markdownData"></joMarkdown>
	</view>
</template>

<script>
	import {
		getPost,
	} from "../../../common/api.js";
	import joMarkdown from '@/components/jo-markdown/decode.vue';
	import markdownFunc from '@/components/jo-markdown/index.js';
	export default {
		components: {
			joMarkdown
		},
		data() {
			return {
				postUrl: "",

				markdownData: {},
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
		
		onBrowserClick: function() {
			console.log(1)
		},

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let that = this;
				getPost(this.postId).then(data => {
					// 解析 markdown
					that.markdownData = markdownFunc(data.originalContent, 'markdown');
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
	/* 解析 md */
	@import '../../../components/jo-markdown/main.css';
	
	.content {
		padding: 30rpx;
	}
</style>
