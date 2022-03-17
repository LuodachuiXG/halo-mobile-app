<template>
	<view>
		<u-notify ref="popup"></u-notify>

		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>名称：</view>
				<input class="input" type="text" v-model="name" />
				<text class="view-input-text">* 页面上所显示的名称</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">别名：</view>
				<input class="input" type="text" v-model="slug" />
				<text class="view-input-text">* 一般为单个分类页面的标识，最好为英文</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">上级目录：</view>
				<view class="input">
					<picker @change="parentChange" :value="parentIndex" :range="parentText">
						<view>{{parentText[parentIndex]}}</view>
					</picker>
				</view>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">封面图：</view>
				<view class="right-button-input">
					<input class="input" type="text" v-model="thumbnail" />
					<image src="/static/images/picture.png" @click="selectAttachment('thumbnail')"></image>
				</view>
				<text class="view-input-text">* 在分类页面可展示，需要主题支持</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">密码：</view>
				<input class="input" type="password" v-model="password" />
				<text class="view-input-text">* 分类密码</text>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">描述：</view>
				<textarea class="input" v-model="description"></textarea>
				<text class="view-input-text">* 分类描述，需要主题支持</text>
			</view>

			<button class="button save-button" type="primary" @click="saving">
				{{ type == "add" ? "添加" : "保存" }}
			</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				categoryId: "",
				// 分类目录页面的分类在数组中的索引
				categoryIndex: 0,
				// 分类目录页面的分类数组
				categories: [],
				type: "",

				name: "",
				slug: "",
				
				// 所有分类目录的名称
				parentText: [],
				// 所有分类目录的id
				parentValue: [],
				parentIndex: 0,
				
				thumbnail: "",
				password: "",
				description: "",
				
				
				popupType: "",
				popupMessage: "",
			}
		},
		onLoad: function(res) {
			// 获取所有页面栈实例列表
			let pages = getCurrentPages()
			// 当前页面实例
			let nowPage = pages[pages.length - 1]
			// 上一页面实例
			let prevPage = pages[pages.length - 2]
			// 检索上一个页面的变量
			this.categories = prevPage.$vm['categories'];
			// 将所有分类目录的名称和id保存到 “上级目录” 选项卡的数组中
			for (var i = 0; i < this.categories.length; i++) {
				// 因为要在数组的 0 索引预留一个 “0” 选项，所以这里 +1
				this.parentText[i + 1] = this.categories[i].name;
				this.parentValue[i + 1] = this.categories[i].id;
			}
			this.parentText[0] = "0";
			this.parentValue[0] = 0;
			
			
			// 判断当前是添加还是修改分类目录
			if (res.type == "add") {
				// 添加分类目录
				this.type = "add";
				uni.setNavigationBarTitle({
					title: "添加分类目录"
				})
			} else {
				// 修改分类目录
				this.categoryId = res.id;
				this.type = "update";
				this.categoryIndex = res.index;
				uni.setNavigationBarTitle({
					title: "修改分类目录"
				})

				this.name = this.categories[this.categoryIndex].name;
				this.slug = this.categories[this.categoryIndex].slug;
				this.thumbnail = this.categories[this.categoryIndex].thumbnail;
				this.password = this.categories[this.categoryIndex].password;
				this.description = this.categories[this.categoryIndex].description;
				
				// 找出当前分类目录的父目录在目录数组中的索引
				let parentId = this.categories[this.categoryIndex].parentId;
				for(var i = 0; i < this.categories.length; i++) {
					if (this.categories[i].id == parentId) {
						// 因为目录数组中 0 位置预留了一个 “0”，所以真实的父目录索引要 +1
						this.parentIndex = i + 1;
						break;
					}
				}
			}
		},
		mounted() {
			
		},

		methods: {
			/**
			 * 上级目录选项更改事件
			 * @param {Object} e
			 */
			parentChange: function(e) {
				this.parentIndex = e.detail.value
			},
			
			/**
			 * 打开新窗口选择附件
			 * @param {Object} attrName 当前页面的变量，传递给附件选择页，用于选择附件后修改的变量
			 */
			selectAttachment: function(attrName) {
				uni.navigateTo({
					url: "../../attachment/selectAttachment/selectAttachment?attrName=" + attrName
				})
			},
			
			/**
			 * 保存按钮单击事件
			 */
			saving: function() {
				let that = this;
				if (this.name.length <= 0) {
					this.popup("请将名称填写完整");
					return ;
				}
				
				let json = {"name": this.name,
					"slug": this.slug,
					"parentId": this.parentValue[this.parentIndex],
					"thumbnail": this.thumbnail,
					"password": this.password,
					"description": this.description};
					
				if (this.type == "add") {
					// 当前是添加分类目录
					uni.request({
						method: "POST",
						dataType: "json",
						url: this.getUrl() + "/api/admin/categories",
						header: {
							"Content-Type": "application/json",
							"ADMIN-Authorization": this.getAccessToken()
						},
						data: json,
						success: function(res) {
							
							if (res.statusCode !== 200) {
								that.popup("添加分类目录失败")
								// 登录过期
								if (that.isExpiredByRequest(res)) {
									that.setData("isLogin", "false")
									uni.reLaunch({
										url: "../../me/me"
									})
								}
								return;
							}
							
							uni.showToast({
								title: "添加分类目录成功",
								icon: "none",
								position: "bottom"
							})
							uni.navigateBack({
								delta:1
							})
						},
						fail: function(e) {
							uni.stopPullDownRefresh();
							uni.showModal({
								title: "添加分类目录失败",
								content: e.errMsg
							});
						}
					})
				} else {
					// 当前是修改分类目录
					uni.request({
						method: "PUT",
						dataType: "json",
						url: this.getUrl() + "/api/admin/categories/" + this.categoryId,
						header: {
							"Content-Type": "application/json",
							"ADMIN-Authorization": this.getAccessToken()
						},
						data: json,
						success: function(res) {
							if (res.statusCode !== 200) {
								that.popup("修改分类目录失败")
								// 登录过期
								if (that.isExpiredByRequest(res)) {
									that.setData("isLogin", "false")
									uni.reLaunch({
										url: "../../me/me"
									})
								}
								return;
							}
							
							uni.showToast({
								title: "修改分类目录成功",
								icon: "none",
								position: "bottom"
							})
							uni.navigateBack({
								delta:1
							})
						},
						fail: function(e) {
							uni.stopPullDownRefresh();
							uni.showModal({
								title: "修改分类目录失败",
								content: e.errMsg
							});
						}
					})
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
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
