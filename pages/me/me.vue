<template>
	<view class="container">
		<u-notify ref="popup"></u-notify>
		<uni-popup ref="popupCode" type="dialog">
			<uni-popup-dialog mode="input" title="请输入两步验证码" placeholder="验证码" :duration="2000" :before-close="false"
				@confirm="popupCodeConfirm"></uni-popup-dialog>
		</uni-popup>
		<view>
		    <u-notice-bar :text="notice" mode="closable"></u-notice-bar>
		</view>
		<view class="view-login block" v-if="!isLogin">
			<image class="view-login-image" src="/static/images/halo.png"></image>
			<view class="login-view">
				<label for="url">站点地址</label>
				<input class="input" id="url" v-model="mUrl" type="text" placeholder="https://" :disabled="disable" />
			</view>

			<view class="login-view">
				<label for="username">用户名/邮箱</label>
				<input class="input" id="username" v-model="username" type="text" placeholder="用户名/邮箱"
					:disabled="disable" />
			</view>

			<view class="login-view">
				<label for="password">密码</label>
				<input class="input" id="password" v-model="password" password="true" placeholder="密码"
					:disabled="disable" />
			</view>
			<button class="button" type="primary" @click="login" id="login" :disabled="disable"
				:loading="disable">登录</button>

		</view>

		<view class="view-me" v-else>
			<view class="view-me-head block">
				<image class="view-me-avatar" :src="avatar" @click="previewAvatar"></image>
				<text class="view-me-text-name">{{ nickname }}</text>
				<text class="view-me-text-description">{{ description }}</text>
				<view class="view-me-view-link">
					<uni-icons type="link" class="view-me-email-icon"></uni-icons>
					<uni-link color="#4368F1" :href="mUrl" :text="mUrl"></uni-link>
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

			<view class="view-me-option block">
				<view class="view-me-option-item item" @click="onOptionClick(0)">
					<image src="/static/images/setting.png"></image>
					<text>博客设置</text>
					<view class="item-sign"></view>
				</view>
				<view class="view-me-option-item item" @click="onOptionClick(1)">
					<image src="/static/images/me.png"></image>
					<text>个人资料</text>
					<view class="item-sign"></view>
				</view>
				<view class="view-me-option-item item" @click="onOptionClick(2)">
					<image src="/static/images/theme.png"></image>
					<text>主题管理</text>
					<view class="item-sign"></view>
				</view>
			</view>

			<view class="view-me-app-option block">
				<view class="view-me-exit item" @click="onOptionClick(3)">
					<image src="/static/images/setting.png"></image>
					<text>设置</text>
					<view class="item-sign"></view>
				</view>
				<view class="view-me-exit item" @click="exit">
					<image src="/static/images/exit.png"></image>
					<text>退出</text>
					<view class="item-sign"></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserProfiles,
		logout
	} from "@/network/UserApi.js";
	export default {
		data() {
			return {
				mUrl: "",
				password: "",
				// 是否禁止编辑框和登录按钮
				disable: false,

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
				description: "",
				
				notice: "请将Halo升级到 1.5.0 以上版本，否则可能出现未知错误。" + 
					"软件目前处于测试阶段，遇到问题请到 Gitee 或 Github 提交 Issues " + 
					"并附上截图或问题复现步骤。",

			}
		},

		mounted: function() {
			// 恢复之前保存的数据
			this.mUrl = this.getUrl();
			this.username = this.getData("username");
			this.password = this.getData("password");

			// 判断个人信息是否过期，没过期就加载个人资料
			if (!this.isExpired()) {
				// token没有过期，更改登录状态，并加载个人信息
				this.isLogin = true
				this.setData("isLogin", "true")
				this.loadAdminInfo()
			} else {
				// token已经过期，留在登录view，并提示用户
				this.isLogin = false
				this.popup('信息过期，请重新登录');
			}
		},

		onShow: function() {
			let that = this
			// 判断个人信息是否过期
			if (this.isExpired()) {
				// token已经过期，更改登录状态
				this.isLogin = false
				setTimeout(function() {
					// 延迟执行popup，防止组件还没加载完成导致此处报错
					that.popup('信息过期，请重新登录');
				}, 500)
			}
		},

		/**
		 * 下拉刷新事件
		 */
		onPullDownRefresh() {
			// 判断个人信息是否过期，没过期就加载个人资料
			if (!this.isExpired()) {
				干饭干饭过多多多多付
				// token没有过期，更改登录状态，并加载个人信息
				this.isLogin = true
				this.setData("isLogin", "true")
				this.loadAdminInfo()
			} else {
				// token已经过期，留在登录view，并提示用户
				this.isLogin = false
				this.popup('信息过期，请重新登录');
				uni.stopPullDownRefresh();
			}
		},

		methods: {
			login: function() {
				if (this.mUrl == "" || this.username == "" || this.password == "") {
					this.popup('请将内容输入完整');
					return;
				}

				// 将信息存储到本地
				this.setData("url", this.mUrl)
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
					url: this.mUrl + "/api/admin/login/precheck",
					header: {
						"Content-Type": "application/json"
					},
					data: json,
					success: function(res) {
						let data = res.data
						that.disable = false
						console.log(res)

						// 状态不是200的话就返回错误信息
						if (res.statusCode != "200") {
							that.popup(res.data.message);
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
					url: this.mUrl + "/api/admin/login",
					header: {
						"Content-Type": "application/json"
					},
					data: json,
					success: function(res) {
						let data = res.data
						that.disable = false

						// 状态不是200的话就返回错误信息
						if (res.statusCode != "200") {
							that.popup(res.data.message);
							return
						}

						if (data.message === "OK") {
							// 登录成功，保存token
							var mData = data.data
							var accessToken = mData.access_token
							console.log(accessToken)
							var expiredIn = mData.expired_in
							var refreshToken = mData.refresh_token
							if (accessToken === "" || expiredIn === "") {
								that.popup("登陆失败，未知错误");
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
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				getUserProfiles().then(data => {
					// 设置个人数据到变量
					if (data.avatar.indexOf("http") != -1) {
						// 检查 avatar 是否是绝对地址
						that.avatar = data.avatar;
					} else {
						that.avatar = that.getUrl() + data.avatar;
					}
					that.username = data.username;
					that.nickname = data.nickname;
					that.email = data.email;
					that.createTime = data.createTime;
					that.updateTime = data.updateTime;
					that.description = data.description;

					// 计算用户已经创建多少天了
					let now = Number(Date.parse(new Date()));
					let createdTime = (now - Number(that.createTime)) / 1000;
					that.createdDay = Math.floor(createdTime / 86400);
					// 停止下拉刷新
					uni.stopPullDownRefresh();
					uni.hideLoading();
					that.popup('欢迎回来', 'success');
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
					uni.showModal({
						title: "获取个人资料失败",
						content: err
					});
				});
			},

			/**
			 * 点击头像预览图片
			 */
			previewAvatar: function() {
				uni.previewImage({
					current: "0",
					urls: [this.avatar]
				})
			},

			/**
			 * 注销或退出
			 */
			exit: function() {
				var accessToken = this.getData("access_token")
				var that = this
				var list = ["注销"]

				// APP有退出程序
				// #ifdef APP-PLUS
				list = ["注销", "退出程序"]
				// #endif

				uni.showActionSheet({
					itemList: list,
					success: function(res) {
						if (res.tapIndex === 0) {
							// 注销，清除会话
							logout().then(data => {
								// 清除本地登录数据
								that.isLogin = false
								that.accessToken = ""
								that.avatar = ""
								that.username = ""
								that.nickname = ""
								that.email = ""
								that.createTime = ""
								that.createdDay = ""
								that.updateTime = ""
								that.description = ""
								that.setData("expired_date", "0")
								that.setData("isLogin", "false")
								that.setData("access_token", "")
							});
						} else {
							// #ifdef APP-PLUS
							if (uni.getSystemInfoSync().platform == 'ios') {
								plus.ios.import("UIApplication").sharedApplication().performSelector(
									"exit")
							} else if (uni.getSystemInfoSync().platform == 'android') {
								plus.runtime.quit();
							}
							// #endif
						}
					}
				})
			},

			/**
			 * 登录后个人页面选项点击事件
			 * @param {Object} i
			 */
			onOptionClick: function(i) {
				switch (i) {
					// 博客设置
					case 0:
						uni.navigateTo({
							url: "../blogSetting/blogSetting"
						})
						break;
						// 个人资料
					case 1:
						uni.navigateTo({
							url: "../userProfile/userProfile"
						})
						break;
						// 主题管理
					case 2:
						uni.navigateTo({
							url: "../themes/themes"
						})
						break;
						// 设置
					case 3:
						uni.navigateTo({
							url: "../setting/setting"
						})
						break;
						// 问题反馈
						// case 4:
						// 	let that = this;
						// 	let url = "https://gitee.com/luodachui/halo-mobile-app/issues/new?issue%5Bassignee_id%5D=0&issue%5Bmilestone_id%5D=0";
						// 	this.openURL(url);
						// 	uni.setClipboardData({
						// 		data: url,
						// 		success: function () {
						// 			that.toast("反馈地址已复制");
						// 		}
						// 	});
						// 	break;
				}
			},


			/**
			 * popup验证码输入Dialog确认事件
			 * @param {Object} res
			 */
			popupCodeConfirm: function(res) {
				let code = res
				// 登录
				this.adminLogin(code)
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
	.container {}

	.login-view {
		margin-bottom: 20rpx;
	}

	.view-login-image {
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



	.view-me {
		position: relative;
	}

	.view-me-head {
		padding-bottom: 20rpx;
	}

	.view-me-avatar {
		position: relative;
		width: 148rpx;
		height: 148rpx;
		border-radius: 99999px;
		top: 30rpx;
		left: calc(50% - 74rpx);
	}

	.view-me-text-name {
		margin-top: 40rpx;
		display: block;
		font-size: 1.3em;
		text-align: center;
	}

	.view-me-text-description {
		margin-top: 5px;
		display: block;
		font-size: .8em;
		text-align: center;
		color: #8C8C8C;
		/* 自动换行 */
		word-break: break-all;
		padding-left: 40rpx;
		padding-right: 40rpx;
	}

	.view-me-view-link {
		margin-top: 5px;
		padding-left: 30rpx;
	}

	.view-me-view-link text {
		margin-left: 5px;
	}

	.view-me-view-email {
		margin-top: 20rpx;
		padding-left: 30rpx;
	}

	.view-me-view-email text {
		margin-left: 5px;
		color: #595959;
		font-size: 0.9em;
	}

	.view-me-view-calendar {
		margin-top: 20rpx;
		margin-bottom: 20rpx;
		padding-left: 30rpx;
	}

	.view-me-view-calendar text {
		margin-left: 5px;
		color: #595959;
		font-size: 0.9em;
	}

	.view-me-app-option {
		margin-bottom: 150rpx;
	}
</style>
