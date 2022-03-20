<template>
	<view>
		<u-notify ref="popup"></u-notify>
		
		<view class="block" v-for="(post, i) in posts">
			<view @click="onPostClick(i)">
				<!-- 文章名 -->
				<view class="block-name">
					<text>{{ post.title }}</text>
				</view>
				
				<!-- 缩略图 -->
				<view class="block-thumbnail">
					<!-- 判断缩略图是否是绝对地址 -->
					<image v-if="post.thumbnail" :src="post.thumbnail.indexOf('http') < 0  ? 
						getUrl() + post.thumbnail : post.thumbnail"></image>
				</view>
				
				<!-- 文章总结 -->
				<view class="block-summary">
					<text>{{ post.summary }}</text>
				</view>
			</view>
			
			<!-- 文章分类标签 -->
			<view class="block-tag" v-if="post.categories.length > 0">
				<uni-tag class="block-tag-item" type="primary" :inverted="true"
					v-for="(categorie, j) in post.categories" :text="categorie.name"></uni-tag>
			</view>
			
			<!-- 文章标签 -->
			<view class="block-tag" v-if="post.tags.length > 0">
				<uni-tag class="block-tag-item" type="success" :inverted="true"
					v-for="(tag, j) in post.tags" :text="tag.name"></uni-tag>
			</view>
			
			<!-- 显示文章状态 -->
			<view class="block-status">
				<!-- 根据回收站、草稿、公开、私密四个状态，设置不同的颜色和文字提示 -->
				<view class="block-status-point" 
					v-if="post.status == 'PUBLISHED' || post.status == 'INTIMATE'"></view>
				<view class="block-status-point background-error" v-if="post.status == 'RECYCLE'"></view>
				<view class="block-status-point background-warning" v-if="post.status == 'DRAFT'"></view>
				<text>
					{{ post.status == 'DRAFT' ? '草稿' : ''}}{{ post.status == 'RECYCLE' ? '回收站' : ''}}{{ post.status == 'INTIMATE' ? '私密' : ''}}{{ post.status == 'PUBLISHED' ? '已发布' : ''}}
				</text>
				<view class="block-status-time">
					<image src="../../static/images/clock.png"></image>
					<text>{{ format(post.createTime) }}</text>
				</view>
			</view>
			
			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>
					<uni-col :span="6">
						<view class="block-action-item" @click="onEditClick(i)">
							<!-- 如果当前文章在回收站，就将编辑改为 删除 ，且垃圾桶颜色改为红色 -->
							<image :src="post.status == 'RECYCLE' ? '../../static/images/trash_red.png' : '../../static/images/edit.png'"
								style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;"></image>
							<text :class="post.status == 'RECYCLE' ? 'color-error' : ''">
								{{ post.status == "RECYCLE" ? '删除' : '编辑'}}
							</text>
						</view>
					</uni-col>
					<uni-col :span="6">
						<view class="block-action-item border" @click="onSettingClick(i)">
							<image src="../../static/images/setting.png"
								style="width: 35rpx; height: 35rpx;top: 8rpx;margin-top: -8rpx;"></image>
							设置
						</view>
					</uni-col>
					<uni-col :span="6">
						<view class="block-action-item border" @click="onPostDataClick(i)">
							<image src="../../static/images/data.png"
								style="width: 35rpx; height: 35rpx;top: 8rpx;margin-top: -8rpx;"></image>
							数据
						</view>
					</uni-col>
					<uni-col :span="6">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<!-- 如果当前文章在回收站，就将文字改为 还原 ，且垃圾桶颜色改为黄色 -->
							<image :src="post.status == 'RECYCLE' ? '../../static/images/trash_yellow.png' : '../../static/images/trash.png'"
								style="width: 35rpx; height: 35rpx;top: 7rpx;margin-top: -7rpx;"></image>
							<text :class="post.status == 'RECYCLE' ? 'color-warning' : ''">
								{{ post.status == "RECYCLE" ? '还原' : '回收'}}
							</text>
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
		
		<view class="view-sizeSelect block">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination style="padding-bottom: 80rpx;margin-left: 20rpx;margin-right: 20rpx;" title="文章" 
		:pageSize="size" :total="total" :current="page + 1" @change="pageChange"></uni-pagination>
		
		
	</view>
</template>

<script>
	import {
		getPosts,
		updatePostStatus,
		deletePosts
	} from "../../common/api.js";
	export default {
		data() {
			return {
				// 当前页数
				page: 0,
				// 总页数
				pages: 0,
				// 总文章数
				total: 0,
				// 每页文章数量
				size: 8,

				// 存放文章信息
				posts: [],
				
				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 1,
				popupType: "",
				popupMessage: "",
			}
		},
		
		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("posts_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认16条/页
				this.sizesIndex = 1;
			}
			
			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
			// 根据每页显示文章数量来设置 size 大小
			switch(this.sizesIndex) {
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
			this.refreshData()
		},
		
		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData()
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
				getPosts(this.page, this.size).then(data => {
					// 保存文章数组
					that.posts = data.content;
					// 保存文章总数
					that.total = data.total;
					// 保存总页数
					that.pages = data.pages;
					
					uni.stopPullDownRefresh()
					uni.hideLoading()
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},
			
			
			/**
			 * 文章编辑按钮，当文章在回收站时，该按钮充当删除功能
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				let that = this;
				let post = this.posts[i];
				if (post.status == "RECYCLE") {
					// 文章在回收站，充当删除功能
					uni.showModal({
						title: '提示',
						content: '确定要永久删除【' + that.posts[i].title + '】文章吗？此操作不可逆。',
						success: function(res) {
							if (res.confirm) {
								deletePosts(post.id).then(data => {
									that.popup("删除成功", "success");
									that.refreshData();
								}).catch(err => {
									uni.stopPullDownRefresh();
									uni.showModal({
										title: "删除失败",
										content: err
									});
								});
							}
						}
					});
				} else {
					// 编辑文章
				}
			},
			
			/**
			 * 文章数据单击事件
			 * @param {Object} i
			 */
			onPostDataClick: function(i) {
				let that = this;
				let post = this.posts[i];
				uni.showModal({
					showCancel: false,
					content: "文章ID：" + post.id + "\n" + 
						"文章标题：" + post.title + "\n" +
						"文章别名：" + post.slug + "\n" + 
						"编辑类型：" + post.editorType + "\n" + 
						"更新时间：" + that.format(post.updateTime) + "\n" + 
						"创建时间：" + that.format(post.createTime) + "\n" + 
						"编辑时间：" + that.format(post.editTime) + "\n" + 
						"浏览次数：" + post.visits + "\n" + 
						"喜欢次数：" + post.likes + "\n" +
						"全部字数：" + post.wordCount + "\n" + 
						"评论数量：" + post.commentCount + "\n" + 
						"是否置顶：" + (post.topped ? "是" : "否")
				})
			},
			
			/**
			 * 将文章回收单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				if (this.posts[i].status == "RECYCLE") {
					// 当前文章在回收站，准备还原
					uni.showModal({
						title: '提示',
						content: '确定要发布【' + that.posts[i].title + '】文章吗？',
						success: function(res) {
							if (res.confirm) {
								updatePostStatus(that.posts[i].id, "PUBLISHED").then(data => {
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
					// 当前文章是正常状态，准备回收
					uni.showModal({
						title: '提示',
						content: '确定要将【' + that.posts[i].title + '】放入回收站吗？',
						success: function(res) {
							if (res.confirm) {
								updatePostStatus(that.posts[i].id, "RECYCLE").then(data => {
									that.popup("成功放入回收站", "success");
									that.refreshData();
								}).catch(err => {
									uni.stopPullDownRefresh();
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
				switch(i) {
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
				this.setData("posts_sizesIndex", this.sizesIndex)
				this.refreshData()
			},
			
			/**
			 * 查看文章点击事件
			 * @param {Object} i
			 */
			onPostClick: function(i) {
				uni.navigateTo({
					url: './detail/detail?id=' + this.posts[i].id
				})
			},
			
			/**
			 * 文章设置点击事件
			 * @param {Object} i
			 */
			onSettingClick: function(i) {
				let postId = this.posts[i].id;
				uni.navigateTo({
					url:'./setting/setting?id=' + postId
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
	.block {
		position: relative;
	}

	.block{
		margin-bottom: 50rpx;
	}
	

	.block-name {
		padding: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #ececec;
	}
	.block-name text {
		font-weight: bold;
		font-size: 1.1em;
		display: inline-block;
		white-space: nowrap; 
		width: 100%; 
		overflow: hidden;
		text-overflow:ellipsis;
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
		overflow:hidden; 
		text-overflow:ellipsis;
		display:-webkit-box; 
		-webkit-box-orient:vertical;
		-webkit-line-clamp:2; 
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

	.tips {
		font-weight: bold;
		font-size: .8em;
	}
	.block-tag {
		margin-top: -20rpx;
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 20rpx;
	}
	.block-tag-item {
		margin-right: 20rpx;
		font-weight: bold;
		
	}
	.block-tag-item:first-child {
		margin-left: 0rpx;
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
</style>
