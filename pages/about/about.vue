<template>
	<view class="container">
		<view>Halo 版本：{{environments.version}}</view>
		<view>数据库：{{environments.database}}</view>
		<view>运行模式：{{environments.mode}}</view>
		<view>启动时间：{{this.format(environments.startTime)}}</view>
	</view>
</template>

<script>
	import {
		getEnvironments
	} from "@/network/OtherApi.js";
	
	export default {
		data() {
			return {
				environments: [],
			}
		},
		
		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData();
		},
		
		mounted() {
			this.refreshData();
		},
		
		methods: {
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				getEnvironments().then(data => {
					this.environments = data;
					uni.stopPullDownRefresh();
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			}
		}
	}
</script>

<style>
	.container {
		margin: 20rpx;
		background-color: white;
		padding: 20rpx;
		padding-left: 30rpx;
	}
	.container view {
		margin-bottom: 10px;
	}
	.container view:last-child {
		margin-bottom: 0px;
	}

</style>
