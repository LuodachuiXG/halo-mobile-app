<template>
	<view>

		<view class="block">
			<u-grid :col="4">
				<u-grid-item @click="onItemClick(0)">
					<image src="@/static/images/home_article.png"></image>
					<text class="grid-text">所有文章</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(1)">
					<image src="@/static/images/home_edit.png"></image>
					<text class="grid-text">写文章</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(2)">
					<image src="@/static/images/home_category.png"></image>
					<text class="grid-text">分类目录</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(3)">
					<image src="@/static/images/home_tag.png"></image>
					<text class="grid-text">标签</text>
				</u-grid-item>
			</u-grid>
		</view>
		
		<view class="block">
			<u-grid :col="4">
				<u-grid-item @click="onItemClick(4)">
					<image src="@/static/images/home_page.png"></image>
					<text class="grid-text">所有页面</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(5)">
					<image src="@/static/images/home_addpage.png"></image>
					<text class="grid-text">新建页面</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(6)">
					<image src="@/static/images/home_journal.png"></image>
					<text class="grid-text">日志</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(7)">
					<image src="@/static/images/home_attachment.png"></image>
					<text class="grid-text">链接</text>
				</u-grid-item>
			</u-grid>
		</view>

		<view class="block">
			<u-grid :col="3">
				<u-grid-item @click="onItemClick(8)">
					<image src="@/static/images/home_theme.png"></image>
					<text class="grid-text">主题</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(9)">
					<image src="@/static/images/home_edittheme.png"></image>
					<text class="grid-text">主题编辑</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(10)">
					<image src="@/static/images/home_menu.png"></image>
					<text class="grid-text">菜单设置</text>
				</u-grid-item>
			</u-grid>
		</view>
	

		<view class="block">
			<u-grid :col="2">
				<u-grid-item @click="onItemClick(11)">
					<image src="@/static/images/home_attachment.png"></image>
					<text class="grid-text">附件</text>
				</u-grid-item>
				<u-grid-item @click="onItemClick(12)">
					<image src="@/static/images/home_comment.png"></image>
					<text class="grid-text">评论</text>
				</u-grid-item>
			</u-grid>
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
		
		onShow: function() {
		},
		
		methods: {
			onItemClick: function(i) {
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
						break;
					// 新建页面
					case 5:
						break;
					// 日志
					case 6:
						uni.navigateTo({
							url: "../journals/journals"
						});
						break;
					// 链接
					case 7:
						uni.navigateTo({
							url: "../links/links"
						});
						break;
					// 主题
					case 8:
						uni.navigateTo({
							url: "../themes/themes"
						});
						break;
					// 主题编辑
					case 9:
						break;
					// 菜单设置
					case 10:
						break;
					// 附件
					case 11:
						break;
					// 评论
					case 12:
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
	.block {
		padding: 30rpx;
		padding-left: 10rpx;
		padding-right: 10rpx;
		color: var(--textPrimaryColor);
		margin-bottom: 5px;
	}
	.block image {
		width: 40px;
		height: 40px;
		margin-bottom: 10px;
		margin-top: 10px;
	}
	
</style>
