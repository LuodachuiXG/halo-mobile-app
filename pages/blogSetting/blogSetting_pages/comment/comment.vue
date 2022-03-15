<template>
	<view class="container">
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">评论者头像：</view>
				<view class="input">
					<picker @change="commentGravatarDefaultChange" :value="comment_gravatar_defaultIndex" :range="comment_gravatar_defaultText">
						<view>{{comment_gravatar_defaultText[comment_gravatar_defaultIndex]}}</view>
					</picker>
				</view>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">评论审核后才显示：</view>
				<switch :checked="comment_new_need_check" @change="commentNewNeedCheckSwitchChange" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">新评论通知：</view>
				<switch :checked="comment_new_notice" @change="commentNewNoticeSwitchChange" />
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">评论回复通知对方：</view>
				<switch :checked="comment_reply_notice" @change="commentReplyNoticeSwitchChange" />
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">API评论开关：</view>
				<switch :checked="comment_api_enabled" @change="commentApiEnabledSwitchChange" /><br>
				<text class="view-input-text">* 关闭之后将无法进行评论</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">评论模块JS：</view>
				<textarea class="input" v-model="comment_internal_plugin_js"></textarea>
				<text class="view-input-text">* 该设置需要主题支持</text>
			</view>
			
			
			<view class="view-input">
				<view class="view-input-titleView">Gravatar镜像源：</view>
				<input class="input" type="text" v-model="gravatar_source" />
				<text class="view-input-text">* 例如：//gravatar.com/avatar/</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">每页显示条数：</view>
				<input class="input" type="number" v-model="comment_page_size" />
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">占位提示：</view>
				<input class="input" type="text" v-model="comment_content_placeholder" />
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
				
				comment_gravatar_default: "",
				comment_gravatar_defaultText: ["默认", "匿名者", "抽象几何图形", "小怪物", "Wavatar", "复古", "机器人", "不显示头像"],
				comment_gravatar_defaultValue: ["", "mm", "identicon", "monsterid", "wavatar", "retro", "robohash", "blank"],
				comment_gravatar_defaultIndex: 0,
				
				comment_new_need_check: true,
				comment_new_notice: true,
				comment_reply_notice: true,
				comment_api_enabled: true,
				comment_internal_plugin_js: "",
				gravatar_source: "",
				comment_page_size: "",
				comment_content_placeholder: "",

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
				let array = ["comment_gravatar_default", "comment_new_need_check", "comment_new_notice",
					"comment_reply_notice", "comment_api_enabled", "comment_internal_plugin_js",
					"gravatar_source", "comment_page_size", "comment_content_placeholder"
				]
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
						
						that.comment_gravatar_default = data.comment_gravatar_default
						// 把服务端的数据和comment_gravatar_default比对，获得对应的选项索引
						that.comment_gravatar_defaultIndex = 
							that.comment_gravatar_defaultValue.indexOf(that.comment_gravatar_default)
						that.comment_new_need_check = data.comment_new_need_check
						that.comment_new_notice = data.comment_new_notice
						that.comment_reply_notice = data.comment_reply_notice
						that.comment_api_enabled = data.comment_api_enabled
						that.comment_internal_plugin_js = data.comment_internal_plugin_js
						that.gravatar_source = data.gravatar_source
						that.comment_page_size = data.comment_page_size
						that.comment_content_placeholder = data.comment_content_placeholder
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
			 * 评论者头像选项更改事件
			 * @param {Object} e
			 */
			commentGravatarDefaultChange: function(e) {
				this.comment_gravatar_defaultIndex = e.detail.value
			},
			
			/**
			 * 评论审核后才显示switch改变事件
			 * @param {Object} e
			 */
			commentNewNeedCheckSwitchChange: function(e) {
				this.comment_new_need_check = e.detail.value
			},
			
			
			/**
			 * 新评论通知switch改变事件
			 * @param {Object} e
			 */
			commentNewNoticeSwitchChange: function(e) {
				this.comment_new_notice = e.detail.value
			},

			/**
			 * 评论回复通知对方switch改变事件
			 * @param {Object} e
			 */
			commentReplyNoticeSwitchChange: function(e) {
				this.comment_reply_notice = e.detail.value
			},
			
			/**
			 * API评论开关switch改变事件
			 * @param {Object} e
			 */
			commentApiEnabledSwitchChange: function(e) {
				this.comment_api_enabled = e.detail.value
			},

			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"comment_gravatar_default": this.comment_gravatar_defaultValue[this.comment_gravatar_defaultIndex],
					"comment_new_need_check": this.comment_new_need_check,
					"comment_new_notice": this.comment_new_notice,
					"comment_reply_notice": this.comment_reply_notice,
					"comment_api_enabled": this.comment_api_enabled,
					"comment_internal_plugin_js": this.comment_internal_plugin_js,
					"gravatar_source": this.gravatar_source,
					"comment_page_size": this.comment_page_size,
					"comment_content_placeholder": this.comment_content_placeholder
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
	.container {
		padding-bottom: 40px;
	}
	.block {
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
