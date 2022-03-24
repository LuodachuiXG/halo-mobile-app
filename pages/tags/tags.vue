<template>
	<view>
		<u-notify ref="popup"></u-notify>
		<u-empty
			v-if="tags.length === 0"
			mode="list"
			icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>
		
		<view class="block" v-if="tags.length > 0">
			<view class="block-tag">
				<view class="block-tag-item" v-for="(tag, i) in tags">
					<u-tag :text="tag.name + '(' + tag.postCount + ')'" plain plainFill closable 
						type="info" @close="onCloseTagClick(i)" @click="onEditClick(i)"
						:bgColor="tag.color" :borderColor="tag.color" color="#000000"></u-tag>
				</view>
			</view>
		</view>
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onAddTagClick"></uni-fab>
	</view>
</template>

<script>
	import {
		getTags,
		deleteTag
	} from "../../common/api.js";
	export default {
		data() {
			return {
				tags: [],
			}
		},
		onShow() {
			this.refreshData();
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
				getTags().then(data => {
					// 保存文章数组
					that.tags = data;

					uni.stopPullDownRefresh();
					uni.hideLoading();
				}).catch(err => {
					uni.stopPullDownRefresh();
					uni.hideLoading();
					uni.showModal({
						title: "获取标签失败",
						content: err
					});
				});
			},
			
			/**
			 * 删除标签点击事件
			 * @param {Object} i
			 */
			onCloseTagClick: function(i) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要删除【' + this.tags[i].name + '】标签吗？',
					success: function(res) {
						if (res.confirm) {
							deleteTag(that.tags[i].id).then(data => {
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
			 * 添加标签悬浮按钮单击事件
			 */
			onAddTagClick: function() {
				uni.navigateTo({
					url: './updateTag/updateTag?type=add'
				})
			},
			
			/**
			 * 编辑标签单击事件
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				let that = this;
				uni.navigateTo({
					url: './updateTag/updateTag?type=update&id=' + that.tags[i].id + 
						"&index=" + i
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
		
	}
	.block-tag {
		margin-top: -20rpx;
		padding-top: 0px;
		padding-left: 15px;
		padding-right: 0px;
		padding-bottom: 15px;

	}

	.block-tag-item {
		margin-right: 10rpx;
		margin-top: 10rpx;
		display: inline-block;
	}
</style>
