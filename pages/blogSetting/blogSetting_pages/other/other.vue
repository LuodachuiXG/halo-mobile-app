<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">自定义全局 head：</view>
				<textarea class="input" v-model="blog_custom_head" 
					placeholder="放置于每个页面的 <head></head>标签中"></textarea>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">自定义内容页 head：</view>
				<textarea class="input" v-model="blog_custom_content_head" 
					placeholder="仅放置于内容页面的 <head></head>标签中"></textarea>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">统计代码：</view>
				<textarea class="input" v-model="blog_statistics_code" 
					placeholder="第三方网站统计的代码,如:Google Analytics、百度统计、CNZZ 等"></textarea>
			</view>

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				accessToken: "",
				url: "",

				blog_custom_head: "",
				blog_custom_content_head: "",
				blog_statistics_code: "",

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
				
				let array = ["blog_custom_head", "blog_custom_content_head", "blog_statistics_code"]
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/options/map_view/keys",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					data: array,
					success: function(res) {
						uni.stopPullDownRefresh()
						if (res.statusCode !== 200) {
							that.popup("获取数据失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return
						}
						let data = res.data.data
						that.blog_custom_head = data.blog_custom_head
						that.blog_custom_content_head = data.blog_custom_content_head
						that.blog_statistics_code = data.blog_statistics_code
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
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"blog_custom_head": this.blog_custom_head,
					"blog_custom_content_head": this.blog_custom_content_head,
					"blog_statistics_code": this.blog_statistics_code
				}
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/options/map_view/saving",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					data: json,
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("保存失败：" + res.statusCode)
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.popup("保存失败，登录已过期，请重新登陆")
							}
							return
						}
						that.popup("保存成功", "success")
						that.refreshData()
					},
					fail: function(e) {
						uni.showModal({
							title: "保存数据失败",
							content: e.errMsg
						})
					}
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
	.block {
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
