<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		
		<view class="block" v-for="(post, i) in posts">
			<view class="block-name">
				<text>{{ post.title }}</text>
			</view>
			<view class="block-thumbnail">
				<!-- 判断缩略图是否是绝对地址 -->
				<image v-if="post.thumbnail" :src="post.thumbnail.indexOf('http') < 0  ? 
					url + post.thumbnail : post.thumbnail"></image>
			</view>
			<view class="block-summary">
				<text>{{ post.summary }}</text>
			</view>
			<view class="block-tag" v-if="post.categories.length > 0">
				<uni-tag class="block-tag-item" type="primary" :inverted="true"
					v-for="(categorie, j) in post.categories" :text="categorie.name"></uni-tag>
			</view>
			<view class="block-tag" v-if="post.tags.length > 0">
				<uni-tag class="block-tag-item" type="success" :inverted="true"
					v-for="(tag, j) in post.tags" :text="tag.name"></uni-tag>
			</view>
			<view class="block-status">
				<view class="block-status-point"></view>
				<text>
					{{ post.status == 'INTIMATE' ? '私密' : ''}}{{ post.status == 'PUBLISHED' ? '已发布' : ''}}
				</text>
				<view class="block-status-time">
					<image src="../../static/images/clock.png"></image>
					<text>{{ format(post.createTime) }}</text>
				</view>
			</view>
			
			<view class="block-action">
				<uni-row>
					<uni-col :span="6">
						<view class="block-action-item" @click="onActivatedClick(i)">
							<image src="../../static/images/edit.png"
								style="width: 30rpx;height: 30rpx;top: 5rpx;margin-top: -5rpx;margin-right: 5rpx;"></image>
							编辑
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
						<view class="block-action-item border" @click="onMoreClick(i)">
							<image src="../../static/images/data.png"
								style="width: 35rpx; height: 35rpx;top: 8rpx;margin-top: -8rpx;"></image>
							数据
						</view>
					</uni-col>
					<uni-col :span="6">
						<view class="block-action-item border" @click="onMoreClick(i)">
							<image src="../../static/images/trash.png"
								style="width: 35rpx; height: 35rpx;top: 7rpx;margin-top: -7rpx;"></image>
							删除
						</view>
					</uni-col>
				</uni-row>
			</view>
			
			
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				accessToken: "",
				url: "",
				
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
			// 获取token和url
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
			
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
				})
				let that = this
				uni.request({
					method: "GET",
					dataType: "json",
					url: this.url + "/api/admin/posts?page=" + this.page + 
						"&size=" + this.size,
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("获取数据失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return ;
						}
			
						// 保存文章数组
						that.posts = res.data.data.content;
						// 保存文章总数
						that.total = res.data.data.total;
						// 保存总页数
						that.pages = res.data.data.pages
						
						
						uni.stopPullDownRefresh()
						uni.hideLoading()
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.hideLoading()
						uni.showModal({
							title: "获取数据失败",
							content: e.message
						})
					}
				})
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
		padding-left: 30rpx;
		padding-right: 30rpx;
		padding-bottom: 20rpx;
	}
	.block-tag-item {
		margin-left: 20rpx;
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
</style>
