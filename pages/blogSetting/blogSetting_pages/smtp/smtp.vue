<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<uni-segmented-control :current="current" :values="items" @clickItem="onSegmentedClickItem" styleType="button"
			activeColor="#007AFF" class="segmented"></uni-segmented-control>
		<view class="content">
			<view v-show="current === 0">
				<view class="block">
					<view class="view-input">
						<view class="view-input-titleView">是否启用：</view>
						<switch :checked="email_enabled" @change="emailEnabledChange" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							SMTP 地址：
						</view>
						<input class="input" type="text" v-model="email_host" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							发送协议：
						</view>
						<input class="input" type="text" v-model="email_protocol" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							SSL 端口：
						</view>
						<input class="input" type="text" v-model="email_ssl_port" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							邮箱账号：
						</view>
						<input class="input" type="text" v-model="email_username" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							邮箱密码：
						</view>
						<input class="input" type="text" password v-model="email_password" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;" v-if="email_enabled">*</text>
							发件人：
						</view>
						<input class="input" type="text" v-model="email_from_name" />
					</view>

					<button class="button save-button" type="primary" @click="saving">保存</button>
				</view>
			</view>
			<view v-show="current === 1">
				<view class="block">
					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;">*</text>
							收件人地址：
						</view>
						<input class="input" type="text" v-model="to" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;">*</text>
							主题：
						</view>
						<input class="input" type="text" v-model="subject" />
					</view>

					<view class="view-input">
						<view class="view-input-titleView">
							<text style="color: red;">*</text>
							内容：
						</view>
						<textarea class="input" v-model="content"></textarea>
					</view>
					<button class="button save-button" type="primary" @click="sendEmail">发送</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getOptionsByMapViewsKeys,
		updateOptionsByMapViews
	} from "@/network/SettingApi.js";
	import {
		sendMailTest
	} from "@/network/OtherApi.js";
	export default {
		data() {
			return {
				email_enabled: true,
				email_host: "",
				email_protocol: "",
				email_ssl_port: "",
				email_username: "",
				email_password: "",
				email_from_name: "",
				
				to: "",
				subject: "",
				content: "",

				// 分段器参数
				current: 0,
				items: ["发信设置", "发送测试"],


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
				let array = ["email_enabled", "email_host", "email_protocol", "email_ssl_port",
					"email_username", "email_password", "email_from_name"];
				let that = this;
				
				getOptionsByMapViewsKeys(array).then(data => {
					that.email_enabled = data.email_enabled
					that.email_host = data.email_host
					that.email_protocol = data.email_protocol
					that.email_ssl_port = data.email_ssl_port
					that.email_username = data.email_username
					that.email_password = data.email_password
					that.email_from_name = data.email_from_name
					uni.stopPullDownRefresh();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},

			/**
			 * SMTP是否启用switch改变事件
			 * @param {Object} e
			 */
			emailEnabledChange: function(e) {
				this.email_enabled = e.detail.value
			},


			/**
			 * 保存按钮事件
			 */
			saving: function() {
				if (this.email_enabled) {
					if (this.email_host.length <= 0) {
						this.popup("SMTP 地址不能为空");
						return
					}
					if (this.email_protocol.length <= 0) {
						this.popup("发送协议不能为空");
						return
					}
					if (this.email_ssl_port.length <= 0) {
						this.popup("SSL 端口不能为空");
						return
					}
					if (this.email_username.length <= 0) {
						this.popup("邮箱账号不能为空");
						return
					}
					if (this.email_password.length <= 0) {
						this.popup("邮箱密码不能为空");
						return
					}
					if (this.email_from_name.length <= 0) {
						this.popup("发件人不能为空");
						return
					}
				};
				let json = {
					"email_enabled": this.email_enabled,
					"email_host": this.email_host,
					"email_protocol": this.email_protocol,
					"email_ssl_port": this.email_ssl_port,
					"email_username": this.email_username,
					"email_password": this.email_password,
					"email_from_name": this.email_from_name
				};
				let that = this;
				
				updateOptionsByMapViews(json).then(data => {
					that.popup("保存成功", "success");
					that.refreshData();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "保存数据失败",
						content: err
					});
				});
			},
			
			
			/**
			 * 发送邮件按钮事件
			 */
			sendEmail: function() {
				let that = this;
				if (this.to.length <= 0 || this.subject.length <=0 || this.content.length <= 0) {
					this.popup("请将内容填写完整");
					return ;
				}
				
				sendMailTest(this.to, this.subject, this.content).then(data => {
					that.popup("发送成功", "success");
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "发送邮件失败",
						content: err
					});
				});
			},

			/**
			 * 页面分段器选择事件
			 * @param {Object} index
			 */
			onSegmentedClickItem: function(e) {
				this.current = e.currentIndex
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

	.segmented {
		margin: 30rpx;
	}
</style>
