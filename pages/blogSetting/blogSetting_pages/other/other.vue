<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">自定义全局 head：</view>
				<textarea class="input" v-model="blog_custom_head" 
					placeholder="放置于每个页面的 <head></head>标签中"></textarea>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">自定义内容页 head：</view>
				<textarea class="input" v-model="blog_custom_content_head" 
					placeholder="仅放置于内容页面的 <head></head>标签中"></textarea>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">统计代码：</view>
				<textarea class="input" v-model="blog_statistics_code" 
					placeholder="第三方网站统计的代码,如:Google Analytics、百度统计、CNZZ 等"></textarea>
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
				blog_custom_head: "",
				blog_custom_content_head: "",
				blog_statistics_code: "",

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
				let array = ["blog_custom_head", "blog_custom_content_head", "blog_statistics_code"];
				let that = this;
				getOptionsByMapViewsKeys(array).then(data => {
					that.blog_custom_head = data.blog_custom_head
					that.blog_custom_content_head = data.blog_custom_content_head
					that.blog_statistics_code = data.blog_statistics_code
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
				let json = {
					"blog_custom_head": this.blog_custom_head,
					"blog_custom_content_head": this.blog_custom_content_head,
					"blog_statistics_code": this.blog_statistics_code
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
