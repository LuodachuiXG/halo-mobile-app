<template>
	<view class="container">
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">文章固定链接类型：</view>
				<view class="input">
					<picker @change="postPermalinkTypeChange" :value="post_permalink_typeIndex" :range="post_permalink_typeText">
						<view>{{post_permalink_typeText[post_permalink_typeIndex]}}</view>
					</picker>
				</view>
				<text class="view-input-text">
					{{ url + "/" + post_permalink_typeTips + path_suffix}}
				</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">归档前缀：</view>
				<input class="input" type="text" v-model="archives_prefix" @input="refreshPostLinkTypeTips"/>
				<text class="view-input-text">{{ url + "/" + archives_prefix + path_suffix}}</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">分类前缀：</view>
				<input class="input" type="text" v-model="categories_prefix" />
				<text class="view-input-text">{{ url + "/" + categories_prefix + "/" + "{slug}" + path_suffix}}</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">标签前缀：</view>
				<input class="input" type="text" v-model="tags_prefix" />
				<text class="view-input-text">{{ url + "/" + tags_prefix + "/" + "{slug}" + path_suffix}}</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">自定义页面固定链接类型：</view>
				<view class="input">
					<picker @change="sheetPermalinkTypeChange" :value="sheet_permalink_typeIndex" :range="sheet_permalink_typeText">
						<view>{{sheet_permalink_typeText[sheet_permalink_typeIndex]}}</view>
					</picker>
				</view>
				<text class="view-input-text">
					{{ (sheet_permalink_typeIndex === 0 ? url + "/" + sheet_prefix + "/{slug}" : 
						url +  "/{slug}") + path_suffix}}
				</text>
			</view>
			
			<view class="view-input" v-if="sheet_permalink_typeIndex === 0">
				<view class="view-input-titleView">自定义页面前缀：</view>
				<input class="input" type="text" v-model="sheet_prefix" />
				<text class="view-input-text">{{ url + "/" + sheet_prefix + "/{slug}" + path_suffix}}</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">友情链接页面前缀：</view>
				<input class="input" type="text" v-model="links_prefix" />
				<text class="view-input-text">{{ url + "/" + links_prefix + path_suffix}}</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">图库页面前缀：</view>
				<input class="input" type="text" v-model="photos_prefix" />
				<text class="view-input-text">{{ url + "/" + photos_prefix + path_suffix}}</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">日志页面前缀：</view>
				<input class="input" type="text" v-model="journals_prefix" />
				<text class="view-input-text">{{ url + "/" + journals_prefix + path_suffix}}</text>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">路径后缀：</view>
				<input class="input" type="text" v-model="path_suffix" />
				<text class="view-input-text">* 格式为：.{suffix}，仅对内建路径有效</text>
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
				isGuest: "",
				
				post_permalink_type: "",
				post_permalink_typeText: ["默认", "年份型", "年月型", "年月日型", "ID 型", "ID 别名型"],
				post_permalink_typeValue: ["DEFAULT", "YEAR", "DATE", "DAY", "ID", "ID_SLUG"],
				post_permalink_typeIndex: 0,
				// 文章固定链接类型下方展示链接
				post_permalink_typeTips: "",
				
				archives_prefix: "",
				categories_prefix: "",
				tags_prefix: "",
			
				sheet_permalink_type: "",
				sheet_permalink_typeText: ["二级路径", "根路径"],
				sheet_permalink_typeValue: ["ROOT", "SECONDARY"],
				sheet_permalink_typeIndex: 0,

				sheet_prefix: "",
				links_prefix: "",
				photos_prefix: "",
				journals_prefix: "",
				path_suffix: "",

				popupType: "",
				popupMessage: ""
			}
		},

		mounted() {
			this.url = this.getData("url")
			this.accessToken = this.getData("access_token")
			this.isGuest = this.getData("isGuest")
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
				// 游客模式不加载数据
				if (this.isGuest === "true") {
					return
				}
				
				let array = ["post_permalink_type", "archives_prefix", "categories_prefix",
					"tags_prefix", "sheet_permalink_type", "sheet_prefix",
					"links_prefix", "photos_prefix", "journals_prefix", "path_suffix"
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
						
						that.post_permalink_type = data.post_permalink_type
						// 把服务端的数据和post_permalink_type比对，获得对应的选项索引
						that.post_permalink_typeIndex = 
							that.post_permalink_typeValue.indexOf(that.post_permalink_type)
						that.archives_prefix = data.archives_prefix
						that.categories_prefix = data.categories_prefix
						that.tags_prefix = data.tags_prefix
						that.sheet_permalink_type = data.sheet_permalink_type
						// 把服务端的数据和sheet_permalink_type比对，获得对应的选项索引
						that.sheet_permalink_typeIndex = 
							that.sheet_permalink_typeValue.indexOf(that.sheet_permalink_type)
						that.sheet_prefix = data.sheet_prefix
						that.links_prefix = data.links_prefix
						that.photos_prefix = data.photos_prefix
						that.journals_prefix = data.journals_prefix
						that.path_suffix = data.path_suffix
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
			 * 文章固定链接类型选项更改事件
			 * @param {Object} e
			 */
			postPermalinkTypeChange: function(e) {
				this.post_permalink_typeIndex = e.detail.value
				this.refreshPostLinkTypeTips()
			},
			
			/**
			 * 刷新文章固定链接类型下方提示文本
			 */
			refreshPostLinkTypeTips: function () {
				let index = this.post_permalink_typeIndex
				let date = new Date()
				let year = date.getFullYear()
				let month = date.getMonth() + 1
				let day = date.getDate()
				let tips = ""
				switch (index) {
					// 默认
					case 0:
						tips = this.archives_prefix + "/{slug}"
						break;
					// 年份型
					case 1:
						tips = year + "/{slug}"
						break;
					// 年月型
					case 2:
						tips = year + "/" + month + "/{slug}"
						break;
					// 年月日型
					case 3:
						tips = year + "/" + month + "/" + day + "/{slug}"
						break;
					// ID 型
					case 4:
						tips = "?p={id}"
						break;
					// ID 别名型
					case 5:
						tips = this.archives_prefix + "/{id}"
						break;
				}
				this.post_permalink_typeTips = tips
			},
			
			/**
			 * 自定义页面固定链接类型选项更改事件
			 * @param {Object} e
			 */
			sheetPermalinkTypeChange: function(e) {
				this.sheet_permalink_typeIndex = e.detail.value
			},
			

			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let json = {
					"post_permalink_type": this.post_permalink_typeValue[this.post_permalink_typeIndex],
					"archives_prefix": this.archives_prefix,
					"categories_prefix": this.categories_prefix,
					"tags_prefix": this.tags_prefix,
					"sheet_permalink_type": this.sheet_permalink_typeValue[this.sheet_permalink_typeIndex],
					"sheet_prefix": this.sheet_prefix,
					"links_prefix": this.links_prefix,
					"photos_prefix": this.photos_prefix,
					"journals_prefix": this.journals_prefix,
					"path_suffix": this.path_suffix
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
	.container {
		padding-bottom: 40px;
	}
	.block {
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
