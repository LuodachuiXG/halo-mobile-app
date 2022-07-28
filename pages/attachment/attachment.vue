<template>
	<view>
		<u-notify ref="popup" duration="1500"></u-notify>

		<!-- 页面下方弹出的操作菜单 -->
		<u-action-sheet :actions="batchOptions" :closeOnClickOverlay="true" :closeOnClickAction="true"
			:show="showActionSheet" cancelText="取消" @close="onActionSheetClose" @select="onActionSheetSelect">
		</u-action-sheet>

		<!-- 附件组件 -->
		<u-sticky v-if="mode === 'all'">
			<uni-collapse>
				<uni-collapse-item title="附件筛选" :open="collapseOpen">
					<view>
						<view class="view-input">
							<view class="view-input-titleView">关键词：</view>
							<input class="input" type="text" v-model="keyword" />
						</view>
						<view class="view-input">
							<view class="view-input-titleView">存储位置：</view>
							<view class="input">
								<picker @change="typeChange" :value="typeIndex" :range="typeText">
									<view style="height: 25px;line-height: 25px;">{{typeText[typeIndex]}}
									</view>
								</picker>
							</view>
						</view>
						<view class="view-input">
							<view class="view-input-titleView">文件类型：</view>
							<view class="input">
								<picker @change="mediaTypeChange" :value="mediaTypeIndex" :range="mediaTypeText">
									<view style="height: 25px;line-height: 25px;">{{mediaTypeText[mediaTypeIndex]}}
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
							<button @click="onSelectAllClick" :disabled="attachments.length === 0"
								:class="attachments.length > 0 && (selectedAttachments.length === attachments.length) ? 'blue' : ''">
								{{ attachments.length > 0 && (selectedAttachments.length === attachments.length) ? "取消" : "全选" }}
							</button>
						</u-checkbox-group>
					</uni-col>
					<uni-col :span="5">
						<button class="blue" @click="onBatchClick" :disabled="selectedAttachments.length === 0">
							批量操作
						</button>
					</uni-col>
					<uni-col :span="9" :push="1">
						<button class="yellow" @click="onReturnAllClick">关闭批量操作模式</button>
					</uni-col>
				</uni-row>
			</view>
		</u-sticky>

		<u-empty v-if="attachments.length === 0" mode="list" icon="http://cdn.uviewui.com/uview/empty/list.png">
		</u-empty>

		<view class="block" v-for="(attachment, i) in attachments"
			:class="(selectedAttachments.indexOf(attachment.id) >= 0) ? 'selected' : ''">
			<view @click="onAttachmentClick(i)">
				<!-- 附件名 -->
				<view class="block-name-view">
					<view class="block-name">
						{{ attachment.name }}
					</view>
				</view>

				<!-- 缩略图 -->
				<view class="block-thumbnail">
					<!-- 判断缩略图是否是绝对地址 -->
					<image v-if="attachment.thumbPath && attachment.mediaType.indexOf('image') >= 0" :src="attachment.thumbPath.indexOf('http') < 0  ? 
						getUrl() + attachment.thumbPath : attachment.thumbPath" mode="aspectFill"></image>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="block-action" :style="attachment.mediaType.indexOf('image') < 0 ? 'border:none;' : ''">
				<uni-row>
					<uni-col :span="12">
						<view class="block-action-item" @click="onInfoClick(i)">
							<text class="iconfont block-action-icon">&#xe7d2;</text>
							详情
						</view>
					</uni-col>
					<uni-col :span="12">
						<view class="block-action-item border" @click="onDelClick(i)">
							<text class="iconfont block-action-icon">&#xe74e;</text>
							删除
						</view>
					</uni-col>
				</uni-row>
			</view>
		</view>

		<view class="view-sizeSelect block" v-if="attachments.length > 0">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination style="padding-bottom: 200rpx;margin-left: 20rpx;margin-right: 20rpx;" title="附件"
			:pageSize="size" :total="total" :current="page + 1" @change="pageChange" v-if="attachments.length > 0">
		</uni-pagination>

		<!-- 批量操作模式不显示悬浮按钮 -->
		<uni-fab horizontal="right" vertical="bottom" @trigger="onFabClick" :content="content" v-if="mode === 'all'">
		</uni-fab>


		<!-- 附件详情弹出层 -->
		<u-popup :show="infoShow" @close="onInfoClose" mode="bottom" :round="4">
			<slot name="info">
				<view class="info-popup">
					<view class="info-view">
						<view class="info-title">附件名：
							<text class="iconfont info-icon" @click="onInfoNameClick">&#xe774;</text>
						</view>
						<text class="info-text">{{ infoAttachement.name }}</text>
					</view>

					<view class="info-view">
						<view class="info-title">附件类型：</view>
						<text class="info-text">{{ infoAttachement.mediaType }}</text>
					</view>

					<view class="info-view">
						<view class="info-title">存储位置：</view>
						<text class="info-text">{{ getTypeText(infoAttachement.type) }}</text>
					</view>

					<view class="info-view">
						<view class="info-title">附件大小：</view>
						<text class="info-text">{{ formatByte(infoAttachement.size) }}</text>
					</view>

					<view class="info-view"
						v-if="infoAttachement.mediaType !== undefined && infoAttachement.mediaType.indexOf('image') >= 0">
						<view class="info-title">图片尺寸：</view>
						<text class="info-text">{{ infoAttachement.width + " × " +  infoAttachement.height }}</text>
					</view>

					<view class="info-view">
						<view class="info-title">上传日期：</view>
						<text class="info-text">{{ format(infoAttachement.createTime) }}</text>
					</view>

					<view class="info-view">
						<view class="info-title">普通链接：
							<text class="iconfont info-icon" @click="onPathCopyClick">&#xe6f4;</text>
						</view>
						<text class="info-text" @click="onPathClick" style="color:var(--primaryColor);">
							{{ infoAttachement.path }}
						</text>
					</view>

					<view class="info-view"
						v-if="infoAttachement.mediaType !== undefined && infoAttachement.mediaType.indexOf('image') >= 0">
						<view class="info-title">Markdown 格式：
							<text class="iconfont info-icon" @click="onMarkdownCopyClick">&#xe6f4;</text>
						</view>
						<text class="info-text">![{{ infoAttachement.name }}]({{ infoAttachement.path }})</text>
					</view>

					<view class="info-btn" @click="onInfoClose">
						关闭
					</view>
				</view>
			</slot>
		</u-popup>
	</view>
</template>

<script>
	import {
		getAttachments,
		getAttachementTypes,
		getAttachementMediaTypes,
		deleteAttachmentById,
		deleteAttachmentByIds,
		updateAttachmentName
	} from "@/network/AttachmentApi.js";
	export default {
		data() {
			return {
				// 控制附件筛选折叠面板是否打开
				collapseOpen: false,

				// 附件筛选中存储位置的 picker 选项
				typeText: ["所有位置"],
				typeValue: [""],
				typeIndex: 0,

				// 附件筛选中关键词
				keyword: "",

				// 附件筛选中文件类型的 picker 选项
				mediaTypeText: ["所有类型"],
				mediaTypeValue: [""],
				mediaTypeIndex: 0,


				// 当前页数
				page: 0,
				// 总页数
				pages: 0,
				// 总附件数
				total: 0,
				// 每页附件数量
				size: 16,

				// 存放附件信息
				attachments: [],

				// 批量模式选中的附件
				selectedAttachments: [],

				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 2,

				// 批量操作模式的批量操作的 picker 选项
				batchOptions: [{
					name: "删除"
				}],
				// 是否显示操作菜单
				showActionSheet: false,

				// 是否显示附件详情弹出层
				infoShow: false,


				// 当前页面是什么模式，展示附件/批量操作/  all/batch
				mode: "all",

				// 悬浮按钮弹出菜单
				content: [{
						"text": "添加附件",
						"iconPath": "/static/images/add.png"
					},
					{
						"text": "批量操作",
						"iconPath": "/static/images/checkbox.png"
					}
				],

				// 查看详情的附件
				infoAttachement: [],
				// 查看详情的附件索引
				infoIndex: -1,
			}
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("attachments_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认 16 条/页
				this.sizesIndex = 2;
			}

			this.sizesIndex = Number(this.sizesIndex)

			// 根据每页显示附件数量来设置 size 大小
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

		onBackPress(event) {
			if (event.from === "backbutton" && this.mode !== "all") {
				this.onReturnAllClick();
				return true;
			}
			return false;
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

				// 获取附件数据
				let page = this.page;
				let size = this.size;
				let type = this.typeValue[this.typeIndex];
				let mediaType = this.mediaTypeValue[this.mediaTypeIndex];
				let keyword = this.keyword;
				getAttachments(page, size, type, mediaType, keyword).then(data => {
					// 保存附件数据
					that.attachments = data.content;
					// 清空已选择的附件
					that.selectedAttachments = [];
					// 保存附件总数
					that.total = data.total;
					// 保存总页数
					that.pages = data.pages;
					
					// 如果附件里的地址是相对地址，就改为绝对地址
					for (let i = 0; i < that.attachments.length; i++) {
						let attachment = that.attachments[i];
						if (attachment.path.indexOf("http") < 0) {
							attachment.path = that.getUrl() + attachment.path;
						}
						
						if (attachment.thumbPath.indexOf("http") < 0) {
							attachment.thumbPath = that.getUrl() + attachment.thumbPath;
						}
						that.attachments[i] = attachment;
					}

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
				
				// 获取所有附件存储位置
				getAttachementTypes().then(data => {
					that.typeText = ["所有位置"];
					that.typeValue = [""];
					for (let i = 0; i < data.length; i++) {
						that.typeText.push(that.getTypeText(data[i]));
						that.typeValue.push(data[i]);
					}
				}).catch(err => {
					uni.showModal({
						title: "获取存储位置失败",
						content: err
					});
				});

				// 获取所有附件类型
				getAttachementMediaTypes().then(data => {
					that.mediaTypeText = ["所有类型"];
					that.mediaTypeValue = [""];
					for (let i = 0; i < data.length; i++) {
						that.mediaTypeText.push(data[i]);
						that.mediaTypeValue.push(data[i]);
					}
				}).catch(err => {
					uni.showModal({
						title: "获取附件类型失败",
						content: err
					});
				});

			},


			/**
			 * 附件信息点击事件
			 * @param {Object} i
			 */
			onInfoClick: function(i) {
				let attachment = this.attachments[i];
				this.infoAttachement = attachment;
				this.infoIndex = i;
				this.infoShow = true;
			},

			/**
			 * 附件信息弹窗关闭事件
			 */
			onInfoClose: function() {
				this.infoShow = false;
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
				this.setData("attachments_sizesIndex", this.sizesIndex)
				this.refreshData()
			},

			/**
			 * 附件点击事件
			 * @param {Object} i
			 */
			onAttachmentClick: function(i) {
				// 如果当前不是展示附件模式（处于批量操作模式下），点击附件是选择附件，而不是查看附件
				if (this.mode !== "all") {
					let id = this.attachments[i].id;
					let index = this.selectedAttachments.indexOf(id);
					if (index < 0) {
						this.selectedAttachments.push(id);
					} else {
						this.selectedAttachments.splice(index, 1);
					}
				} else {
					let attachment = this.attachments[i];
					// 预览图片
					if (attachment.mediaType.indexOf("image") < 0) {
						this.popup("当前文件不支持预览");
						return;
					}

					let url = this.attachments[i].path;
					if (url.indexOf("http") < 0) {
						url = this.getUrl() + url;
					}
					uni.previewImage({
						current: "0",
						urls: [url]
					})
				}

			},

			/**
			 * 附件删除点击事件
			 * @param {Object} i
			 */
			onDelClick: function(i) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要将当前附件永久删除吗？',
					success: function(res) {
						if (res.confirm) {
							deleteAttachmentById(that.attachments[i].id).then(data => {
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
			},

			/**
			 * 悬浮按钮点击事件
			 */
			onFabClick: function(e) {
				switch (e.index) {
					case 0:
						// 添加附件
						uni.navigateTo({
							url: "./addAttachment/addAttachment"
						})
						break;
					case 1:
						// 批量操作
						this.mode = "batch";
						this.selectedAttachments = [];
						break;
				}
			},

			/**
			 * 附件存储位置选项更改事件
			 * @param {Object} e
			 */
			typeChange: function(e) {
				this.page = 0;
				this.typeIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 附件筛选文件类型选项更改事件
			 * @param {Object} e
			 */
			mediaTypeChange: function(e) {
				this.page = 0;
				this.mediaTypeIndex = e.detail.value;
				this.refreshData();
			},

			/**
			 * 重置文章筛选选项
			 */
			reset: function() {
				this.keyword = "";
				this.typeIndex = 0;
				this.mediaTypeIndex = 0;
				this.refreshData();
			},

			/**
			 * 返回展示附件模式
			 */
			onReturnAllClick: function() {
				this.mode = "all";
				this.refreshData();
			},

			/**
			 * 全选按钮点击事件
			 */
			onSelectAllClick: function() {
				if (this.attachments.length === this.selectedAttachments.length) {
					this.selectedAttachments = [];
				} else {
					this.selectedAttachments = [];
					for (var i = 0; i < this.attachments.length; i++) {
						this.selectedAttachments.push(this.attachments[i].id);
					}
				}
			},

			/**
			 * 根据附件存储位置英文返回对应的中文
			 * @param {Object} type
			 */
			getTypeText: function(type) {
				switch (type) {
					case "ALIOSS":
						return "阿里云";
					case "BAIDUBOS":
						return "百度云";
					case "HUAWEIOBS":
						return "华为云";
					case "LOCAL":
						return "本地";
					case "MINIO":
						return "MinIO";
					case "QINIUOSS":
						return "七牛云";
					case "SMMS":
						return "SM.MS";
					case "TENCENTCOS":
						return "腾讯云";
					case "UPOSS":
						return "又拍云";
				}
			},

			/**
			 * 操作菜单取消按钮事件
			 */
			onActionSheetClose: function() {
				this.showActionSheet = false;
			},

			/**
			 * 批量操作按钮点击事件
			 */
			onBatchClick: function() {
				this.showActionSheet = true;
			},

			/**
			 * 操作菜单选择事件
			 * @param {Object} e
			 */
			onActionSheetSelect: function(e) {
				let that = this;
				switch (e.name) {
					case "删除":
						// 删除
						uni.showModal({
							title: '提示',
							content: '确定要将所选的 ' + this.selectedAttachments.length + ' 个附件永久删除吗？',
							success: function(res) {
								if (res.confirm) {
									deleteAttachmentByIds(that.selectedAttachments).then(data => {
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
			 * 附件详情的普通链接点击事件
			 */
			onPathClick: function() {
				this.openURL(this.infoAttachement.path);
			},

			/**
			 * 附件详情的普通链接复制按钮点击事件
			 */
			onPathCopyClick: function() {
				let that = this;
				uni.setClipboardData({
					data: this.infoAttachement.path,
					showToast: false,
					success: function() {
						that.toast("复制成功");
					}
				});
			},

			/**
			 * 附件详情的 Markdown 复制按钮点击事件
			 */
			onMarkdownCopyClick: function() {
				let that = this;
				let data = "![" + this.infoAttachement.name + "](" + this.infoAttachement.path + ")";
				uni.setClipboardData({
					data: data,
					showToast: false,
					success: function() {
						that.toast("复制成功");
					}
				});
			},

			/**
			 * 附件详情的修改附件名点击事件
			 */
			onInfoNameClick: function() {
				// 关闭附件详情面板
				this.infoShow = false;

				let that = this;
				let attachment = this.infoAttachement;
				uni.showModal({
					title: "修改附件名",
					content: attachment.name,
					editable: true,
					placeholderText: attachment.name,
					success: function(res) {
						let thatt = that;
						if (res.confirm) {
							if (res.content.length > 0 && res.content !== attachment.name) {
								// 修改附件名
								updateAttachmentName(attachment.id, res.content).then(data => {
									// 修改成功，返回修改后数据，替换原有数据
									that.attachments.splice(thatt.infoIndex, 1, data);
									thatt.toast("修改成功");
								}).catch(err => {
									uni.showModal({
										title: "修改附件名失败",
										content: err
									});
								});
							}
						}
					}
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
			}
		}
	}
</script>

<style>
	.block {
		position: relative;
		margin-bottom: 50rpx;
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
		border-top: 1px solid #ececec;
		color: #898989;
	}

	.block-action-item {
		position: relative;
		padding: 20rpx;
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
		height: 32px;
		line-height: 32px;
		margin-right: 10px;
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

	.info-popup {
		padding: 20rpx;
		padding-left: 30rpx;
		padding-right: 30rpx;
	}

	.info-view {
		border-bottom: 1px solid var(--activatedColor);
		padding-bottom: 20rpx;
		margin-top: 20rpx;
	}

	.info-view:first-child {
		margin-top: 0px;
	}

	.info-view:last-child {
		border-bottom: none;
		padding-bottom: 0px;
	}

	.info-title {
		color: var(--textPrimaryColor);
		margin-bottom: 12rpx;
	}

	.info-text {
		color: var(--textContentColor);
		word-wrap: break-word;
	}

	.info-icon {
		color: var(--primaryColor);
		font-size: 38rpx;
		position: relative;
		top: 5rpx;
	}

	.info-btn {
		text-align: center;
		margin-top: 20px;
		margin-bottom: 10px;
		background-color: var(--primaryColor);
		color: white;
		padding-top: 5px;
		padding-bottom: 5px;
		border-radius: 4px;
	}
</style>
