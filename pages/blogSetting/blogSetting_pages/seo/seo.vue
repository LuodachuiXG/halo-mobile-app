<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">屏蔽搜索引擎：</view>
				<switch :checked="seo_spider_disabled" @change="switchChange" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">关键词：</view>
				<input class="input" type="text" v-model="seo_keywords" placeholder="多个关键词以英文状态下的逗号隔开" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">博客描述：</view>
				<textarea class="input" v-model="seo_description"></textarea>
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

				seo_spider_disabled: "",
				seo_keywords: "",
				seo_description: "",

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
				let array = ["seo_spider_disabled", "seo_keywords", "seo_description"]
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
						that.seo_spider_disabled = data.seo_spider_disabled
						that.seo_keywords = data.seo_keywords
						that.seo_description = data.seo_description
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "获取数据失败",
							content: e.message
						})
					}
				})
			},
			
			/**
			 * 屏蔽搜索引擎switch改变事件
			 * @param {Object} e
			 */
			switchChange: function(e) {
				this.seo_spider_disabled = e.detail.value
			},
			
			
			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"seo_spider_disabled": this.seo_spider_disabled,
					"seo_keywords": this.seo_keywords,
					"seo_description": this.seo_description
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
							content: e.message
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
