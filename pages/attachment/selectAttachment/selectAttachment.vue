<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>

		<uni-row class="view-images">
			<uni-col :span="6" v-for="(item, i) in content">
				<view class="view-image">
					<!-- 当前附件不是图片就展示格式错误的图片 -->
					<image :src="item.url" v-if="!item.isNotImage" @click="selectAttachment(i)"></image>
					<image src="/static/images/format_error.jpg" v-else></image>
				</view>
			</uni-col>
		</uni-row>
		
		<view class="view-sizeSelect block">
			<picker @change="sizesChange" :value="sizesIndex" :range="sizes">
				<view>{{sizes[sizesIndex]}}</view>
			</picker>
		</view>
		<uni-pagination style="padding-bottom: 80rpx;margin-left: 10rpx;margin-right: 10rpx;" title="附件" :pageSize="size" :total="total"
			:current="currentPage + 1" @change="pageChange"></uni-pagination>
			
	</view>
</template>

<script>
	import {
		getAttachments,
	} from "@/network/AttachmentApi.js";
	export default {
		data() {
			return {
				// 总页数
				pages: 0,
				// 总附件数
				total: 0,
				// 每页默认附件数量
				size: 24,
				// 默认第一页
				currentPage: 0,
				// 存放附件信息
				content: [],

				popupType: "",
				popupMessage: "",
				sizes: ["4条/页", "8条/页", "16条/页", "24条/页", "48条/页", "96条/页"],
				sizesIndex: 3,
				
				attrName: "",
			}
		},
		
		onLoad: function(option) {
			// 接收上个页面传递的变量名，用于选择附件后设置变量数据
			this.attrName = option.attrName
		},

		mounted() {
			// 获取之前设置的每页几条数据
			this.sizesIndex = this.getData("selectAttachment_sizesIndex")
			if (this.sizesIndex.length <= 0) {
				// 默认24条/页
				this.sizesIndex = 3
			}
			// 将本地取出的文本数据转成int
			this.sizesIndex = Number(this.sizesIndex)
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
				getAttachments(this.currentPage, this.size).then(data => {
					that.pages = data.pages;
					that.total = data.total;
					that.content = data.content;
					
					for (let i = 0; i < that.content.length; i++) {
						let item = that.content[i];
						let thumbPath = that.content[i].thumbPath;
						// 如果图片地址是相对地址，就加上网站URL
						if (item.thumbPath.indexOf("http") === -1) {
							that.content[i].url = that.getUrl() + thumbPath;
						} else {
							that.content[i].url = thumbPath;
						}
					
						// 如果当前附件不是图片就标记它
						if (item.mediaType.indexOf("image") === -1) {
							that.content[i].isNotImage = true;
						}
					}
						uni.stopPullDownRefresh();
						uni.hideLoading();
				}).catch(err => {
					uni.stopPullDownRefresh()
					uni.hideLoading()
					uni.showModal({
						title: "获取数据失败",
						content: err,
						showCancel: false
					})
				});
			},
			
			/**
			 * 更改每页显示条数事件
			 * @param {Object} e
			 */
			sizesChange: function(e) {
				let i = e.detail.value
				this.sizesIndex = i
				this.currentPage = 0
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
				this.setData("selectAttachment_sizesIndex", this.sizesIndex)
				this.refreshData()
			},
			
			/**
			 * 单击图片选中附件，修改上个页面需要修改附件的变量
			 * @param {Object} i
			 */
			selectAttachment: function(i) {
				// 获取所有页面栈实例列表
				let pages = getCurrentPages()
				// 当前页面实例
				let nowPage = pages[pages.length - 1]
				// 上一页面实例
				let prevPage = pages[pages.length - 2]
				// 修改上一页面需要修改附件的变量
				prevPage.$vm[this.attrName] = this.content[i].path
				// 返回上一页面
				uni.navigateBack({
					delta:1
				})
				
			},

			/**
			 * 改变页面事件
			 */
			pageChange: function(e) {
				let current = e.current
				this.currentPage = current - 1
				this.refreshData()
			},

			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				this.popupMessage = message
				this.popupType = type
				this.$refs.popup.open()
			},
		}
	}
</script>

<style>
	.view-images {
		margin: 10rpx;
		/* background-color: red; */
	}

	.view-image {
		margin: 10rpx;
		height: 160rpx;
		box-shadow: 0px 0px 1px 1px rgba(216, 216, 216, 1);
	}

	.view-image image {
		width: 100%;
		height: 100%;
	}
	.view-sizeSelect {
		margin: 10rpx;
		margin-top: 20rpx;
		padding: 20rpx;
		color: #616255;
	}
</style>
