<template>
	<view>
		<u-notify ref="popup" duration="1500"></u-notify>

		<!-- 文章筛选组件，回收站模式不显示 -->
		<u-sticky v-if="mode === 'all'">
			<uni-collapse>
				<uni-collapse-item title="文章筛选" :open="collapseOpen">
					<view>
						<view class="view-input">
							<view class="view-input-titleView">关键词：</view>
							<input class="input" type="text" v-model="keyword" />
						</view>
						<view class="view-input">
							<view class="view-input-titleView">文章状态：</view>
							<view class="input">
								<picker @change="postStatusChange" :value="post_statusIndex" :range="post_statusText">
									<view style="height: 25px;line-height: 25px;">{{post_statusText[post_statusIndex]}}
									</view>
								</picker>
							</view>
						</view>
						<view class="view-input">
							<view class="view-input-titleView">分类目录：</view>
							<view class="input">
								<picker @change="categoriesChange" :value="categoriesIndex" :range="categoriesText">
									<view style="height: 25px;line-height: 25px;">{{categoriesText[categoriesIndex]}}
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
							<button @click="onSelectAllClick" :disabled="posts.length === 0"
								:class="posts.length > 0 && (selectedPost.length === posts.length) ? 'blue' : ''">
								{{ posts.length > 0 && (selectedPost.length === posts.length) ? "取消" : "全选" }}
							</button>
						</u-checkbox-group>
					</uni-col>
					<uni-col :span="5">
						<picker @change="batchBatchChange" :value="batchBatchIndex"
							:range="batchBatch" :disabled="selectedPost.length === 0">
							<button class="blue" :disabled="selectedPost.length === 0">批量操作</button>
						</picker>
					</uni-col>
					<uni-col :span="9" :push="1">
						<button class="yellow" 
							@click="onReturnAllClick">关闭批量操作模式</button>
					</uni-col>
				</uni-row>
			</view>
		</u-sticky>
		

		<!-- 回收站模式的吸顶按钮 -->
		<u-sticky v-if="mode === 'recycle'">
			<view class="recycle-sticky">
				<uni-row>
					<uni-col :span="5">
						<u-checkbox-group>
							<button @click="onSelectAllClick" :disabled="posts.length === 0"
								:class="posts.length > 0 && (selectedPost.length === posts.length) ? 'blue' : ''">
								{{ posts.length > 0 && (selectedPost.length === posts.length) ? "取消" : "全选" }}
							</button>
						</u-checkbox-group>
					</uni-col>
					<uni-col :span="5">
						<picker @change="recycleBatchChange" :value="recycleBatchIndex"
							:range="recycleBatch" :disabled="selectedPost.length === 0">
							<button class="blue" :disabled="selectedPost.length === 0">批量操作</button>
						</picker>
					</uni-col>
					<uni-col :span="6" :push="1">
						<button class="red" :disabled="posts.length === 0" 
							@click="onDeleteCurrentPageClick">删除当前页</button>
					</uni-col>
					<uni-col :span="5" :push="2">
						<button class="yellow" @click="onReturnAllClick">返回</button>
					</uni-col>
				</uni-row>
			</view>
		</u-sticky>
		
		<u-empty 
			v-if="posts.length === 0"
			mode="list"
			icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>

		<view class="block" v-for="(post, i) in posts"  
			:class="(selectedPost.indexOf(post.id) >= 0) ? 'selected' : ''">
			<view @click="onPostClick(i)">
				<!-- 文章名 -->
				<view class="block-name-view">
					<view class="block-name">
						<view class="tips-info topped-tag"
							style="margin-right: 15rpx;display: ;" v-if="post.topped && mode !== 'recycle'">
							置顶
						</view>
						{{ post.title }}
					</view>
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
				<view class="block-tag-item" v-for="(category, j) in post.categories">
					<u-tag :text="category.name" plain plainFill type="primary"
						@click="onCategoryTagClick(category.id)"></u-tag>
				</view>
			</view>
			<!-- 文章标签 -->
			<view class="block-tag" v-if="post.tags.length > 0">
				<view class="block-tag-item" v-for="(tag, j) in post.tags">
					<u-tag :text="tag.name" plain plainFill type="success"
						:bgColor="tag.color" :borderColor="tag.color" color="#000000"></u-tag>
				</view>
			</view>

			<!-- 显示文章状态 -->
			<view class="block-status">
				<!-- 根据回收站、草稿、公开、私密四个状态，设置不同的颜色和文字提示 -->
				<view class="block-status-point" v-if="post.status == 'PUBLISHED' || post.status == 'INTIMATE'"></view>
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
							<image
								:src="post.status == 'RECYCLE' ? '../../static/images/trash_red.png' : '../../static/images/edit.png'"
								style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;">
							</image>
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
							<image
								:src="post.status == 'RECYCLE' ? '../../static/images/trash_yellow.png' : '../../static/images/trash.png'"
								style="width: 35rpx; height: 35rpx;top: 7rpx;margin-top: -7rpx;"></image>
							<text :class="post.status == 'RECYCLE' ? 'color-warning' : ''">
								{{ post.status == "RECYCLE" ? '还原' : '回收'}}
							</text>
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>

		<view class="view-sizeSelect block" v-if="posts.length > 0">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination 
			style="padding-bottom: 200rpx;margin-left: 20rpx;margin-right: 20rpx;" 
			title="文章"
			:pageSize="size" 
			:total="total" 
			:current="page + 1"
			@change="pageChange"
			v-if="posts.length > 0"></uni-pagination>

		<!-- 回收站模式不显示悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @trigger="onFabClick" :content="content"
			v-if="mode === 'all'"></uni-fab>
	</view>
</template>

<script>
	import {
		getPosts,
		updatePostStatus,
		updatePostsStatus,
		deletePosts,
		deletePost
	} from "@/network/PostApi.js";
	
	import {
		getCategories
	} from "@/network/CategoryApi.js";
	export default {
		data() {
			return {
				// 控制文章筛选折叠面板是否打开
				collapseOpen: false,

				// 文章筛选中文章状态的 picker 选项
				post_statusText: ["所有状态", "已发布", "草稿", "私密"],
				post_statusValue: ["", "PUBLISHED", "DRAFT", "INTIMATE"],
				post_statusIndex: 0,

				// 文章筛选中关键词
				keyword: "",

				// 文章筛选中分类目录的 picker 选项
				categories: [],
				categoriesText: ["所有分类"],
				categoriesValue: [""],
				categoriesIndex: 0,


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
				selectedPost: [],

				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 1,

				// 回收站模式的批量操作的 picker 选项
				recycleBatch: ["发布", "转为草稿", "永久删除"],
				recycleBatchIndex: 0,
				
				// 批量操作模式的批量操作的 picker 选项
				batchBatch: ["发布", "转为草稿", "删除"],
				batchBatchIndex: 0,

				// 悬浮按钮弹出菜单
				content: [{
						"text": "写文章",
						"iconPath": "/static/images/edit.png"
					},
					{
						"text": "批量操作",
						"iconPath": "/static/images/checkbox.png"
					},
					{
						"text": "回收站",
						"iconPath": "/static/images/trash.png"
					}
				],

				// 当前页面是什么模式，展示文章/批量操作/回收站/  all/batch/recycle
				mode: "all",
			}
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("posts_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认8条/页
				this.sizesIndex = 1;
			}

			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
			// 根据每页显示文章数量来设置 size 大小
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
			this.refreshCategoryData();
		},

		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData()
			this.refreshCategoryData();
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
				// 获取文章数据
				let page = this.page;
				let size = this.size;
				let keyword = this.keyword;
				let status = this.post_statusValue[this.post_statusIndex];
				let categoryId = this.categoriesValue[this.categoriesIndex];
				if (this.mode === "recycle") {
					// 回收站模式只获取回收站状态的文章
					status = "RECYCLE";
				}
				getPosts(page, size, keyword, status, categoryId).then(data => {
					// 保存文章数组
					that.posts = data.content;
					// 清空已选择的文章
					that.selectedPost = [];
					// 保存文章总数
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
			 * 刷新分类目录数据
			 */
			refreshCategoryData: function() {
				let that = this;
				getCategories().then(data => {
					that.categories = data;
					// 对数据格式化以配合 picker 组件
					that.categoriesText = ["所有分类"];
					that.categoriesValue = [""];
					for (var i = 0; i < that.categories.length; i++) {
						let category = that.categories[i];
						that.categoriesText.push(category.name + "(" + category.postCount + ")");
						that.categoriesValue.push(category.id);
					}
				}).catch(err => {
					uni.showModal({
						title: "获取标签数据失败",
						content: err
					});
				})
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
								deletePost(post.id).then(data => {
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
					// 编辑文章
					uni.navigateTo({
						url: "./edit/edit?id=" + this.posts[i].id + "?type=update"
					})
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
				this.setData("posts_sizesIndex", this.sizesIndex)
				this.refreshData()
			},

			/**
			 * 查看文章点击事件
			 * @param {Object} i
			 */
			onPostClick: function(i) {
				// 如果当前不是展示文章模式（处于回收站或批量操作模式下），点击文章是选择文章，而不是查看文章内容
				if (this.mode !== "all") {
					let id = this.posts[i].id;
					let index = this.selectedPost.indexOf(id);
					if (index < 0) {
						this.selectedPost.push(id);
					} else {
						this.selectedPost.splice(index, 1);
					}
				} else {
					uni.navigateTo({
						url: './detail/detail?id=' + this.posts[i].id
					})
				}

			},

			/**
			 * 文章设置点击事件
			 * @param {Object} i
			 */
			onSettingClick: function(i) {
				let postId = this.posts[i].id;
				uni.navigateTo({
					url: './setting/setting?id=' + postId
				})
			},

			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function(e) {
				switch (e.index) {
					// 写文章
					case 0:
						uni.navigateTo({
							url: "./edit/edit?type=add"
						});
						break;
					// 批量操作
					case 1:
						this.mode = "batch";
						this.selectedPost = [];
						break;
					// 回收站
					case 2:
						uni.setNavigationBarTitle({
							title: "回收站"
						})
						this.mode = "recycle";
						this.selectedPost = [];
						this.refreshData();
						break;
				}

			},

			/**
			 * 文章筛选状态选项更改事件
			 * @param {Object} e
			 */
			postStatusChange: function(e) {
				this.post_statusIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 文章筛选分类目录选项更改事件
			 * @param {Object} e
			 */
			categoriesChange: function(e) {
				this.categoriesIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 重置文章筛选选项
			 */
			reset: function() {
				this.keyword = "";
				this.post_statusIndex = 0;
				this.categoriesIndex = 0;
				this.refreshData();
			},

			/**
			 * 文章方块上分类标签的点击事件，筛选出当前分类的所有文章
			 * @param {Object} id
			 */
			onCategoryTagClick: function(id) {
				// 只有展示所有文章模式时才会筛选文章
				if (this.mode === "all") {
					this.categoriesIndex = this.categoriesValue.indexOf(id);
					this.refreshData();
					this.toast("你选择了：" + this.categories[this.categoriesIndex - 1].name);
					this.collapseOpen = true;
				}

			},

			/**
			 * 返回展示所有文章模式
			 */
			onReturnAllClick: function() {
				this.mode = "all";
				uni.setNavigationBarTitle({
					title: "所有文章"
				});
				this.refreshData();
			},
			
			/**
			 * 全选按钮点击事件
			 */
			onSelectAllClick: function() {
				if (this.posts.length === this.selectedPost.length) {
					this.selectedPost = [];
				} else {
					this.selectedPost = [];
					for (var i = 0; i < this.posts.length; i++) {
						this.selectedPost.push(this.posts[i].id);
					}
				}
			},
			
			/**
			 * 回收站批量操作 picker 选择事件
			 * @param {Object} e
			 */
			recycleBatchChange: function(e) {
				let that = this;
				switch (e.detail.value) {
					// 发布
					case 0:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章转为已发布状态吗？',
							success: function(res) {
								if (res.confirm) {
									updatePostsStatus(that.selectedPost, "PUBLISHED").then(data => {
										that.toast("发布成功", "success");
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
					// 转为草稿
					case 1:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章转为草稿状态吗？',
							success: function(res) {
								if (res.confirm) {
									updatePostsStatus(that.selectedPost, "DRAFT").then(data => {
										that.toast("操作成功", "success");
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
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章永久删除吗？',
							success: function(res) {
								if (res.confirm) {
									deletePosts(that.selectedPost).then(data => {
										that.toast("删除成功", "success");
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
						break;
				}
			},
			
			/**
			 * 回收站删除当前页点击事件
			 * @param {Object} e
			 */
			onDeleteCurrentPageClick: function() {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要永久删除当前页的 ' + this.posts.length + ' 个文章吗？',
					success: function(res) {
						if (res.confirm) {
							let ids = [];
							for (var i = 0; i < that.posts.length; i++) {
								ids.push(that.posts[i].id);
							}
							deletePosts(ids).then(data => {
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
			},
			
			/**
			 * 批量操作模式的批量操作 picker 选择事件
			 * @param {Object} e
			 */
			batchBatchChange: function(e) {
				let that = this;
				switch (e.detail.value) {
					// 发布
					case 0:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章转为已发布状态吗？',
							success: function(res) {
								if (res.confirm) {
									updatePostsStatus(that.selectedPost, "PUBLISHED").then(data => {
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
						break;
					// 转为草稿
					case 1:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章转为草稿状态吗？',
							success: function(res) {
								if (res.confirm) {
									updatePostsStatus(that.selectedPost, "DRAFT").then(data => {
										that.popup("操作成功", "success");
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
					// 删除到回收站
					case 2:
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedPost.length + ' 个文章转为回收站状态吗？',
							success: function(res) {
								if (res.confirm) {
									updatePostsStatus(that.selectedPost, "RECYCLE").then(data => {
										that.popup("操作成功", "success");
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
		margin-bottom: 50rpx;
	}

	.block-name-view{
		padding: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #ececec;
	}

	.block-name {
		font-weight: bold;
		font-size: 1.1em;
		display: inline-block;
		white-space: nowrap;
		width: 100%;
		overflow: hidden;
		text-overflow: ellipsis;
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
	
	.topped-tag {
		background-color: var(--errorColor);
	}
</style>
