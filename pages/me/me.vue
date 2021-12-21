<template>
	<view class="container">
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<uni-popup ref="popupCode" type="dialog">
			<uni-popup-dialog mode="input" title="请输入两步验证码" placeholder="验证码" :duration="2000" :before-close="false"
				@confirm="popupCodeConfirm"></uni-popup-dialog>
		</uni-popup>

		<view class="view-login" v-if="!isLogin">
			<image src="/static/images/halo.png"></image>
			<view class="login-view">
				<label for="url">站点地址</label>
				<input id="url" v-model="url" type="text" placeholder="https://域名" :disabled="disable" />
			</view>

			<view class="login-view">
				<label for="username">用户名/邮箱</label>
				<input id="username" v-model="username" type="text" placeholder="用户名/邮箱" :disabled="disable" />
			</view>

			<view class="login-view">
				<label for="username">密码</label>
				<input id="password" v-model="password" password="true" placeholder="密码" :disabled="disable" />
			</view>
			<button type="primary" @click="login" id="login" :disabled="disable" :loading="disable">登录</button>
		</view>

		<view class="view-me" v-else>
			<view class="container">

				<view class="view-me-head">
					<image class="avatar" :src="avatar"></image>
					<text class="view-me-text-name">{{ nickname }}</text>
					<view class="view-me-view-link">
						<uni-icons class="view-me-email-icon" type="link"></uni-icons>
						<uni-link color="#4368F1" :href="url" :text="url"></uni-link>
					</view>
					<view class="view-me-view-email">
						<uni-icons class="view-me-email-icon" type="email"></uni-icons>
						<text>{{ email }}</text>
					</view>
					<view class="view-me-view-calendar">
						<uni-icons class="view-me-email-icon" type="calendar"></uni-icons>
						<text>{{ createdDay }} 天</text>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				url: "",
				username: "",
				password: "",
				// 是否禁止编辑框和登录按钮
				disable: false,
				// popup弹窗类型和信息
				popupType: 'error',
				popupMessage: '',
				// 当前是否是登录状态
				isLogin: false,

				// 个人资料变量
				accessToken: "",
				avatar: "",
				username: "",
				nickname: "",
				email: "",
				createTime: "",
				createdDay: "",
				updateTime: "",
			}
		},

		mounted: function() {
			// 恢复之前保存的数据
			this.url = this.getData("url")
			this.username = this.getData("username")
			this.password = this.getData("password")

			// 判断个人信息是否过期，没过期就加载个人资料
			if (!this.isExpired()) {
				// token没有过期，更改登录状态，并加载个人信息
				this.isLogin = true
				this.setData("isLogin", "true")
				this.loadAdminInfo()
			} else {
				// token已经过期，留在登录view，并提示用户
				this.popup("信息过期，请重新登录", "error")
			}
		},

		onShow: function() {
			// 判断个人信息是否过期
			if (this.isExpired()) {
				// token已经过期，更改登录状态
				this.isLogin = false
				this.popup("信息过期，请重新登录", "error")
			}
		},

		methods: {
			login: function() {
				if (this.url === "" || this.username === "" || this.password === "") {
					this.popup("请将内容输入完整")
					return;
				}

				// 将信息存储到本地
				this.setData("url", this.url)
				this.setData("username", this.username)
				this.setData("password", this.password)

				// 设置input和button禁止状态
				this.disable = true

				// 尝试登录
				this.adminLoginPreCheck()
			},

			/**
			 * api/admin/login/precheck
			 * 判断账号密码是否正确并且是否需要验证码
			 */
			adminLoginPreCheck: function() {
				let json = {}
				let that = this
				json['username'] = this.username
				json['password'] = this.password

				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/login/precheck",
					header: {
						"Content-Type": "application/json"
					},
					data: json,
					success: function(res) {
						let data = res.data
						that.disable = false

						// 状态不是200的话就返回错误信息
						if (res.statusCode != "200") {
							that.popup(res.data.message)
							return
						}

						// 判断是否需要两步验证码
						if (data.data.needMFACode === true) {
							// 需要验证码，打开popup
							that.$refs.popupCode.open()
						} else {
							// 不需要验证码，直接登录
							that.adminLogin()
						}
						this.disable = true
					},
					fail: function(e) {
						that.disable = false
						uni.showModal({
							title: "登陆失败",
							content: e.errMsg,
							showCancel: false
						})
					}
				})
			},


			/**
			 * api/admin/login
			 * 登录并获取token
			 */
			adminLogin: function(code = "") {
				let json = {}
				let that = this
				json['username'] = this.username
				json['password'] = this.password
				if (code !== "") {
					json['authcode'] = code
				}

				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/login",
					header: {
						"Content-Type": "application/json"
					},
					data: json,
					success: function(res) {
						let data = res.data
						that.disable = false

						// 状态不是200的话就返回错误信息
						if (res.statusCode != "200") {
							that.popup(res.data.message)
							return
						}

						if (data.message === "OK") {
							// 登录成功，保存token
							var mData = data.data
							var accessToken = mData.access_token
							var expiredIn = mData.expired_in
							var refreshToken = mData.refresh_token
							if (accessToken === "" || expiredIn === "") {
								that.popup("登陆失败，未知错误")
								return
							}

							// 保存token
							that.setData("access_token", accessToken)
							that.setData("expired_in", expiredIn)
							that.setData("refresh_token", refreshToken)

							// 设置token到期时间戳
							var expiredDate = Number(Date.parse(new Date())) + Number(expiredIn * 1000)
							that.setData("expired_date", expiredDate)
						}

						// 登录成功，更改状态，并加载个人数据
						that.isLogin = true
						that.setData("isLogin", "true")
						that.loadAdminInfo()
					},
					fail: function(e) {
						that.disable = false
						uni.showModal({
							title: "登陆失败",
							content: e.errMsg,
							showCancel: false
						})
					}
				})

			},

			/**
			 * 加载用户信息
			 */
			loadAdminInfo: function() {
				let accessToken = this.getData("access_token")
				let that = this

				// 获取用户资料
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.url + "/api/admin/users/profiles",
					header: {
						"Content-Type": "*/*",
						"ADMIN-Authorization": accessToken
					},
					success: function(res) {
						let data = res.data
						if (res.statusCode !== 200) {
							that.popup("获取个人资料失败\n" + data.message)
							if (data.message === "Token 已过期或不存在") {
								that.isLogin = false
								that.setData("isLogin", "false")
							}
							return
						}

						// 设置个人数据到变量
						that.avatar = that.url + data.data.avatar
						that.username = data.data.username
						that.nickname = data.data.nickname
						that.email = data.data.email
						that.createTime = data.data.createTime
						that.updateTime = data.data.updateTime
						
						// 计算用户已经创建多少天了
						let now = Number(Date.parse(new Date()))
						let createdTime = (now - Number(that.createTime)) / 1000
						that.createdDay = Math.floor(createdTime / 86400)
						
						that.popup("欢迎回来，" + that.nickname, "success")
					},
					fail: function(e) {
						uni.showModal({
							title: "获取个人资料失败",
							content: e.errMsg,
							showCancel: false
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

			/**
			 * popup验证码输入Dialog确认事件
			 * @param {Object} res
			 */
			popupCodeConfirm: function(res) {
				let code = res
				// 登录
				this.adminLogin(code)
			}
		}
	}
</script>

<style>
	.container {
		background-color: #F0F2F5;
	}

	.login-view {
		margin-bottom: 20rpx;
	}

	image {
		display: block;
		width: 128rpx;
		height: 128rpx;
		margin: 0px auto;
		margin-top: 40rpx;
		margin-bottom: 40rpx;
	}

	button {
		margin-top: 40rpx;
	}

	.view-login {
		padding: 20px;
		font-size: 14px;
		line-height: 24px;
	}

	.view-me {}

	.view-me-head {
		background-color: #FFFFFF;
		height: 450rpx;
		position: relative;
		margin: 14px;
		box-shadow: 0 0 5px rgba(216, 216, 216, .5);
		border-radius: 6px;
	}

	.view-me-text-name {
		position: relative;
		top: 220rpx;
		display: block;
		font-size: 1.3em;
		text-align: center;
		padding-left: 0rpx;
	}

	.view-me-view-link {
		position: relative;
		top: 240rpx;
	}

	.view-me-view-link text {
		margin-left: 20rpx;
	}

	.view-me-view-email {
		position: relative;
		top: 250rpx;
	}

	.view-me-view-email text {
		margin-left: 20rpx;
		color: #595959;
		font-size: 0.9em;
	}
	
	.view-me-view-calendar {
		position: relative;
		top: 260rpx;
	}
	
	.view-me-view-calendar text {
		margin-left: 20rpx;
		color: #595959;
		font-size: 0.9em;
	}

	.avatar {
		position: absolute;
		width: 148rpx;
		height: 148rpx;
		border-radius: 99999px;
		top: 10rpx;
		left: calc(50% - 74rpx);
	}
</style>
