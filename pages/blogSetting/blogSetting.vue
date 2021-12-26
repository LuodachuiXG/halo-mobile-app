<template>
	<view class="container">
		<view class="block-title">
			<text>普通设置</text>
			<view></view>
		</view>
		<view class="block">
			<view class="item" @click="onOptionClick(0)">
				<image src="/static/images/spanner.png"></image>
				<text>常规设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(1)">
				<image src="/static/images/earth.png"></image>
				<text>SEO设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(2)">
				<image src="/static/images/article.png"></image>
				<text>文章设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(3)">
				<image src="/static/images/comment.png"></image>
				<text>评论设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(4)">
				<image src="/static/images/attachment.png"></image>
				<text>附件设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(5)">
				<image src="/static/images/email.png"></image>
				<text>SMTP服务</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(6)">
				<image src="/static/images/other.png"></image>
				<text>其他设置</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="block-title" style="margin-top: -10rpx;">
			<text>高级设置</text>
			<view></view>
		</view>
		<view class="block">
			<view class="item" @click="onOptionClick(7)">
				<image src="/static/images/link.png"></image>
				<text>固定链接</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(8)">
				<image src="/static/images/api.png"></image>
				<text>API设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(9)">
				<image src="/static/images/other.png"></image>
				<text>其他设置</text>
				<view class="item-sign"></view>
			</view>
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
			 * 设置item选项单击事件
			 * @param {Object} i
			 */
			onOptionClick: function(i) {
				switch(i) {
					// 常规设置
					case 0:
						uni.navigateTo({
							url:"../blogSetting_pages/general/general"
						})
				}
			}
		}
	}
</script>

<style>
	
</style>
