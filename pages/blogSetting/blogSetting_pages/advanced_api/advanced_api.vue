<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">API服务：</view>
				<switch :checked="api_enabled" @change="switchChange" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">
					<text style="color: red;" v-if="api_enabled">*</text>
					Access Key：
				</view>
				<uni-easyinput type="password" v-model="api_access_key"></uni-easyinput>
			</view>

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				api_enabled: false,
				api_access_key: "",

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
				let array = ["api_enabled", "api_access_key"]
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
						that.api_enabled = data.api_enabled
						that.api_access_key = data.api_access_key
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
			 * APi服务switch改变事件
			 * @param {Object} e
			 */
			switchChange: function(e) {
				this.api_enabled = e.detail.value
			},
			
			
			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"api_enabled": this.api_enabled,
					"api_access_key": this.api_access_key
				};
				let that = this;
				
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
