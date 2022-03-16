<template>
	<view class="content">
		<joMarkdown :nodes="markdownData"></joMarkdown>
	</view>
</template>

<script>
	import joMarkdown from '@/components/jo-markdown/decode.vue';
	import markdownFunc from '@/components/jo-markdown/index.js';
	export default {
		components: {
			joMarkdown
		},
		data() {
			return {
				accessToken: "",
				url: "",
				postUrl: "",

				markdownData: {},
				postId: 0,

			}
		},

		onLoad: function(res) {
			this.postId = res.id;
		},

		mounted() {
			// 获取token和url
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
			
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
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.url + "/api/admin/posts/" + this.postId,
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						console.log(res)
						if (res.statusCode !== 200) {
							that.toast("获取文章失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return;
						}
						
						// 解析 markdown
						that.markdownData = markdownFunc(res.data.data.originalContent, 'markdown');
						that.postUrl = that.url + res.data.data.fullPath;
						uni.stopPullDownRefresh()
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "获取文章失败",
							content: e.errMsg
						})
					}
				})
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
