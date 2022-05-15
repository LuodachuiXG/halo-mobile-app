<template>
	<view>
		<u-notify ref="popup"></u-notify>
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
	import {
		getOptionsByMapViewsKeys,
		updateOptionsByMapViews
	} from "@/network/SettingApi.js";
	export default {
		data() {
			return {
				api_enabled: false,
				api_access_key: "",
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
				let array = ["api_enabled", "api_access_key"];
				let that = this;
				getOptionsByMapViewsKeys(array).then(data => {
					that.api_enabled = data.api_enabled;
					that.api_access_key = data.api_access_key;
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
				if (this.api_access_key.length <= 0) {
					this.popup("Access Key 不能为空");
					return ;
				}
				let json = {
					"api_enabled": this.api_enabled,
					"api_access_key": this.api_access_key
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
