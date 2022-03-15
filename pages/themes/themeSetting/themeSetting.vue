<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				url: "",
				accessToken: "",
				themeId: "",
				
				
				popupType: "",
				popupMessage: ""
			}
		},
		
		// 接收上个页面传过来的主题 ID
		onLoad:function(data){
			this.themeId = data.id;
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
				let that = this
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.url + "/api/admin/themes/" + this.themeId + "/configurations",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						uni.stopPullDownRefresh();
						if (res.statusCode !== 200) {
							that.popup("获取数据失败");
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								});
							}
							return;
						}
			
						
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
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				this.popupMessage = message
				this.popupType = type
				this.$refs.popup.open()
			}
		}
	}
</script>

<style>

</style>
