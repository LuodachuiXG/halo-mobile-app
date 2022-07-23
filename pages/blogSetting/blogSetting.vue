<template>
	<view class="container">
		<view class="block-title">
			<text>普通设置</text>
			<view></view>
		</view>
		<view class="block">
			<view class="item" @click="onOptionClick(0)">
				<text class="iconfont item-icon">&#xe7f5;</text>
				<text class="item-title">常规设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(1)">
				<text class="iconfont item-icon">&#xe781;</text>
				<text class="item-title">SEO 设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(2)">
				<text class="iconfont item-icon">&#xe892;</text>
				<text class="item-title">文章设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(3)">
				<text class="iconfont item-icon">&#xe8a6;</text>
				<text class="item-title">评论设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(4)">
				<text class="iconfont item-icon">&#xe751;</text>
				<text class="item-title">附件设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(5)">
				<text class="iconfont item-icon">&#xe77f;</text>
				<text class="item-title">SMTP 服务</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(6)">
				<text class="iconfont item-icon">&#xe8eb;</text>
				<text class="item-title">其他设置</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="block-title" style="margin-top: -10rpx;">
			<text>高级设置</text>
			<view></view>
		</view>
		<view class="block">
			<view class="item" @click="onOptionClick(7)">
				<text class="iconfont item-icon">&#xe797;</text>
				<text class="item-title">固定链接</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(8)">
				<text class="iconfont item-icon">&#xe7b8;</text>
				<text class="item-title">API设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(9)">
				<text class="iconfont item-icon">&#xe8eb;</text>
				<text class="item-title">其他设置</text>
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
						url = "../blogSetting/blogSetting_pages/general/general"
						break;
					// SEO 设置
					case 1:	
						url = "../blogSetting/blogSetting_pages/seo/seo"
						break;
					// 文章设置
					case 2:	
						url = "../blogSetting/blogSetting_pages/post/post"
						break;	
					// 评论设置
					case 3:	
						url = "../blogSetting/blogSetting_pages/comment/comment"
						break;
					// 附件设置
					case 4:	
						url = "../blogSetting/blogSetting_pages/attachment/attachment"
						break;
					// SMTP 服务
					case 5:	
						url = "../blogSetting/blogSetting_pages/smtp/smtp"
						break;
					// 其他设置
					case 6:	
						url = "../blogSetting/blogSetting_pages/other/other"
						break;
					// 高级设置-固定链接
					case 7:	
						url = "../blogSetting/blogSetting_pages/advanced_link/advanced_link"
						break;
					// 高级设置-APi设置
					case 8:	
						url = "../blogSetting/blogSetting_pages/advanced_api/advanced_api"
						break;
					// 高级设置-其他设置
					case 9:	
						url = "../blogSetting/blogSetting_pages/advanced_other/advanced_other"
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
