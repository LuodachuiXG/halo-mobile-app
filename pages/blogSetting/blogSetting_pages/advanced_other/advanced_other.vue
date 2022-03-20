<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">全局绝对路径：</view>
				<switch :checked="global_absolute_path_enabled" @change="switchChange" style="margin-bottom: 10rpx;" />
				<br><text class="view-input-text">* 对网站上面的所有页面路径、本地附件路径、以及主题中的静态资源路径有效。</text>
			</view>
			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	import {
		getOptionsByMapViewsKeys,
		updateOptionsByMapViews
	} from "../../../../common/api.js";
	export default {
		data() {
			return {
				global_absolute_path_enabled: "",
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
				let array = ["global_absolute_path_enabled"];
				let that = this;

				getOptionsByMapViewsKeys(array).then(data => {
					that.global_absolute_path_enabled = data.global_absolute_path_enabled
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
			 * 全局绝对路径switch改变事件
			 * @param {Object} e
			 */
			switchChange: function(e) {
				this.global_absolute_path_enabled = e.detail.value
			},


			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"global_absolute_path_enabled": this.global_absolute_path_enabled
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
