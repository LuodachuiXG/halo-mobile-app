<template>
	<view>
		<u-notify ref="popup" duration="1500"></u-notify>

		<!-- 日志筛选组件 -->
		<u-sticky>
			<uni-collapse>
				<uni-collapse-item title="日志筛选" :open="collapseOpen">
					<view>
						<view class="view-input">
							<view class="view-input-titleView">关键词：</view>
							<input class="input" type="text" v-model="keyword" />
						</view>
						<view class="view-input">
							<view class="view-input-titleView">日志状态：</view>
							<view class="input">
								<picker @change="journalStatusChange" :value="journal_statusIndex"
									:range="journal_statusText">
									<view style="height: 25px;line-height: 25px;">
										{{journal_statusText[journal_statusIndex]}}
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


		<u-empty v-if="journals.length === 0" mode="list" icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>


		<view class="block" v-for="(journal, i) in journals">
			<view>
				<!-- 日志时间标题 -->
				<view class="block-name-view">
					<view class="block-name">
						{{ format(journal.createTime) }}
					</view>
				</view>

				<!-- 日志内容 -->
				<view class="block-content">
					<text>{{ journal.sourceContent }}</text>
				</view>

				<!-- 显示日志状态 -->
				<view class="block-status">
					<!-- 根据公开和私密两个状态，设置不同的颜色和文字提示 -->
					<view class="block-status-point" v-if="journal.type === 'PUBLIC'"></view>
					<view class="block-status-point background-error" v-if="journal.type == 'INTIMATE'"></view>
					<text>
						{{ journal.type == 'PUBLIC' ? '公开' : ''}}{{ journal.type == 'INTIMATE' ? '私密' : ''}}
					</text>
					
					<view class="block-status-data">
						<view>
							<image src="../../static/images/like.png"></image>
							<text>{{journal.likes}}</text>
						</view>
						<view>
							<image src="../../static/images/comment_1.png"></image>
							<text>{{journal.commentCount}}</text>
						</view>
					</view>
				</view>
			</view>


			<!-- 操作按钮 -->
			<view class="block-action">
				<uni-row>

					<uni-col :span="12">
						<view @click="onEditClick(i)">
							<view class="block-action-item">
								<image src="../../static/images/edit.png"
									style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;">
								</image>
								<text>
									编辑
								</text>
							</view>
						</view>
					</uni-col>


					<uni-col :span="12">
						<view class="block-action-item border" @click="onDeleteClick(i)">
							<image src="../../static/images/trash.png"
								style="width: 35rpx; height: 35rpx;top: 7rpx;margin-top: -7rpx;"></image>
							<text>
								删除
							</text>
						</view>
					</uni-col>

				</uni-row>
			</view>
		</view>

		<view class="view-sizeSelect block" v-if="journals.length > 0">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination style="padding-bottom: 200rpx;margin-left: 20rpx;margin-right: 20rpx;" title="日志"
			:pageSize="size" :total="total" :current="page + 1" @change="pageChange" v-if="journals.length > 0">
		</uni-pagination>

		<!-- 悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @fabClick="onFabClick">
		</uni-fab>

	</view>
</template>

<script>
	import {
		getJournals,
		deleteJournal
	} from "@/network/JournalApi.js";
	export default {
		data() {
			return {
				// 控制日志筛选折叠面板是否打开
				collapseOpen: false,

				// 日志筛选的日志状态的 picker 选项
				journal_statusText: ["所有状态", "公开", "私密"],
				journal_statusValue: ["", "PUBLIC", "INTIMATE"],
				journal_statusIndex: 0,

				// 日志筛选的关键词
				keyword: "",

				// 当前页数
				page: 0,
				// 总页数
				pages: 0,
				// 总日志数
				total: 0,
				// 每页日志数量
				size: 8,

				// 存放日志信息
				journals: [],

				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 1,
			}
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("journals_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认8条/页
				this.sizesIndex = 1;
			}

			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
			// 根据每页显示日志数量来设置 size 大小
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
				// 获取日志数据
				let page = this.page;
				let size = this.size;
				let keyword = this.keyword;
				let type = this.journal_statusValue[this.journal_statusIndex];
				getJournals(page, size, keyword, type).then(data => {
					// 保存日志数组
					that.journals = data.content;
					// 保存日志总数
					that.total = data.total;
					// 保存总页数
					that.pages = data.pages;

					uni.hideLoading();
					uni.stopPullDownRefresh();
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
			 * 编辑日志
			 * @param {Object} i
			 */
			onEditClick: function(i) {
				uni.navigateTo({
					url: "./edit/edit?type=update&i=" + i
				});
			},


			/**
			 * 日志删除单击事件
			 * @param {Object} i
			 */
			onDeleteClick: function(i) {
				let that = this;
				let journal = this.journals[i];
				uni.showModal({
					title: '提示',
					content: '确定要删除这条日志吗。',
					success: function(res) {
						if (res.confirm) {
							deleteJournal(journal.id).then(data => {
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
				this.setData("journals_sizesIndex", this.sizesIndex)
				this.refreshData()
			},


			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function() {
				// 写日志
				uni.navigateTo({
					url: "./edit/edit?type=add"
				});
			},

			/**
			 * 日志筛选状态选项更改事件
			 * @param {Object} e
			 */
			journalStatusChange: function(e) {
				this.journal_statusIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 重置日志筛选选项
			 */
			reset: function() {
				this.keyword = "";
				this.journal_statusIndex = 0;
				this.refreshData();
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
		font-size: 1.1em;
		display: inline-block;
		white-space: nowrap;
		width: 100%;
		overflow: hidden;
		text-overflow: ellipsis;
	}


	.block-content {
		padding: 30rpx;
		padding-bottom: 10rpx;
		padding-top: 20rpx;
	}

	.block-content text {
		overflow: hidden;
		font-size: 1em;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 3;
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
	
	.block-status-data {
		position: absolute;
		right: 30rpx;
		top: 1px;
		color: #8c8c8c;
	}
	
	.block-status-data view{
		display: inline-block;
		margin-left: 20rpx;
	}
	
	.block-status-data image{
		width: 25rpx;
		height: 25rpx;
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
