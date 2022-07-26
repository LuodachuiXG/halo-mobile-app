<template>
	<view>
		<u-notify ref="popup" duration="1500"></u-notify>
		
		<u-empty 
			v-if="mPages.length === 0"
			mode="list"
			icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>

		<view class="block" v-for="(mPage, i) in mPages">
			<view @click="onPageClick(i)">
				<!-- 页面标题 -->
				<view class="block-name-view">
					<view class="block-name">
						<view class="tips-info topped-tag"
							style="margin-right: 15rpx;display: ;" v-if="mPage.topped">
							置顶
						</view>
						{{ mPage.title }}
					</view>
				</view>

				<!-- 缩略图 -->
				<view class="block-thumbnail">
					<!-- 判断缩略图是否是绝对地址 -->
					<image v-if="mPage.thumbnail" :src="mPage.thumbnail.indexOf('http') < 0  ? 
						getUrl() + mPage.thumbnail : mPage.thumbnail"></image>
				</view>

				<!-- 页面描述 -->
				<view class="block-summary" :style="mPage.thumbnail ? '' : 'border:none;'">
					<text>{{ mPage.summary }}</text>
				</view>
			</view>
			
			<!-- 评论量标签 -->
			<view class="block-tag">
				<view class="block-tag-item">
					<u-tag :text="'评论量：' + mPage.commentCount" type="error"></u-tag>
				</view>
			</view>
			<!-- 访问量标签 -->
			<view class="block-tag">
				<view class="block-tag-item" >
					<u-tag :text="'访问量：' + mPage.visits" type="primary"></u-tag>
				</view>
			</view>
			
			<!-- 显示页面状态 -->
			<view class="block-status">
				<!-- 根据回收站、草稿、公开、私密四个状态，设置不同的颜色和文字提示 -->
				<view class="block-status-point" v-if="mPage.status == 'PUBLISHED' || mPage.status == 'INTIMATE'"></view>
				<view class="block-status-point background-error" v-if="mPage.status == 'RECYCLE'"></view>
				<view class="block-status-point background-warning" v-if="mPage.status == 'DRAFT'"></view>
				<text>
					{{ mPage.status == 'DRAFT' ? '草稿' : ''}}{{ mPage.status == 'RECYCLE' ? '回收站' : ''}}{{ mPage.status == 'INTIMATE' ? '私密' : ''}}{{ mPage.status == 'PUBLISHED' ? '已发布' : ''}}
				</text>
				<view class="block-status-time">
					<text>{{ format(mPage.createTime) }}</text>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>
					<uni-col :span="8">
						<view class="block-action-item" @click="onEditClick(i)">
							<!-- 如果当前页面在回收站，就将编辑改为 还原 ，且垃圾桶颜色改为黄色 -->
						<text class="iconfont item-icon" :class="mPage.status == 'RECYCLE' ? 'color-warning' : ''">
							{{ mPage.status == "RECYCLE" ? "&#xe74e;" : "&#xe892;"}}
						</text>
							<text :class="mPage.status == 'RECYCLE' ? 'color-warning' : ''">
								{{ mPage.status == "RECYCLE" ? '还原' : '编辑'}}
							</text>
						</view>
					</uni-col>
					<uni-col :span="8">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<!-- 如果当前页面在回收站，就将删除改为 永久删除 ，且垃圾桶颜色改为红色 -->
					 		<text class="iconfont item-icon" :class="mPage.status == 'RECYCLE' ? 'color-error' : ''">
								&#xe74e;
							</text>
							<text :class="mPage.status == 'RECYCLE' ? 'color-error' : ''">
								{{ mPage.status == "RECYCLE" ? '永久删除' : '删除'}}
							</text>
						</view>
					</uni-col>
					<uni-col :span="8">
						<view class="block-action-item border" @click="onSettingClick(i)">
							<text class="iconfont item-icon">&#xe7f5;</text>
							设置
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>

		<view class="view-sizeSelect block" v-if="mPages.length > 0">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination 
			style="padding-bottom: 200rpx;margin-left: 20rpx;margin-right: 20rpx;" 
			title="页面"
			:pageSize="size" 
			:total="total" 
			:current="page + 1"
			@change="pageChange"
			v-if="mPages.length > 0"></uni-pagination>

		<!-- 回收站模式不显示悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onFabClick"></uni-fab>
	</view>
</template>

<script>
	import {
		getPages,
		updatePageStatus,
		deletePage
	} from "@/network/PageApi.js";
	export default {
		data() {
			return {
				// 当前页数
				page: 0,
				// 总页数
				pages: 0,
				// 总页面数
				total: 0,
				// 每页页面数量
				size: 8,

				// 存放页面信息
				mPages: [],

				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 1,
			}
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("pages_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认8条/页
				this.sizesIndex = 1;
			}

			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
			// 根据每页显示页面数量来设置 size 大小
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
				// 获取页面数据
				let page = this.page;
				let size = this.size;
				getPages(page, size).then(data => {
					this.mPages = data.content;
					
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
			 * 页面编辑按钮，当页面在回收站时，该按钮充当还原功能
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				let that = this;
				let page = this.mPages[i];
				if (page.status == "RECYCLE") {
					// 页面在回收站，充当还原功能
					uni.showModal({
						title: '提示',
						content: '确定要发布【' + that.mPages[i].title + '】页面吗？',
						success: function(res) {
							if (res.confirm) {
								updatePageStatus(page.id, "PUBLISHED").then(data => {
									that.popup("发布成功", "success");
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
				} else {
					// 编辑页面
					uni.navigateTo({
						url: "./edit/edit?id=" + this.mPages[i].id + "&type=update"
					})
				}
			},

			/**
			 * 页面删除按钮，当页面在回收站时，该按钮充当永久删除功能
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				let page = this.mPages[i];
				if (page.status === "RECYCLE") {
					// 当前页面在回收站，永久删除
					uni.showModal({
						title: '提示',
						content: '确定要永久删除【' + page.title + '】页面吗？',
						success: function(res) {
							if (res.confirm) {
								deletePage(page.id).then(data => {
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
					// 当前页面是正常状态，加入回收站
					uni.showModal({
						title: '提示',
						content: '确定要将【' + page.title + '】页面放入回收站吗？',
						success: function(res) {
							if (res.confirm) {
								updatePageStatus(page.id, "RECYCLE").then(data => {
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
				this.setData("pages_sizesIndex", this.sizesIndex)
				this.refreshData()
			},


			/**
			 * 页面设置点击事件
			 * @param {Object} i
			 */
			onSettingClick: function(i) {
				let pageId = this.mPages[i].id;
				uni.navigateTo({
					url: './setting/setting?id=' + pageId
				})
			},
			
			/**
			 * 查看页面点击事件
			 * @param {Object} i
			 */
			onPageClick: function(i) {
				this.openURL(this.getUrl() + this.mPages[i].fullPath)
			},

			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function(e) {
				// 新建页面
				uni.navigateTo({
					url: "./edit/edit?type=add"
				});
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
		margin-bottom: 50rpx;
	}

	.block-name-view{
		padding: 30rpx;
		padding-bottom: 20rpx;
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

	.block-thumbnail {}

	.block-thumbnail image {
		vertical-align: middle;
		width: 100%;
	}

	.block-summary {
		padding: 30rpx;
		padding-bottom: 20rpx;
		border-top: 1px solid #ececec;
	}

	.block-summary text {
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		color: var(--textPrimaryColor);
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

	.block-action-icon {
		font-size: 40rpx;
		position: relative;
		top: 5rpx;
	}

	.border {
		border-left: 1px solid #ececec;
	}

	.block-tag {
		margin-top: -20rpx;
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 20rpx;

	}

	.block-tag-item {
		margin-right: 20rpx;
		margin-top: 20rpx;
		display: inline-block;
	}

	.block-status {
		position: relative;
		padding-left: 30rpx;
		padding-right: 30rpx;
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

	.block-status-time image {
		position: absolute;
		top: 10rpx;
		left: -30rpx;
		width: 25rpx;
		height: 25rpx;
	}

	.view-sizeSelect {
		margin: 30rpx;
		margin-bottom: 40rpx;
		padding: 20rpx;
		color: #616255;
	}
	
	.topped-tag {
		background-color: var(--errorColor);
	}
</style>
