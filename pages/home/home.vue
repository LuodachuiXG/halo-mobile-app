<template>
	<view class="container">
		<uni-collapse class="collapse" accordion="">
			<uni-collapse-item title="文章" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="list" title="所有文章" clickable size="large"></u-cell>
					<u-cell icon="edit-pen" title="写文章" clickable size="large"></u-cell>
					<u-cell icon="grid" title="分类目录" clickable size="large"></u-cell>
					<u-cell icon="tags" title="标签" :border="false" clickable size="large"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="页面" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="grid" title="所有页面" clickable size="large"></u-cell>
					<u-cell icon="plus-circle" title="新建页面" :border="false" clickable size="large"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="外观" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="star" title="主题" clickable size="large"></u-cell>
					<u-cell icon="setting" title="主题编辑" clickable size="large"></u-cell>
					<u-cell icon="setting" title="菜单设置" :border="false" clickable size="large"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="用户" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="account" title="个人资料" :border="false" clickable size="large"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="系统" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="setting" title="博客设置" clickable size="large"></u-cell>
					<u-cell icon="info-circle" title="关于" :border="false" clickable size="large"></u-cell>
				</u-cell-group>
			</uni-collapse-item>
			
			<uni-collapse-item title="其他" :show-animation="true">
				<u-cell-group :border="false">
					<u-cell icon="attach" title="附件" size="large"></u-cell>
					<u-cell icon="chat" title="评论" :border="false" size="large"></u-cell>
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
