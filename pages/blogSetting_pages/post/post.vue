<template>
	<view>
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">首页文章排序：</view>
				<view class="input">
					<picker @change="postIndexSortChange" :value="post_index_sortIndex" :range="post_index_sortText">
						<view>{{post_index_sortText[post_index_sortIndex]}}</view>
					</picker>
				</view>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">首页每页文章条数：</view>
				<input class="input" type="number" v-model="post_index_page_size" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">归档页面每页文章条数：</view>
				<input class="input" type="number" v-model="post_archives_page_size" />
				<text class="view-input-text">* 包括标签所属文章页面、分类所属文章页面</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">RSS内容类型：</view>
				<view class="input">
					<picker @change="rssContentTypeChange" :value="rss_content_typeIndex" :range="rss_content_typeText">
						<view>{{rss_content_typeText[rss_content_typeIndex]}}</view>
					</picker>
				</view>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">RSS内容条数：</view>
				<input class="input" type="number" v-model="rss_page_size" />
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">文章摘要字数：</view>
				<input class="input" type="number" v-model="post_summary_length" />
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">自动清理回收站文章：</view>
				<switch :checked="recycled_post_cleaning_enabled" @change="switchChange" />
			</view>
			
			<view class="view-input" v-if="recycled_post_cleaning_enabled">
				<view class="view-input-titleView">回收站文章保留时长：</view>
				
				<uni-row class="view-images">
					<uni-col :span="12">
						<input class="input" type="number" v-model="recycled_post_retention_time" />
					</uni-col>
					<uni-col :span="11" :push="1">
						<view class="input">
							<picker @change="recycledPostRetentionTimeunitChange" :value="recycled_post_retention_timeunitIndex" 
								:range="recycled_post_retention_timeunitText">
								<view>{{recycled_post_retention_timeunitText[recycled_post_retention_timeunitIndex]}}</view>
							</picker>
						</view>
					</uni-col>
				</uni-row>
			</view>

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				accessToken: "",
				url: "",

				post_index_sort: "",
				post_index_sortText: ["创建时间", "最后编辑时间", "点击量"],
				post_index_sortValue: ["createTime", "editTime", "visits"],
				post_index_sortIndex: 1,

				post_index_page_size: "",
				post_archives_page_size: "",
				
				rss_content_type: "",
				rss_content_typeText: ["全文", "摘要"],
				rss_content_typeValue: ["full", "summary"],
				rss_content_typeIndex: 0,
				
				rss_page_size: "",
				post_summary_length: "",
				recycled_post_cleaning_enabled: false,
				recycled_post_retention_time: "",
				
				recycled_post_retention_timeunit: "",
				recycled_post_retention_timeunitText: ["天", "小时"],
				recycled_post_retention_timeunitValue: ["DAY", "HOUR"],
				recycled_post_retention_timeunitIndex: 0,

				popupType: "",
				popupMessage: ""
			}
		},

		mounted() {
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
			this.refreshData()
		},

		/**
		 * 下拉刷新事件
		 */
		onPullDownRefresh() {
			this.refreshData()
		},

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let array = ["post_index_sort", "post_index_page_size", "post_archives_page_size",
					"rss_content_type", "rss_page_size", "post_summary_length", "recycled_post_cleaning_enabled",
					"recycled_post_retention_time", "recycled_post_retention_timeunit"
				]
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/options/map_view/keys",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					data: array,
					success: function(res) {
						uni.stopPullDownRefresh()
						if (res.statusCode !== 200) {
							that.popup("获取数据失败")
							// 登录过期
							if (res.message === undefined || res.message === "Token 已过期或不存在") {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return
						}

						let data = res.data.data
						that.post_index_sort = data.post_index_sort
						// 把服务端的数据和post_index_sortValue比对，获得对应的选项索引
						that.post_index_sortIndex = that.post_index_sortValue.indexOf(that.post_index_sort)
						that.post_index_page_size = data.post_index_page_size
						that.post_archives_page_size = data.post_archives_page_size
						that.rss_content_type = data.rss_content_type
						that.rss_content_typeIndex = that.rss_content_typeValue.indexOf(that.rss_content_type)
						that.rss_page_size = data.rss_page_size
						that.post_summary_length = data.post_summary_length
						that.recycled_post_cleaning_enabled = data.recycled_post_cleaning_enabled
						that.recycled_post_retention_time = data.recycled_post_retention_time
						that.recycled_post_retention_timeunit = data.recycled_post_retention_timeunit
						that.recycled_post_retention_timeunitIndex = 
							that.recycled_post_retention_timeunitValue.indexOf(that.recycled_post_retention_timeunit)
						
					
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "获取数据失败",
							content: e.message
						})
					}
				})
			},

			/**
			 * 首页文章排序选项更改事件
			 * @param {Object} e
			 */
			postIndexSortChange: function(e) {
				this.post_index_sortIndex = e.detail.value
			},
			
			/**
			 * RSS内容类型选项更改事件
			 * @param {Object} e
			 */
			rssContentTypeChange: function(e) {
				this.rss_content_typeIndex = e.detail.value
			},
			
			/**
			 * 自动清理回收站文章switch改变事件
			 * @param {Object} e
			 */
			switchChange: function(e) {
				this.recycled_post_cleaning_enabled = e.detail.value
			},
			
			/**
			 * 自动清理回收文章保留时长时间单位更改事件
			 * @param {Object} e
			 */
			recycledPostRetentionTimeunitChange: function(e) {
				this.recycled_post_retention_timeunitIndex = e.detail.value
			},


			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"post_index_sort": this.post_index_sortValue[this.post_index_sortIndex],
					"post_index_page_size": this.post_index_page_size,
					"post_archives_page_size": this.post_archives_page_size,
					"rss_content_type": this.rss_content_typeValue[this.rss_content_typeIndex],
					"rss_page_size": this.rss_page_size,
					"post_summary_length": this.post_summary_length,
					"recycled_post_cleaning_enabled": this.recycled_post_cleaning_enabled,
					"recycled_post_retention_time": this.recycled_post_retention_time,
					"recycled_post_retention_timeunit": 
						this.recycled_post_retention_timeunitValue[this.recycled_post_retention_timeunitIndex]
				}
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.url + "/api/admin/options/map_view/saving",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.accessToken
					},
					data: json,
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("保存失败：" + res.statusCode)
							// 登录过期
							if (res.message === undefined || res.message === "Token 已过期或不存在") {
								that.popup("保存失败，登录已过期，请重新登陆")
							}
							return
						}
						that.popup("保存成功", "success")
						that.refreshData()
					},
					fail: function(e) {
						uni.showModal({
							title: "保存数据失败",
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
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
