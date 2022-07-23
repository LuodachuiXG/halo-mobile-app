<template>
	<view>
		<view class="block">
			<view class="item" @click="onOptionClick(3)">
				<text class="iconfont item-icon">&#xe75d;</text>
				<text class="item-title">查看所有发行版</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(2)">
				<text class="iconfont item-icon">&#xe7cf;</text>
				<text class="item-title">问题反馈 / BUG反馈</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(1)">
				<text class="iconfont item-icon">&#xe7e4;</text>
				<text class="item-title">感谢</text>
				<view class="item-sign"></view>
			</view>
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
						})
						break;
					// 开源框架及插件
					case 1:
						uni.navigateTo({
							url: '../setting/setting_pages/frameUsed/frameUsed'
						})
						break;
					// 问题反馈
					case 2:
						let that = this;
						let url = "https://gitee.com/luodachui/halo-mobile-app/issues/new?issue%5Bassignee_id%5D=0&issue%5Bmilestone_id%5D=0";
						this.openURL(url);
						uni.setClipboardData({
							data: url,
							success: function() {
								that.toast("反馈地址已复制");
							}
						});
						break;
					// 检查更新
					case 3:
						this.toast("当前版本：" + this.getVersion());
						this.openURL("https://gitee.com/luodachui/halo-mobile-app/releases")
						break;
				}
			},
		}
	}
</script>

<style>

</style>
