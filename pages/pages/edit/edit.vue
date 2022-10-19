<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>
		
		<view class="block">
			<scroll-view class="scroll-view" :scroll-y="true"
				:style="'height:' + (windowHeight / 2) + 'px;width:' 
				+ (windowWidth - 10) + 'px;'">
				<mp-html class="mp-html" :content="originalContent" :markdown="true" :selectable="true"
					containerStyle="padding: 10px;padding-top:0px;overflow: hidden;"></mp-html>
			</scroll-view>
		
			<!-- 分割线 -->
			<view class="spacer"></view>
		
			<scroll-view :scroll-y="true" 
				:style="'width:' + windowWidth + 'px;height:' 
					+ (windowHeight / 2) + 'px;'">
				<input class="input title" v-model="title" placeholder="输入页面标题" />
				<textarea 
					class="input" 
					v-model="originalContent" 
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
		getPage,
		getPagePreviewLink,
		updatePage,
		addPage
	} from "@/network/PageApi.js";
	export default {
		components: {
			mpHtml
		},
		data() {
			return {
				// 当前是编辑还是新增页面，add/update
				type: "",

				pageId: 0,
				mPage: [],
				title: "",
				originalContent: "",
				// 悬浮按钮弹出菜单
				content: [{
						"text": "附件库",
						"iconPath": "/static/images/attachment.png"
					},
					{
						"text": "预览",
						"iconPath": "/static/images/preview.png"
					},
					{
						"text": "保存草稿",
						"iconPath": "/static/images/save.png"
					},
					{
						"text": "发布",
						"iconPath": "/static/images/upload.png"
					}
				],

				// 用于存储选择附件的 URL
				imgUrl: "",
				
				placeholder: "请输入页面内容",
				
				// 可用窗口宽高
				windowHeight: 0,
				windowWidth: 0,
			}
		},
		onLoad(e) {
			this.type = e.type;
			if (this.type === "add") {
				uni.setNavigationBarTitle({
					title: "新增页面"
				});
			} else {
				uni.setNavigationBarTitle({
					title: "编辑页面"
				});
				this.pageId = e.id;
				this.refreshData();
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
						that.originalContent += str;
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
			 * 获取页面数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				getPage(this.pageId).then(data => {
					that.mPage = data;
					that.title = that.mPage.title;
					that.originalContent = that.mPage.originalContent;
					uni.hideLoading();
				}).catch(err => {
					uni.hideLoading();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},

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
						})
						break;
					case 1:
						// 预览
						let that = this;
						if (this.title.length <= 0) {
							this.popup("标题不能为空");
							return;
						}
						if (this.type === "add") {
							// 新增页面状态下预览
							let json = {"title" : this.title,
								"originalContent" : this.originalContent,
								"status" : "DRAFT"};
							this.mPage = json;
							// 先创建页面
							addPage(this.mPage).then(data => {
								// 创建页面成功，保存 id
								that.mPage.id = data.id;
								// 提交数据成功后获取预览 URL
								getPagePreviewLink(that.mPage.id).then(data => {
									that.openURL(data.data);
								}).catch(err => {
									uni.showModal({
										title: "获取预览地址失败",
										content: err
									});
								});
								// 因为当前已经创建了页面，所以切换为编辑模式
								that.type = "update";
								that.pageId = data.id;
								that.refreshData();
							}).catch(err => {
								uni.showModal({
									title: "获取预览地址失败",
									content: err
								});
							});
						} else {
							// 编辑页面状态下预览
							this.mPage.title = this.title;
							this.mPage.originalContent = this.originalContent;
							updatePage(this.mPage.id, this.mPage).then(data => {
								// 提交数据成功后获取预览 URL
								getPagePreviewLink(that.mPage.id).then(data => {
									that.openURL(data.data);
								}).catch(err => {
									uni.showModal({
										title: "获取预览地址失败",
										content: err
									});
								});
							}).catch(err => {
								uni.showModal({
									title: "提交数据失败",
									content: err
								});
							});
						}
						break;
					case 2:
						// 保存草稿
						this.saveDraft();
						break;
					case 3:
						// 发布
						this.releasePage();
						break;
				}
			},


			/**
			 * 保存草稿按钮点击事件
			 */
			saveDraft: function() {
				if (this.title.length <= 0) {
					this.popup("页面标题不能为空");
					return;
				}

				if (this.type === "add") {
					// 新增页面的保存草稿也是先新建一个页面
					let that = this;
					let json = {
						"title": this.title,
						"originalContent": this.originalContent
					};
					addPage(json).then(data => {
						// 创建页面成功，保存 id
						that.mPage.id = data.id;
						that.popup("保存草稿成功", "success");
						
						// 因为当前已经创建了页面，所以切换为编辑模式
						that.type = "update";
						that.pageId = data.id;
						that.refreshData();
					}).catch(err => {
						uni.showModal({
							title: "保存草稿失败",
							content: err
						});
					});
				} else {
					// 编辑页面的保存草稿
					// 先将 page 的 status 改为 DRAFT
					this.mPage.status = "DRAFT";
					this.mPage.title = this.title;
					this.mPage.originalContent = this.originalContent;
					updatePage(this.mPage.id, this.mPage).then(data => {
						this.popup("保存草稿成功", "success");
					}).catch(err => {
						uni.showModal({
							title: "保存草稿失败",
							content: err
						});
					});
				}
			},

			/**
			 * 发布按钮点击事件
			 */
			releasePage: function() {
				if (this.title.length <= 0) {
					this.popup("页面标题不能为空");
					return;
				}
				this.mPage.title = this.title;
				this.mPage.originalContent = this.originalContent;
				uni.navigateTo({
					url: '../setting/setting'
				})
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
	}
	
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
