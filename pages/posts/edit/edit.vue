<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>
		
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>文章标题：</view>
				<input class="input" type="text" v-model="title" placeholder="请输入标题"/>
			</view>
			<view class="view-input">
				<view class="view-input-titleView">文章内容：</view>
				<textarea class="input" v-model="originalContent"
					:placeholder="placeholder" :maxlength="-1" style="height: 800rpx;"></textarea>
			</view>
			<view class="view-input" style="position: relative;margin-top: 20px;">
				<button class="btn left-btn" type="warn" @click="saveDraft">保存草稿</button>
				<button class="btn right-btn" type="primary" @click="releasePost">发布</button>
			</view>
		</view>
		
		
		<uni-fab horizontal="right" vertical="bottom" :content="content"
			@trigger="onFabClick"></uni-fab>
	</view>
</template>

<script>
	import {
		getPost,
		addPost,
		getPostPreviewLink,
		updatePost
	} from "@/network/PostApi.js";
	export default {
		data() {
			return {
				// 当前是编辑还是新增文章，add/update
				type: "",
				
				postId: 0,
				post: [],
				title: "",
				originalContent: "",
				// 悬浮按钮弹出菜单
				content: [{"text": "附件库", "iconPath": "/static/images/attachment.png"},
					{"text": "预览", "iconPath": "/static/images/preview.png"}],
					
				// 用于存储选择附件的 URL
				imgUrl: "",
				
				placeholder: "如需插入图片，请点击右下角添加附件，复制附件链接后需要按照 markdown 语法插入图片"
			}
		},
		onLoad(e) {
			this.type = e.type;
			if (this.type === "add") {
				uni.setNavigationBarTitle({
					title: "新增文章"
				});
			} else {
				uni.setNavigationBarTitle({
					title: "编辑文章"
				});
				this.postId = e.id;
				this.refreshData();
			}
			
		},
		onShow() {
			let that = this;
			
			// 判断当前是否是从附件选择页面返回
			// 选择附件后，imgUrl 变量会被赋值
			// 通过 imgUrl 变量内容来判断是否选择附件
			if (this.imgUrl.length > 0) {
				// 判断是否设置自动粘贴到编辑框
				let autoPaste = this.getData("setting_edit_autoPaste");
				console.log(autoPaste);
				autoPaste = (autoPaste === undefined || "") ? true : JSON.parse(autoPaste);
				// 判断是否设置复制 markdown 代码
				let copyMarkdown = this.getData("setting_edit_copyMarkdown");
				copyMarkdown = (copyMarkdown === undefined || copyMarkdown === "") ? true : JSON.parse(copyMarkdown);
				
				let str = copyMarkdown ? ("![](" + this.imgUrl + ")") : this.imgUrl;
				
				if (autoPaste) {
					this.originalContent += str;
				}
				
				// 复制到剪贴板
				uni.setClipboardData({
					data: str,
					success: function () {
						that.toast("附件地址已复制");
					},
					fail:function(){
						that.toast("附件地址复制失败");
					}
				});
				
				// 复制到剪贴板后清空变量
				this.imgUrl = "";
			}
		},
		methods: {
			/**
			 * 获取文章数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				getPost(this.postId).then(data => {
					that.post = data;
					that.title = that.post.title;
					that.originalContent = that.post.originalContent;
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
				switch(e.index) {
					case 0:
						// 选择附件
						uni.navigateTo({
							url: "../../attachment/selectAttachment/selectAttachment?attrName=imgUrl"
						})
						break;
					case 1:
						// 预览
						let that = this;
						if (this.title.length <= 0) {
							this.popup("标题不能为空");
							return ;
						}
						if (this.type === "add") {
							// 新增文章状态下预览
								this.post.title = this.title;
								this.post.originalContent = this.originalContent;
								this.post.status = "DRAFT";
								
								// 先创建文章
								addPost(this.post).then(data => {
									// 创建文章成功，保存 id
									that.post.id = data.id;
									// 提交数据成功后获取预览 URL
									getPostPreviewLink(that.post.id).then(data => {
										that.openURL(data.data);
									}).catch(err => {
										uni.showModal({
											title: "获取预览地址失败",
											content: err
										});
									});
									// 因为当前已经创建了文章，所以切换为编辑模式
									that.type = "update";
									that.postId = data.id;
									that.refreshData();
								}).catch(err => {
									uni.showModal({
										title: "获取预览地址失败",
										content: err
									});
								});
						} else {
							// 编辑文章状态下预览
							this.post.title = this.title;
							this.post.originalContent = this.originalContent;
							updatePost(this.post.id, this.post).then(data => {
								// 提交数据成功后获取预览 URL
								getPostPreviewLink(that.post.id).then(data => {
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
				}
			},
			
			
			/**
			 * 保存草稿按钮点击事件
			 */
			saveDraft: function () {
				if (this.title.length <= 0) {
					this.popup("文章标题不能为空");
					return ;
				}
				
				if (this.type === "add") {
					// 新增文章的保存草稿也是先新建一个文章
					let that = this;
					let json = {"title" : this.title,
						"originalContent": this.originalContent};
					addPost(json).then(data => {
						// 创建文章成功，保存 id
						that.post.id = data.id;
						that.popup("保存草稿成功", "success");
						
						// 因为当前已经创建了文章，所以切换为编辑模式
						that.type = "update";
						that.postId = data.id;
						that.refreshData();
					}).catch(err => {
						uni.showModal({
							title: "保存草稿失败",
							content: err
						});
					});
				} else {
					// 编辑文章的保存草稿
					// 先将 post 的 status 改为 DRAFT
					this.post.status = "DRAFT";
					this.post.title = this.title;
					this.post.originalContent = this.originalContent;
					updatePost(this.post.id, this.post).then(data => {
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
			releasePost: function() {
				if (this.title.length <= 0) {
					this.popup("文章标题不能为空");
					return ;
				}
				this.post.title = this.title;
				this.post.originalContent = this.originalContent;
				uni.navigateTo({
					url:'../setting/setting'
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
		padding-bottom: 50rpx;
	}
	.block {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}
	
	.btn {
		color: #FFFFFF;
		height: 60rpx;
		line-height: 60rpx;
		margin-right: 20rpx;
		font-size: .9em;
	}
	
	.left-btn {
		display: inline-block;
		width: 47%;
		position: relative;
		
	}
	
	.right-btn {
		display: inline-block;
		width: 47%;
		position: absolute;
		margin-right: 0px;
		right: 0px;
	}
</style>
