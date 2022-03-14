<template>
	<view>
		<view class="block aboutBlock gitee" @click="gitee">
			<image src="/static/images/gitee.png"></image>
			<text>前往 Gitee 开源页面</text>
			<image src='https://gitee.com/luodachui/halo-mobile-app/badge/star.svg?theme=white'>
			</image>
		</view>
		<view class="block aboutBlock github" @click="github">
			<image src="/static/images/github.png"></image>
			<text>前往 Github 开源页面</text>
		</view>
		
		<view class="block aboutBlock halo" @click="halo">
			<image src="/static/images/halo.png"></image>
			<text>前往 Halo 官网</text>
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
			},
			
			/**
			 * 前往halo页面单击事件
			 */
			halo: function() {
				this.openURL("https://halo.run")
			}
		}
	}
</script>

<style>
	.aboutBlock {
		padding: 20rpx;
		line-height: 20rpx;
	}

	.aboutBlock image {
		width: 50rpx;
		height: 50rpx;
		vertical-align: middle;
	}

	.aboutBlock text {
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

	.halo {
		background-color: #0069e1;
	}

	.halo:hover {
		background-color: #0051ad;
	}
</style>
