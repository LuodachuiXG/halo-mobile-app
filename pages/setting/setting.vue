<template>
	<view>
		<view class="block">
			<view class="item" @click="onOptionClick(0)">
				<image src="/static/images/about.png"></image>
				<text>关于</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(1)">
				<image src="/static/images/earth.png"></image>
				<text>用到的开源框架及插件</text>
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
						})
						break;
					// 开源框架及插件
					case 1:
						uni.navigateTo({
							url: '../setting/setting_pages/frameUsed/frameUsed'
						})
						break;
				}
			},
		}
	}
</script>

<style>

</style>
