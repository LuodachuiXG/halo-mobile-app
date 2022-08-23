<template>
	<view>
		<view class="block">
			<view class="item" @click="onOptionClick(1)">
				<text class="iconfont item-icon">&#xe7f5;</text>
				<text class="item-title">编辑设置</text>
				<view class="item-sign"></view>
			</view>
		</view>
		
		<view class="block">
			<view class="item" @click="onOptionClick(0)">
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
		methods: {
			/**
			 * 选项点击事件
			 * @param {Object} i
			 */
			onOptionClick: function(i) {
				switch (i) {
					// 关于
					case 0:
						uni.navigateTo({
							url: '../setting/setting_pages/about/about'
						});
						break;
					// 文章设置
					case 1:
						uni.navigateTo({
							url: '../setting/setting_pages/edit/edit'
						});
						break;
				}
			},
		}
	}
</script>

<style>

</style>
