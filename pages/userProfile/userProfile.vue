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
				<view class="view-input-titleView">头像：</view>
				<view class="right-button-input">
					<input class="input" type="text" v-model="avatar" />
					<image src="/static/images/picture.png" @click="selectAttachment('avatar')"></image>
				</view>
				<text class="view-input-text">* 在分类页面可展示，需要主题支持</text>
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
	import {
		getUserStatistics,
		updateUserProfiles
	} from "../../common/api.js";
	export default {
		data() {
			return {
				username: "",
				nickname: "",
				email: "",
				description: "",
				createTime: "",
				updateTime: "",
				avatar: "",
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
				let that = this;
				getUserStatistics().then(data => {
					let user = data.user;
					that.username = user.username;
					that.nickname = user.nickname;
					that.email = user.email;
					that.description = user.description;
					that.createTime = user.createTime;
					that.updateTime = user.updateTime;
					that.avatar = user.avatar;
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
					"description": this.description,
					"avatar": this.avatar
				};
				let that = this;
				updateUserProfiles(json).then(data => {
					that.popup('保存成功', 'success');
					that.refreshData()
				}).catch(err => {
					uni.showModal({
						title: "保存数据失败",
						content: err
					});
				});
				
			},
			
			/**
			 * 打开新窗口选择附件
			 * @param {Object} attrName 当前页面的变量，传递给附件选择页，用于选择附件后修改的变量
			 */
			selectAttachment: function(attrName) {
				uni.navigateTo({
					url: "../attachment/selectAttachment/selectAttachment?attrName=" + attrName
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
