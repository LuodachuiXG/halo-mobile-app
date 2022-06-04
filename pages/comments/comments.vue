<template>
	<view>
		<u-notify ref="popup" duration="1500"></u-notify>

		<!-- 评论筛选组件 -->
		<u-sticky v-if="mode === 'all'">
			<uni-collapse>
				<uni-collapse-item title="评论筛选" :open="collapseOpen">
					<view>
						<view class="view-input">
							<view class="view-input-titleView">关键词：</view>
							<input class="input" type="text" v-model="keyword" />
						</view>
						<view class="view-input">
							<view class="view-input-titleView">评论分类：</view>
							<view class="input">
								<picker @change="commentTypeChange" :value="comment_typeIndex"
									:range="comment_typeText">
									<view style="height: 25px;line-height: 25px;">
										{{comment_typeText[comment_typeIndex]}}
									</view>
								</picker>
							</view>
						</view>
						<view class="view-input">
							<view class="view-input-titleView">评论状态：</view>
							<view class="input">
								<picker @change="commentStatusChange" :value="comment_statusIndex"
									:range="comment_statusText">
									<view style="height: 25px;line-height: 25px;">
										{{comment_statusText[comment_statusIndex]}}
									</view>
								</picker>
							</view>
						</view>
						<view class="view-input" style="position: relative;margin-top: 20px;">
							<button class="btn left-btn" type="primary" @click="refreshData">查询</button>
							<button class="btn right-btn" type="default" @click="reset">重置</button>
						</view>
					</view>
				</uni-collapse-item>
			</uni-collapse>
		</u-sticky>

		<!-- 批量操作模式的吸顶按钮 -->
		<u-sticky v-if="mode === 'batch'">
			<view class="recycle-sticky">
				<uni-row>
					<uni-col :span="5">
						<u-checkbox-group>
							<button @click="onSelectAllClick" :disabled="comments.length === 0"
								:class="comments.length > 0 && (selectedComment.length === comments.length) ? 'blue' : ''">
								{{ comments.length > 0 && (selectedComment.length === comments.length) ? "取消" : "全选" }}
							</button>
						</u-checkbox-group>
					</uni-col>
					<uni-col :span="5">
						<picker @change="batchBatchChange" :value="batchBatchIndex" :range="batchBatch"
							:disabled="selectedComment.length === 0">
							<button class="blue" :disabled="selectedComment.length === 0">批量操作</button>
						</picker>
					</uni-col>
					<uni-col :span="9" :push="1">
						<button class="yellow" @click="onReturnAllClick">关闭批量操作模式</button>
					</uni-col>
				</uni-row>
			</view>
		</u-sticky>

		<u-empty v-if="comments.length === 0" mode="list" icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>


		<view class="block" v-for="(comment, i) in comments"
			:class="(selectedComment.indexOf(comment.id) >= 0) ? 'selected' : ''">
			<view @click="onCommentClick(i)">
				<!-- 评论用户名 -->
				<view class="block-name-view">
					<view class="block-name">
						<text style="margin-right: 10rpx;color: var(--primaryColor);">#{{comment.id}}</text>
						{{ comment.author }}
						<view class="tips-info admin-tag" style="margin-left: 15rpx;" v-if="comment.isAdmin">
							博主
						</view>
					</view>
				</view>

				
				<view class="block-content">
					<!-- 评论的地方 -->
					<view class="block-postTitle" @click="onTitleClick(i)">
						{{ type == "posts" && comment.post != undefined ? comment.post.title : ""}}
						{{ type == "sheets" && comment.sheet != undefined ? comment.sheet.title : ""}}
						{{ type == "journals" && comment.journal != undefined ? comment.journal.title : ""}}
					</view>
					<!-- 评论id -->
					<text style="font-weight: bold;color: var(--primaryColor);margin-right: 5px;"
						v-if="comment.parentId !== 0">
						@{{comment.parentId}}
					</text>
					<!-- 评论内容 -->
					<text>{{ comment.content }}</text>
				</view>
				
				<!-- 显示评论状态 -->
				<view class="block-status">
					<!-- 根据已发布、待审核、回收站三个状态，设置不同的颜色和文字提示 -->
					<view class="block-status-point" v-if="comment.status == 'PUBLISHED'"></view>
					<view class="block-status-point background-error" v-if="comment.status == 'RECYCLE'"></view>
					<view class="block-status-point background-warning" v-if="comment.status == 'AUDITING'"></view>
					<text>
						{{ comment.status == 'AUDITING' ? '待审核' : ''}}{{ comment.status == 'RECYCLE' ? '回收站' : ''}}{{ comment.status == 'PUBLISHED' ? '已发布' : ''}}
					</text>
					<view class="block-status-time">
						<text>{{ format(comment.createTime) }}</text>
					</view>
				</view>
			</view>


			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>

					<uni-col :span="12">
						<view @click="onReplyClick(i)">
							<view class="block-action-item">
								<view v-if="comment.status !== 'RECYCLE'">
									<!-- 如果当前评论是待审核，就将回复改为通过 -->
									<image
										:src="comment.status == 'AUDITING' ? '../../static/images/ok_green.png' : '../../static/images/edit.png'"
										style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;">
									</image>
									<text :class="comment.status == 'AUDITING' ? 'color-success' : ''">
										{{ comment.status == "AUDITING" ? '通过' : '回复'}}
									</text>
								</view>

								<!-- 回收站状态 -->
								<view v-else>
									<image src="../../static/images/trash_yellow.png"
										style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;">
									</image>
									<text class="color-warning">
										还原
									</text>
								</view>
							</view>
						</view>
					</uni-col>


					<uni-col :span="12">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<!-- 如果当前评论在回收站，就将文字改为删除 -->
							<image
								:src="comment.status == 'RECYCLE' ? '../../static/images/trash_red.png' : '../../static/images/trash.png'"
								style="width: 35rpx; height: 35rpx;top: 7rpx;margin-top: -7rpx;"></image>
							<text :class="comment.status == 'RECYCLE' ? 'color-error' : ''">
								{{ comment.status == "RECYCLE" ? '删除' : '回收站'}}
							</text>
						</view>
					</uni-col>

				</uni-row>
			</view>
		</view>

		<view class="view-sizeSelect block" v-if="comments.length > 0">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination style="padding-bottom: 200rpx;margin-left: 20rpx;margin-right: 20rpx;" title="评论"
			:pageSize="size" :total="total" :current="page + 1" @change="pageChange" v-if="comments.length > 0">
		</uni-pagination>

		<!-- 批量操作模式不显示悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @trigger="onFabClick" :content="content" v-if="mode !== 'batch'">
		</uni-fab>

	</view>
</template>

<script>
	import {
		getComments,
		updateCommentStatus,
		replyComment,
		deleteComment,
		updateCommentsStatus,
		deleteComments
	} from "@/network/CommentApi.js";
	export default {
		data() {
			return {
				// 控制评论筛选折叠面板是否打开
				collapseOpen: false,
				
				// 评论筛选的评论分类的 picker 选项
				comment_typeText: ["文章", "页面", "日志"],
				comment_typeValue: ["posts", "sheets", "journals"],
				comment_typeIndex: 0,

				// 评论筛选的评论状态的 picker 选项
				comment_statusText: ["所有状态", "已发布", "待审核", "回收站"],
				comment_statusValue: ["", "PUBLISHED", "AUDITING", "RECYCLE"],
				comment_statusIndex: 0,

				// 评论筛选的关键词
				keyword: "",

				// 当前页数
				page: 0,
				// 总页数
				pages: 0,
				// 总评论数
				total: 0,
				// 每页评论数量
				size: 8,

				// 存放评论信息
				comments: [],

				// 批量操作状态下选中的评论
				selectedComment: [],

				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 1,

				// 批量操作模式的批量操作的 picker 选项
				batchBatch: ["通过", "放入回收站", "永久删除"],
				batchBatchIndex: 0,

				// 悬浮按钮弹出菜单
				content: [{
					"text": "批量操作",
					"iconPath": "/static/images/checkbox.png"
				}],

				// 当前页面是什么模式，展示评论/批量操作  all/batch
				mode: "all",
				// 当前显示的是什么评论，文章评论/页面评论/日志评论  posts/sheets/journals
				type: "posts"
			}
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("comments_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认8条/页
				this.sizesIndex = 1;
			}

			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
			// 根据每页显示评论数量来设置 size 大小
			switch (this.sizesIndex) {
				case 0:
					this.size = 4
					break;
				case 1:
					this.size = 8
					break;
				case 2:
					this.size = 16
					break;
				case 3:
					this.size = 24
					break;
				case 4:
					this.size = 48
					break;
				case 5:
					this.size = 96
					break;
			}
		},

		onShow() {
			this.refreshData();
		},

		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData();
		},
		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				uni.showLoading({
					title: "正在加载"
				});
				let that = this;
				// 获取评论数据
				let page = this.page;
				let size = this.size;
				let keyword = this.keyword;
				let status = this.comment_statusValue[this.comment_statusIndex];
				let type = this.comment_typeValue[this.comment_typeIndex];
				getComments(page, size, keyword, status, type).then(data => {
					// 保存评论数组
					that.comments = data.content;
					// 清空已选择的评论
					that.selectedComment = [];
					// 保存评论总数
					that.total = data.total;
					// 保存总页数
					that.pages = data.pages;

					uni.stopPullDownRefresh();
					uni.hideLoading();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},

			/**
			 * 评论回复按钮点击事件
			 * 当评论是待审核时，该按钮充当通过功能
			 * 当评论是回收站时，该按钮充当还原功能
			 * @param {Object} i
			 */
			onReplyClick: function(i) {
				let that = this;
				let comment = this.comments[i];
				let status = comment.status;
				let id = "";
				switch(this.type) {
					case "posts":
						id = comment.post.id;
						break;
					case "sheets":
						id = comment.sheet.id;
						break;
					case "journals":
						id = comment.journal.id;
						break;
				}
				
				if (status === "RECYCLE" || status === "AUDITING") {
					// 评论在回收站，充当还原(公开-PUBLISHED)功能
					// 评论在待审核，充当通过(公开-PUBLISHED)功能
					let str = (status === "RECYCLE" ? "还原" : "通过");
					uni.showModal({
						title: "提示",
						content: "确定要" + str + "该评论吗？",
						success: function(res) {
							if (res.confirm) {
								updateCommentStatus(comment.id, "PUBLISHED", that.type).then(data => {
									that.popup(str + "成功", "success");
									that.refreshData();
								}).catch(err => {
									uni.showModal({
										title: str + "失败",
										content: err
									});
								});
							}
						}
					});
				} else {
					// 回复评论
					uni.showModal({
						title: "回复评论",
						placeholderText: "请输入回复",
						editable: true,
						success: function(res) {
							if (res.confirm) {
								let content = res.content;
								replyComment(id, content, comment.id, that.type).then(data => {
									that.popup("回复成功", "success");
									that.refreshData();
								}).catch(err => {
									uni.showModal({
										title: "回复失败",
										content: err
									});
								});
							}
						}
					})
				}
			},


			/**
			 * 评论回收站单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				let comment = this.comments[i];
				let status = comment.status;
				if (status == "RECYCLE") {
					// 当评论在回收站时，充当永久删除功能
					uni.showModal({
						title: '提示',
						content: '确定要永久删除该评论吗？此操作不可逆。',
						success: function(res) {
							if (res.confirm) {
								deleteComment(comment.id, that.type).then(data => {
									that.popup("删除成功", "success");
									that.refreshData();
								}).catch(err => {
									uni.showModal({
										title: "删除失败",
										content: err
									});
								});
							}
						}
					});
				} else {
					// 当评论是正常状态，充当回收站功能
					uni.showModal({
						title: '提示',
						content: '确定要将该评论放入回收站吗？',
						success: function(res) {
							if (res.confirm) {
								updateCommentStatus(comment.id, "RECYCLE", that.type).then(data => {
									that.popup("成功放入回收站", "success");
									that.refreshData();
								}).catch(err => {
									uni.showModal({
										title: "放入回收站失败",
										content: err
									});
								});
							}
						}
					});
				}
			},

			/**
			 * 改变页面事件
			 */
			pageChange: function(e) {
				let current = e.current
				this.page = current - 1
				this.refreshData()
			},

			/**
			 * 更改每页显示条数事件
			 * @param {Object} e
			 */
			sizesChange: function(e) {
				let i = e.detail.value
				this.sizesIndex = i
				this.page = 0
				switch (i) {
					case 0:
						this.size = 4
						break;
					case 1:
						this.size = 8
						break;
					case 2:
						this.size = 16
						break;
					case 3:
						this.size = 24
						break;
					case 4:
						this.size = 48
						break;
					case 5:
						this.size = 96
						break;
				}
				// 将每页几条数据设置保存到本地
				this.setData("comments_sizesIndex", this.sizesIndex)
				this.refreshData()
			},

			/**
			 * 评论中蓝色的评论标题点击事件
			 */
			onTitleClick: function(i) {
				// 跳转评论网页
				// 如果当前是批量操作模式，不进行网页跳转
				let url = "";
				switch(this.type) {
					case "posts":
						url = this.comments[i].post.fullPath;
						break;
					case "sheets":
						url = this.comments[i].sheet.fullPath;
						break;
					case "journals":
						url = this.comments[i].journals.fullPath;
						break;
				}
				
				if (this.mode !== "batch") {
					this.openURL(this.getUrl() + url);
				}
				
			},

			/**
			 * 评论点击选中事件
			 * @param {Object} i
			 */
			onCommentClick: function(i) {
				// 如果当前是批量操作模式，点击评论是选择评论
				if (this.mode === "batch") {
					let id = this.comments[i].id;
					let index = this.selectedComment.indexOf(id);
					if (index < 0) {
						this.selectedComment.push(id);
					} else {
						this.selectedComment.splice(index, 1);
					}
				}

			},

			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function(e) {
				switch (e.index) {
					// 批量操作
					case 0:
						this.mode = "batch";
						this.selectedComment = [];
						break;
				}

			},
			
			/**
			 * 评论筛选分类选项更改事件
			 * @param {Object} e
			 */
			commentTypeChange: function(e) {
				let i = e.detail.value;
				this.comment_typeIndex = i;
				switch(i) {
					case 0:
						this.type = "posts";
						break;
					case 1:
						this.type = "sheets";
						break;
					case 2:
						this.type = "journals";
						break;
				}
				this.refreshData();
			},

			/**
			 * 评论筛选状态选项更改事件
			 * @param {Object} e
			 */
			commentStatusChange: function(e) {
				this.comment_statusIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 重置评论筛选选项
			 */
			reset: function() {
				this.keyword = "";
				this.comment_statusIndex = 0;
				this.refreshData();
			},


			/**
			 * 返回展示所有评论模式
			 */
			onReturnAllClick: function() {
				this.mode = "all";
				uni.setNavigationBarTitle({
					title: "所有评论"
				});
				this.refreshData();
			},

			/**
			 * 全选按钮点击事件
			 */
			onSelectAllClick: function() {
				if (this.comments.length === this.selectedComment.length) {
					this.selectedComment = [];
				} else {
					this.selectedComment = [];
					for (var i = 0; i < this.comments.length; i++) {
						this.selectedComment.push(this.comments[i].id);
					}
				}
			},



			/**
			 * 批量操作模式的批量操作 picker 选择事件
			 * @param {Object} e
			 */
			// ["通过", "放入回收站", "永久删除"]
			batchBatchChange: function(e) {
				let that = this;
				switch (e.detail.value) {
					// 通过
					case 0:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedComment.length + ' 个评论转为已发布状态吗？',
							success: function(res) {
								if (res.confirm) {
									updateCommentsStatus(that.selectedComment, "PUBLISHED", that.type).then(data => {
										that.toast("发布成功", "success")
										that.refreshData();
									}).catch(err => {
										uni.showModal({
											title: "发布失败",
											content: err
										});
									});
								}
							}
						});
						break;
					// 放入回收站
					case 1:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedComment.length + ' 个评论放入回收站吗？',
							success: function(res) {
								if (res.confirm) {
									updateCommentsStatus(that.selectedComment, "RECYCLE", that.type).then(data => {
										that.toast("操作成功", "success")
										that.refreshData();
									}).catch(err => {
										uni.showModal({
											title: "操作失败",
											content: err
										});
									});
								}
							}
						});
						break;
					// 永久删除
					case 2:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedComment.length + ' 个评论永久删除吗？此操作不可逆。',
							success: function(res) {
								if (res.confirm) {
									let thatt = that;
									uni.showModal({
										title: '提示',
										content: '确定要删除所有评论吗？',
										success: function(res) {
											if (res.confirm) {
												deleteComments(thatt.selectedComment, that.type).then(data => {
													thatt.toast("删除成功", "success")
													thatt.refreshData();
												}).catch(err => {
													uni.showModal({
														title: "删除失败",
														content: err
													});
												});
											}
										}
									});
								}
							}
						});
						break;
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
		position: relative;
		margin-bottom: 10px;
	}

	.block-name-view {
		padding: 20rpx;
		padding-bottom: 10rpx;
		padding-left: 30rpx;
		border-bottom: 1px solid #ececec;
	}

	.block-name {
		font-weight: bold;
		font-size: 29rpx;
		display: inline-block;
		white-space: nowrap;
		width: 100%;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 41rpx;
	}


	.block-content {
		padding: 30rpx;
		padding-bottom: 10rpx;
		padding-top: 20rpx;
	}

	.block-content text {
		font-size: 1em;
	}

	.block-postTitle {
		color: var(--primaryColor);
		margin-bottom: 5px;
	}

	.block-action {
		margin-top: 20rpx;
		border-top: 1px solid #ececec;
		color: #898989;
	}

	.block-action-item {
		position: relative;
		padding: 30rpx;
		text-align: center;
	}

	.block-action-item:active {
		background-color: var(--activatedColor);
	}

	.block-action-item image {
		position: relative;
		top: 10rpx;
		margin-top: -10rpx;
		width: 40rpx;
		height: 40rpx;
	}

	.border {
		border-left: 1px solid #ececec;
	}

	.activated {
		color: #1890FF;
	}


	.block-status {
		position: relative;
		padding-left: 30rpx;
		padding-right: 30rpx;
		margin-top: 10px;
	}

	.block-status-point {
		width: 15rpx;
		height: 15rpx;
		background-color: var(--successColor);
		border-radius: 9999px;
		display: inline-block;
	}

	.background-error {
		background-color: var(--errorColor);
	}

	.background-warning {
		background-color: var(--warningColor);
	}

	.color-error {
		color: var(--errorColor);
	}

	.color-warning {
		color: var(--warningColor);
	}

	.color-success {
		color: var(--successColor);
	}

	.block-status text {
		margin-left: 10rpx;
		color: var(--textPrimaryColor);
		font-size: .9em;
	}

	.block-status-time {
		position: absolute;
		right: 30rpx;
		top: 0rpx;
	}

	.view-sizeSelect {
		margin: 30rpx;
		margin-bottom: 40rpx;
		padding: 20rpx;
		color: #616255;
	}

	.view-input {
		margin-top: 0rpx;
		margin-left: 30rpx;
		margin-right: 30rpx;
	}

	.btn {
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

	.recycle-sticky {
		background-color: #FFFFFF;
		height: 80rpx;
		box-shadow: 0px 1px 4px rgb(50, 50, 50, .2);
	}

	.recycle-sticky button {
		height: 60rpx;
		font-size: .9em;
		margin-top: 10rpx;
		line-height: 60rpx;
		border: none;
	}

	.recycle-sticky-picker {
		height: 60rpx;
		line-height: 60rpx;
		margin-top: 10rpx;
		text-align: center;
	}

	.red {
		background-color: var(--errorColor);
		color: #FFFFFF;
	}

	.blue {
		background-color: var(--primaryColor);
		color: #FFFFFF;
	}

	.yellow {
		background-color: var(--warningColor);
		color: #FFFFFF;
	}

	.selected {
		filter: brightness(60%) blur(1px);
	}

	.admin-tag {
		background-color: var(--primaryColor);
	}
</style>
