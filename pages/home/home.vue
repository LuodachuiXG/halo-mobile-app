<template>
	<view class="container">
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(0)">
				<text class="iconfont item-icon">&#xe7f4;</text>
				<text class="item-title">所有文章</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(1)">
				<text class="iconfont item-icon">&#xe6f6;</text>
				<text class="item-title">写文章</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(2)">
				<text class="iconfont item-icon">&#xe7d6;</text>
				<text class="item-title">分类目录</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(3)">
				<text class="iconfont item-icon">&#xe776;</text>
				<text class="item-title">标签</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(4)">
				<text class="iconfont item-icon">&#xe7ed;</text>
				<text class="item-title">所有页面</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(5)">
				<text class="iconfont item-icon">&#xe8a1;</text>
				<text class="item-title">新建页面</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(12)">
				<text class="iconfont item-icon">&#xe751;</text>
				<text class="item-title">附件</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(13)">
				<text class="iconfont item-icon">&#xe8a6;</text>
				<text class="item-title">评论</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(6)">
				<text class="iconfont item-icon">&#xe74a;</text>
				<text class="item-title">主题</text>
				<view class="item-sign"></view>
			</view>
<!-- 			<view class="view-me-exit item" @click="click(7)">
				<text class="iconfont item-icon">&#xe6f6;</text>
				<text class="item-title">主题编辑</text>
				<view class="item-sign"></view>
			</view> -->
			<view class="view-me-exit item" @click="click(8)">
				<text class="iconfont item-icon">&#xe7fc;</text>
				<text class="item-title">菜单设置</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(9)">
				<text class="iconfont item-icon">&#xe8a5;</text>
				<text class="item-title">个人资料</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="view-me-app-option block">
			<view class="view-me-exit item" @click="click(10)">
				<text class="iconfont item-icon">&#xe7f5;</text>
				<text class="item-title">博客设置</text>
				<view class="item-sign"></view>
			</view>
			<view class="view-me-exit item" @click="click(11)">
				<text class="iconfont item-icon">&#xe7b9;</text>
				<text class="item-title">关于</text>
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
		mounted: function() {


		},

		onShow: function() {},

		methods: {
			click: function(i) {
				switch(i) {
					// 所有文章
					case 0:
						uni.navigateTo({
							url: "../posts/posts"
						});
						break;
					// 写文章
					case 1:
						uni.navigateTo({
							url: "../posts/edit/edit?type=add"
						});
						break;
					// 分类目录
					case 2:
						uni.navigateTo({
							url: "../categories/categories"
						});
						break;
					// 标签
					case 3:
						uni.navigateTo({
							url: "../tags/tags"
						});
						break;
					// 所有页面
					case 4:
						uni.navigateTo({
							url: "../page/page"
						});
						break;
					// 新建页面
					case 5:
						uni.navigateTo({
							url: "../page/edit/edit?type=add"
						})
						break;
					// 主题
					case 6:
						uni.navigateTo({
							url: "../themes/themes"
						});
						break;
					// 主题编辑
					case 7:
						break;
					// 菜单设置
					case 8:
						uni.navigateTo({
							url: "../menus/menus"
						})
						break;
					// 个人资料
					case 9:
						uni.navigateTo({
							url: "../userProfile/userProfile"
						});
						break;
					// 博客设置
					case 10:
						uni.navigateTo({
							url: "../blogSetting/blogSetting"
						});
						break;
					// 关于
					case 11:
						uni.navigateTo({
							url: "../about/about"
						})
						break;
					// 附件
					case 12:
						uni.navigateTo({
							url: "../attachment/attachment"
						})
						break;
					// 评论
					case 13:
						uni.navigateTo({
							url: "../comments/comments"
						});
						break;
				}
			}
		}
	}
</script>

<style>
	.container {
		padding-bottom: 50px;
	}
</style>
