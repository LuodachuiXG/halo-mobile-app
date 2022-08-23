<template>
	<view>
		<view class="block" style="margin-bottom: 0px;">
			<view class="item-option">
				<text class="item-option-title">{{ copyMarkdownText }}</text>
				<view class="item-option-switch">
					<u-switch v-model="copyMarkdown" @change="onCopyMarkdownChange"></u-switch>
				</view>
			</view>		
			<view class="item-option">
				<text class="item-option-title">图片自动粘贴</text>
				<view class="item-option-switch">
					<u-switch v-model="autoPaste" @change="onAutoPasteChange"></u-switch>
				</view>
			</view>		
		</view>
		<view class="view-input-text">
			编辑时，选择图片后会自动将图片地址粘贴到编辑框。
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				autoPaste: true,
				copyMarkdown: true,
				copyMarkdownText: "复制图片 Markdown 代码",
			}
		},
		mounted() {
			this.refreshData();
		},
		
		methods: {
			/**
			 * 刷新设置数据
			 */
			refreshData: function() {
				this.autoPaste = this.getData("setting_edit_autoPaste");
				this.autoPaste = (this.autoPaste === undefined || this.autoPaste === "") ? true : JSON.parse(this.autoPaste);
				this.copyMarkdown = this.getData("setting_edit_copyMarkdown");
				this.copyMarkdown = (this.copyMarkdown === undefined || this.copyMarkdown === "") ? true : JSON.parse(this.copyMarkdown);
				
				if (!this.copyMarkdown) {
					this.copyMarkdownText = "复制图片地址";
				}
			},
			
			
			/**
			 * 自动粘贴 switch 改变事件
			 */
			onAutoPasteChange: function() {
				this.setData("setting_edit_autoPaste", this.autoPaste);
			},
			
			/**
			 * 复制图片 Markdown 代码 switch 改变事件
			 */
			onCopyMarkdownChange: function() {
				this.setData("setting_edit_copyMarkdown", this.copyMarkdown);
				if (this.copyMarkdown) {
					this.copyMarkdownText = "复制图片 Markdown 代码";
				} else{
					this.copyMarkdownText = "复制图片地址";
				}
			}
		}
	}
</script>

<style>
	.view-input-text {
		margin: 30rpx;
		margin-top: 10rpx;
	}

</style>
