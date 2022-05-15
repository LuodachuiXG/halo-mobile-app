<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">屏蔽搜索引擎：</view>
				<switch :checked="seo_spider_disabled" @change="switchChange" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">关键词：</view>
				<input class="input" type="text" v-model="seo_keywords" placeholder="多个关键词以英文状态下的逗号隔开" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">博客描述：</view>
				<textarea class="input" v-model="seo_description"></textarea>
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
				seo_spider_disabled: "",
				seo_keywords: "",
				seo_description: "",

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
				let array = ["seo_spider_disabled", "seo_keywords", "seo_description"];
				let that = this;
				
				getOptionsByMapViewsKeys(array).then(data => {
					that.seo_spider_disabled = data.seo_spider_disabled
					that.seo_keywords = data.seo_keywords
					that.seo_description = data.seo_description
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
			 * 屏蔽搜索引擎switch改变事件
			 * @param {Object} e
			 */
			switchChange: function(e) {
				this.seo_spider_disabled = e.detail.value
			},
			
			
			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"seo_spider_disabled": this.seo_spider_disabled,
					"seo_keywords": this.seo_keywords,
					"seo_description": this.seo_description
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
