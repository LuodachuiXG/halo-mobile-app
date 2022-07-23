<template>
	<view>
		<view class="block">
			<view class="item" @click="onOptionClick(3)">
				<image src="/static/images/data.png"></image>
				<text>查看所有发行版</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(2)">
				<image src="/static/images/edit.png"></image>
				<text>问题反馈 / BUG反馈</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(1)">
				<image src="/static/images/earth.png"></image>
				<text>开源框架及插件</text>
				<view class="item-sign"></view>
			</view>
			<view class="item" @click="onOptionClick(0)">
				<image src="/static/images/about.png"></image>
				<text>关于</text>
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
