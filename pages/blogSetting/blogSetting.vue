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
				<text>SEO 设置</text>
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
				<text>SMTP 服务</text>
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
				let url = ""
				switch(i) {
					// 常规设置
					case 0:
						url = "../blogSetting_pages/general/general"
						break;
					// SEO 设置
					case 1:	
						url = "../blogSetting_pages/seo/seo"
						break;
					// 文章设置
					case 2:	
						url = "../blogSetting_pages/post/post"
						break;	
					// 评论设置
					case 3:	
						url = "../blogSetting_pages/comment/comment"
						break;
					// 附件设置
					case 4:	
						url = "../blogSetting_pages/attachment/attachment"
						break;
					// SMTP 服务
					case 5:	
						url = "../blogSetting_pages/smtp/smtp"
						break;
					// 其他设置
					case 6:	
						url = "../blogSetting_pages/other/other"
						break;
					// 高级设置-固定链接
					case 7:	
						url = "../blogSetting_pages/advanced_link/advanced_link"
						break;
				}
				uni.navigateTo({
					url: url
				})
			}
		}
	}
</script>

<style>
	
</style>
