<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">创建时间：{{this.format(createTime)}}</view>
				<view class="view-input-titleView">修改时间：{{this.format(updateTime)}}</view>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>用户名：</view>
				<input class="input" type="text" v-model="username"/>
			</view>
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>昵称：</view>
				<input class="input" type="text" v-model="nickname"/>
			</view>
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>电子邮箱：</view>
				<input class="input" type="text" v-model="email"/>
				<text class="view-input-text">* 登录后台系统的邮箱</text>
			</view>
			<view class="view-input">
				<view class="view-input-titleView">个人说明：</view>
				<textarea class="input" v-model="description"></textarea>
			</view>

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				username: "",
				nickname: "",
				email: "",
				description: "",
				createTime: "",
				updateTime: "",
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
				let that = this
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.getUrl() + "/api/admin/statistics/user",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
					},
					success: function(res) {
						uni.stopPullDownRefresh()
						if (res.statusCode !== 200) {
							that.popup('获取数据失败');
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return
						}
			
						let user = res.data.data.user;
						that.username = user.username;
						that.nickname = user.nickname;
						that.email = user.email;
						that.description = user.description;
						that.createTime = user.createTime;
						that.updateTime = user.updateTime;
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
				if (this.username.length <= 0 || this.nickname.length <= 0 || this.email.length <= 0) {
					this.popup('请将必填项填写完整');
					return ;
				}
				let json = {
					"username": this.username,
					"nickname": this.nickname,
					"email": this.email,
					"description": this.description
				}
				let that = this
				uni.request({
					method: "PUT",
					dataType: "json",
					url: this.getUrl() + "/api/admin/users/profiles",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
					},
					data: json,
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup('保存失败');
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.popup('保存失败，登录已过期，请重新登陆');
							}
							return
						}
						that.popup('保存成功', 'success');
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
