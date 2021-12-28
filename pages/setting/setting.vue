<template>
	<view>
		<view class="block gitee" @click="gitee">
			<image src="/static/images/gitee.png"></image>
			<text>前往Gitee开源页面</text>
			<image src='https://gitee.com/luodachui/halo-mobile-app/badge/star.svg?theme=white' >
			</image>
		</view>
		<view class="block github" @click="github">
			<image src="/static/images/github.png"></image>
			<text>前往Github开源页面</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {

			}
		},
		methods: {
			/**
			 * 前往gitee页面单击事件
			 */
			gitee: function() {
				this.openURL("https://gitee.com/luodachui/halo-mobile-app")
			},
			
			/**
			 * 前往github页面单击事件
			 */
			github: function() {
				this.openURL("https://github.com/LuodachuiXG/halo-mobile-app")
			}
		}
	}
</script>

<style>
	.block {
		padding: 20rpx;
		line-height: 20rpx;
	}

	.block image {
		width: 50rpx;
		height: 50rpx;
		vertical-align: middle;
	}

	.block text {
		margin-left: 20rpx;
		vertical-align: middle;
		font-size: .9em;
		color: #f0f0f0;
	}

	.gitee {
		background-color: #C71D23;
		position: relative;
	}

	.gitee:hover {
		background-color: #af1824;
	}
	
	.gitee image:last-child {
		width: 140rpx;
		height: 40rpx;
		position: absolute;
		right: 30rpx;
		top: calc(50% - 20rpx);
	}

	.github {
		background-color: #000000;
	}

	.github:hover {
		background-color: #323232;
	}
</style>
