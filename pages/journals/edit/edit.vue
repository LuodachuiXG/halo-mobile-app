<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>
		<view class="block">
			<scroll-view class="scroll-view" :scroll-y="true"
				:style="'height:' + (windowHeight / 2) + 'px;width:' 
				+ (windowWidth - 10) + 'px;'">
				<mp-html class="mp-html" :content="journalContent" :markdown="true" :selectable="true"
					containerStyle="padding: 10px;padding-top:0px;overflow: hidden;"></mp-html>
			</scroll-view>
		
			<!-- 分割线 -->
			<view class="spacer"></view>
		
			<scroll-view :scroll-y="true" 
				:style="'width:' + windowWidth + 'px;height:' 
					+ (windowHeight / 2) + 'px;'">
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>公开日志：</view>
					<u-switch v-model="journalType"></u-switch>
				</view>
				<textarea 
					class="input" 
					v-model="journalContent" 
					:style="'width:' + (windowWidth - 10) + 'px;'"
					:placeholder="placeholder" 
					:maxlength="-1" 
					:cursor-spacing="200"
					auto-height="true"></textarea>
			</scroll-view>
		</view>

		<uni-fab horizontal="right" vertical="bottom" :content="content" @trigger="onFabClick"></uni-fab>
	</view>
</template>

<script>
	import mpHtml from '@/components/mp-html/mp-html';
	import {
		createJournal,
		updateJournal
	} from "@/network/JournalApi.js";
	export default {
		components: {
			mpHtml
		},
		data() {
			return {
				// 当前是编辑还是新增日志，add/update
				type: "",

				journalId: 0,
				journal: [],
				journalContent: "",
				journalType: true,
				// 悬浮按钮弹出菜单
				content: [{
						"text": "附件库",
						"iconPath": "/static/images/attachment.png"
					},
					{
						"text": "发布",
						"iconPath": "/static/images/upload.png"
					}
				],

				// 用于存储选择附件的 URL
				imgUrl: [],
				
				placeholder: "请输入日志内容",
				// 可用窗口宽高
				windowHeight: 0,
				windowWidth: 0,
			}
		},
		onLoad(res) {
			this.type = res.type;
			if (this.type === "add") {
				uni.setNavigationBarTitle({
					title: "新增日志"
				});
			} else {
				uni.setNavigationBarTitle({
					title: "编辑日志"
				});
				let index = res.i;
				// 从上个页面直接读取 journal
				let pages = getCurrentPages();
				// 上一页面实例
				let prevPage = pages[pages.length - 2];
				this.journal = prevPage.$vm._data.journals[index];
				this.journalId = this.journal.id;
				this.journalContent = this.journal.sourceContent;
				this.journalType = this.journal.type === "INTIMATE" ? false : true;
			}
			
			// 获取可使用窗口宽高
			let windowInfo = uni.getWindowInfo();
			this.windowHeight = windowInfo.windowHeight;
			this.windowWidth = windowInfo.windowWidth;

		},
		onShow() {
			let that = this;
			
			// 判断当前是否是从附件选择页面返回
			// 选择附件后，imgUrl 变量会被赋值
			// 通过 imgUrl 变量内容来判断是否选择附件
			if (this.imgUrl.length > 0) {
				// 判断是否设置自动粘贴到编辑框
				let autoPaste = this.getData("setting_edit_autoPaste");
				autoPaste = (autoPaste === undefined || autoPaste === "") ? true : JSON.parse(autoPaste);
				// 判断是否设置复制 markdown 代码
				let copyMarkdown = this.getData("setting_edit_copyMarkdown");
				copyMarkdown = (copyMarkdown === undefined || copyMarkdown === "") ? true : JSON.parse(copyMarkdown);
			
				// 拼接附件地址变量，稍后复制到剪贴板
				let clipboadrStr = "";
				this.imgUrl.forEach(function(path) {
					let str = (copyMarkdown ? ("![](" + path + ")") : path) + "\n";
					clipboadrStr += str;
					if (autoPaste) {
						that.journalContent += str;
					}
				});
				
				// 复制到剪贴板
				uni.setClipboardData({
					data: clipboadrStr,
					success: function() {
						that.toast("附件地址已复制");
					},
					fail: function() {
						that.toast("附件地址复制失败");
					}
				});
			
				// 清空变量
				this.imgUrl = [];
			}
		},
		methods: {
			/**
			 * 悬浮按钮菜单点击事件
			 * @param {Object} e
			 */
			onFabClick: function(e) {
				switch (e.index) {
					case 0:
						// 选择附件
						uni.navigateTo({
							url: "../../attachment/selectAttachment/selectAttachment?attrName=imgUrl&mul=true"
						});
						break;
					case 1:
						// 发布日志
						this.releaseJournal();
						break;
				}
			},

			/**
			 * 发布按钮点击事件
			 */
			releaseJournal: function() {
				let that = this;
				if (this.journalContent.length <= 0) {
					this.popup("日志内容不能为空");
					return;
				}
				let type = this.journalType ? "PUBLIC" : "INTIMATE";
				if (this.type === "add") {
					// 新增日志
					createJournal(this.journalContent, type).then(data => {
						that.toast("发布成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "发布失败",
							content: err
						});
					});
				} else {
					// 编辑日志
					updateJournal(this.journalId, this.journalContent, type).then(data => {
						that.toast("修改成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "修改失败",
							content: err
						});
					});
				}
				
			},


			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				if (type === "error") {
					this.$refs.popup.error(message);
				} else {
					this.$refs.popup.success(message);
				}
			},
		}
	}
</script>

<style>
	.block {
		margin: 0px;
		border-radius: 0px;
	}
	
	.btn {
		color: #FFFFFF;
		height: 60rpx;
		line-height: 60rpx;
		margin-right: 20rpx;
		font-size: .9em;
	}
	
	.input {
		border: none;
		padding: 10px;
		border-radius: 0px;
		font-size: 32rpx;
	}
	
	.spacer {
		width: 100%;
		height: 4rpx;
		background-color: var(--textContentColor);
	}
	
	.title {
		font-size: 38rpx;
		font-weight: bold;
	}

</style>
