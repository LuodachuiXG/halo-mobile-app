<template>
	<view class="container">
		<uni-collapse class="collapse" accordion="">
			<uni-collapse-item title="文章" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="list" title="所有文章" clickable size="large" @click="click(0)"></u-cell>
					<u-cell icon="edit-pen" title="写文章" clickable size="large" @click="click(1)"></u-cell>
					<u-cell icon="grid" title="分类目录" clickable size="large" @click="click(2)"></u-cell>
					<u-cell icon="tags" title="标签" :border="false" clickable size="large" @click="click(3)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="页面" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="grid" title="所有页面" clickable size="large" @click="click(4)"></u-cell>
					<u-cell icon="plus-circle" title="新建页面" :border="false" clickable size="large" @click="click(5)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="外观" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="star" title="主题" clickable size="large" @click="click(6)"></u-cell>
					<u-cell icon="setting" title="主题编辑" clickable size="large" @click="click(7)"></u-cell>
					<u-cell icon="setting" title="菜单设置" :border="false" clickable size="large" @click="click(8)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="用户" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="account" title="个人资料" :border="false" clickable size="large" @click="click(9)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="系统" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="setting" title="博客设置" clickable size="large" @click="click(10)"></u-cell>
					<u-cell icon="info-circle" title="关于" :border="false" clickable size="large" @click="click(11)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="其他" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="attach" title="附件" size="large" @click="click(12)"></u-cell>
					<u-cell icon="chat" title="评论" :border="false" size="large" @click="click(13)"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
		</uni-collapse>
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
		padding: 30rpx;
	}

	.collapse {
		margin-bottom: 75px;
	}
</style>
