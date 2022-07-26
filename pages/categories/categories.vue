<template>
	<view>
		<u-notify ref="popup"></u-notify>
		
		<u-empty
			v-if="categories.length === 0"
			mode="list"
			icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>
		
		<view class="block" v-for="(category, i) in categories">
			<!-- 分类目录名 -->
			<view class="block-name">
				<text>{{ category.name }}</text>
				<text>{{ category.slug }}</text>
			</view>

			<!-- 缩略图 -->
			<view class="block-thumbnail">
				<!-- 判断缩略图是否是绝对地址 -->
				<image v-if="category.thumbnail" :src="category.thumbnail.indexOf('http') < 0  ? 
					getUrl() + category.thumbnail : category.thumbnail"></image>
			</view>

			<!-- 分类目录的一些信息 -->
			<view class="block-data"
				:style="category.thumbnail == '' ? 'border-top:none' : 'border-top: 1px solid #ececec;'">
				<view class="block-tag">
					<view class="block-tag-item">
						<u-tag :text="'文章数：' + category.postCount"></u-tag>
					</view>
				</view>
				
				
			</view>

			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>
					<uni-col :span="12">
						<view class="block-action-item" @click="onEditClick(i)">
							<text class="iconfont item-icon">&#xe892;</text>
							编辑
						</view>
					</uni-col>
					<uni-col :span="12">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<text class="iconfont item-icon">&#xe74e;</text>
							删除
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onAddCategoryClick"></uni-fab>

	</view>
</template>

<script>
	import {
		getCategories,
		deleteCategory
	} from "@/network/CategoryApi.js";
	export default {
		data() {
			return {
				categories: [],
			}
		},

		mounted() {
			
		},

		// 下拉刷新事件
		onPullDownRefresh() {
			this.refreshData()
		},
		
		onShow() {
			let that = this;
			// 延迟 100 防止 URL 还未读取成功就刷新数据
			setTimeout(function() {
				that.refreshData()
			}, 100)
		},
		
		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let that = this;
				getCategories().then(data => {
					that.categories = data;
					uni.stopPullDownRefresh();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.showModal({
						title: "获取数据失败",
						content: err
					});
				});
			},

			/**
			 * 编辑分类目录单击事件
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				let that = this;
				uni.navigateTo({
					url: './updateCategory/updateCategory?type=update&id=' + that.categories[i].id + 
						"&index=" + i
				})
			},

			/**
			 * 删除分类目录单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要删除【' + this.categories[i].name + '】分类吗？',
					success: function(res) {
						if (res.confirm) {
							deleteCategory(that.categories[i].id).then(data => {
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
			},
			
			/**
			 * 添加分类目录单击事件
			 */
			onAddCategoryClick: function() {
				uni.navigateTo({
					url: './updateCategory/updateCategory?type=add'
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
	.block-name {
		padding: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #ececec;
	}

	.block-name text:first-child {
		font-weight: bold;
		font-size: 1.1em;
	}

	.block-name text:last-child {
		font-weight: bold;
		font-size: .8em;
		margin-left: 20rpx;
		color: var(--textPrimaryColor);
	}

	.block-thumbnail {}

	.block-thumbnail image {
		vertical-align: middle;
		width: 100%;
	}

	.block-data {
		padding-top: 20rpx;
	}

	.block-tag {
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 20rpx;
	}

	.block-tag-item {
		margin-top: 0rpx;
		margin-left: 20rpx;
		font-weight: bold;
		display: inline-block;
	}

	.block-tag-item:first-child {
		margin-left: 0rpx;
	}

	.block-action {
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
</style>
