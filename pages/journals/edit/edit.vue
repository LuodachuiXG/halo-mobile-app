<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>

		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>公开日志：</view>
				<u-switch v-model="journalType"></u-switch>
			</view>
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>日志内容：</view>
				<u--textarea v-model="journalContent" placeholder="请输入日志(支持 markdown)" autoHeight :maxlength="-1"></u--textarea>
			</view>
			
			<view class="view-input" style="position: relative;margin-top: 20px;">
				<button class="btn" type="primary" @click="releaseJournal">发布</button>
			</view>
		</view>


		<uni-fab horizontal="right" vertical="bottom" :content="content" @trigger="onFabClick"></uni-fab>
	</view>
</template>

<script>
	import {
		createJournal,
		updateJournal
	} from "@/network/JournalApi.js";
	export default {
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
					}
				],

				// 用于存储选择附件的 URL
				imgUrl: "",
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

		},
		onShow() {
			let that = this;

			// 判断当前是否是从附件选择页面返回
			// 选择附件后，imgUrl 变量会被赋值
			// 通过 imgUrl 变量内容来判断是否选择附件
			if (this.imgUrl.length > 0) {
				// 将附件地址复制到系统剪贴板
				uni.setClipboardData({
					data: this.imgUrl,
					success: function() {
						that.toast("附件地址已复制");
					},
					fail: function() {
						that.toast("附件地址复制失败");
					}
				});

				// 复制到剪贴板后清空变量
				this.imgUrl = "";
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
							url: "../../attachment/selectAttachment/selectAttachment?attrName=imgUrl"
						})
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
	.content {
		padding-bottom: 50px;
	}

	.block {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.btn {
		color: #FFFFFF;
		height: 60rpx;
		width: 100%;
		line-height: 60rpx;
		margin-right: 20rpx;
		font-size: .9em;
	}

</style>
