<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>博客标题：</view>
				<input class="input" type="text" v-model="blog_title" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>博客地址：</view>
				<input class="input" type="text" v-model="blog_url" placeholder="如:https://halo.run" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">Logo：</view>
				<view class="right-button-input">
					<input class="input" type="text" v-model="blog_logo" />
					<image src="/static/images/picture.png" @click="selectAttachment('blog_logo')"></image>
				</view>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">Favicon：</view>
				<view class="right-button-input">
					<input class="input" type="text" v-model="blog_favicon" />
					<image src="/static/images/picture.png" @click="selectAttachment('blog_favicon')"></image>
				</view>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">页脚消息：</view>
				<textarea class="input" v-model="blog_footer_info" placeholder="支持HTML格式的文本"></textarea>
			</view>

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				blog_title: "",
				blog_url: "",
				blog_logo: "",
				blog_favicon: "",
				blog_footer_info: "",

				popupType: "",
				popupMessage: ""
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
				let array = ["blog_title", "blog_url", "blog_logo", "blog_favicon", "blog_footer_info"]
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.getUrl() + "/api/admin/options/map_view/keys",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
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
						that.blog_title = data.blog_title
						that.blog_url = data.blog_url
						that.blog_logo = data.blog_logo
						that.blog_favicon = data.blog_favicon
						that.blog_footer_info = data.blog_footer_info
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
			 * 打开新窗口选择附件
			 * @param {Object} attrName 当前页面的变量，传递给附件选择页，用于选择附件后修改的变量
			 */
			selectAttachment: function(attrName) {
				uni.navigateTo({
					url: "../../../attachment/selectAttachment/selectAttachment?attrName=" + attrName
				})
			},
			
			/**
			 * 保存按钮事件
			 */
			saving: function() {
				if (this.blog_title.length <= 0 || this.blog_url <= 0) {
					this.popup("请将必填内容填写完整")
					return 
				}
				
				let json = {
					"blog_title": this.blog_title,
					"blog_url": this.blog_url,
					"blog_logo": this.blog_logo,
					"blog_favicon": this.blog_favicon,
					"blog_footer_info": this.blog_footer_info
				}
				let that = this
				
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.getUrl() + "/api/admin/options/map_view/saving",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
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
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
