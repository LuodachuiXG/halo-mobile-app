<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>

		<view class="block">
			<!-- 页面标题 -->
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>页面标题：</view>
				<input class="input" type="text" v-model="title" />
			</view>

			<!-- 页面别名 -->
			<view class="view-input">
				<view class="view-input-titleView">页面别名：</view>
				<input class="input" type="text" v-model="slug" />
			</view>

			<!-- 发表时间 -->
			<view class="view-input">
				<view class="view-input-titleView">发表时间：</view>
				<input class="input" type="text" :value="createTime" disabled="true" @click="onUpdateCreateTimeClick" />
				<w-picker :visible.sync="dateVisible" mode="date" startYear="1970" endYear="2037" :value="createTime"
					:current="false" fields="second" @confirm="onCreateTimeConfirm($event,'date1')"
					:disabled-after="false" ref="date1"></w-picker>
			</view>

			<!-- 开启评论 -->
			<view class="view-input">
				<view class="view-input-titleView">开启评论：</view>
				<radio-group @change="onCommentRadioChange">
					<label>
						<radio value="true" :checked="!disallowComment" />开启
					</label>
					<label style="margin-left: 30rpx;">
						<radio value="false" :checked="disallowComment" />关闭
					</label>
				</radio-group>
			</view>

			<!-- 分割线 -->
			<view class="split"></view>


			<!-- 摘要 -->
			<view class="view-input">
				<view class="view-input-titleView">摘要：</view>
				<textarea class="input" v-model="summary" placeholder="如不填写,将从页面中自动截取"></textarea>
			</view>

			<view class="split"></view>

			<!-- 封面图 -->
			<view class="view-input">
				<view class="view-input-titleView">封面图：</view>
				<!-- 判断图片地址是否是绝对地址 -->
				<image :src="thumbnail.length <= 0 ? '/static/images/nothumbnail.jpg' 
							: (thumbnail.indexOf('http') < 0  ? getUrl() + thumbnail : thumbnail)" style="width: 100%;"></image>
				<view class="right-button-input">
					<input class="input" type="text" v-model="thumbnail" />
					<image src="/static/images/picture.png" @click="selectAttachment('thumbnail')"></image>
				</view>
			</view>
			<button class="button save-button" type="primary" @click="saving" v-if="!advanced && type === 'update'">
				保存
			</button>
			<view class="view-input" style="position: relative;margin-top: 20px;" v-if="!advanced && type === 'add'">
				<button class="btn left-btn" type="warn" @click="saving(1)">保存草稿</button>
				<button class="btn right-btn" type="primary" @click="releasePage">发布</button>
			</view>
		</view>

		<!-- 高级设置 -->
		<view class="block-title" style="margin-top: -20rpx;">
			<text>高级设置</text>
			<view></view>
		</view>
		<view class="block" style="margin-bottom: 60rpx;">
			<uni-collapse @change="onCollapseChange">
				<uni-collapse-item title="高级设置">

					<!-- SEO设置 -->
					<view class="view-input">
						<view class="view-input-titleView">SEO 自定义关键词：</view>
						<input class="input" type="text" v-model="metaKeywords" />
						<text class="view-input-text">
							多个关键字以英文逗号隔开
						</text>
					</view>

					<!-- SEO自定义描述 -->
					<view class="view-input">
						<view class="view-input-titleView">SEO 自定义描述：</view>
						<textarea class="input" v-model="metaDescription" placeholder="如不填写,会从页面中自动截取"></textarea>
					</view>

					<!-- 元数据 -->
					<view class="view-input" style="position: relative;">
						<view class="view-input-titleView">元数据：</view>
						<view class="view-meta" v-for="(meta, i) in metas">
							<uni-row class="row">
								<uni-col :span="11">
									<uni-easyinput type="text" :clearable="false" placeholder="K"
										placeholderStyle="font-style:italic" v-model="metas[i].key"></uni-easyinput>
								</uni-col>
								<uni-col :span="11" :push="2">
									<uni-easyinput type="text" :clearable="false" placeholder="V"
										placeholderStyle="font-style:italic" suffixIcon="closeempty"
										@iconClick="onMetaDeleteClick(i)" v-model="metas[i].value"></uni-easyinput>
								</uni-col>
							</uni-row>
						</view>
						<button class="meta-add-btn" @click="onMetaAddClick">新增</button>
					</view>

					<button class="button save-button" type="primary" @click="saving"
						v-if="advanced && type === 'update'">
						保存
					</button>
					<view class="view-input" style="position: relative;margin-top: 20px;"
						v-if="advanced && type === 'add'">
						<button class="btn left-btn" type="warn" @click="saving(1)">保存草稿</button>
						<button class="btn right-btn" type="primary" @click="releasePage">发布</button>
					</view>
				</uni-collapse-item>
			</uni-collapse>
		</view>
		<!-- 占位 -->
		<view style="margin-top: 40rpx;opacity: 0;">1</view>
	</view>
</template>

<script>
	import {
		getPage,
		updatePage,
		addPage
	} from "@/network/PageApi.js";
	export default {
		data() {
			return {
				// 标记当前是旧文章的设置更新，还是新文章的设置发布，add/update
				type: "",

				page: [],
				pageId: 0,

				title: "",
				slug: "",

				// 格式化后的时间
				createTime: "",
				// 时间戳
				createTimeStamp: 0,

				// 控制时间日期选择器是否显示
				dateVisible: false,

				// 禁止评论
				disallowComment: false,

				// 指定
				topped: false,

				// 摘要
				summary: "",

				// 封面图
				thumbnail: "",

				// SEO 自定义关键词
				metaKeywords: "",
				// SEO 自定义描述
				metaDescription: "",
				
				// 标记高级设置折叠菜单是否展开
				advanced: false,

				metaIds: [],
				metas: [],

				popupType: "",
				popupMessage: "",
			}
		},
		onLoad(res) {
			// 如果上个页面没有传来页面 id， 证明当前是 新增/编辑页面 跳转的页面设置
			if (res.id == undefined) {
				this.type = "add";
			} else {
				this.pageId = res.id;
				this.type = "update";
			}
			this.refreshPageData();
		},

		mounted() {

		},

		methods: {
			/**
			 * 刷新页面数据
			 */
			refreshPageData: function() {
				let mPage;
				if (this.type === "add") {
					// 当前是 新增/编辑页面 跳转的页面设置，从上个页面直接读取 page
					let pages = getCurrentPages();
					// 上一页面实例
					let prevPage = pages[pages.length - 2];
					mPage = prevPage.$vm._data.page;
					this.pageId =
						(mPage.id !== undefined && mPage.id !== 0) ? mPage.id : 0;
					this.title = mPage.title;
					this.slug = mPage.slug == undefined ? '' : mPage.slug;
					this.createTimeStamp = mPage.createTime == undefined ? new Date() : mPage.createTime;
					this.createTime = this.format(this.createTimeStamp);
					this.disallowComment = mPage.disallowComment == undefined ? false : mPage.disallowComment;
					this.thumbnail = mPage.thumbnail == undefined ? "" : mPage.thumbnail;
					this.summary = mPage.summary == undefined ? "" : mPage.summary;
					this.metaKeywords = mPage.metaKeywords == undefined ? "" : mPage.metaKeywords;
					this.metaDescription = mPage.metaDescription == undefined ? "" : mPage.metaDescription;
					this.metaIds = mPage.metaIds == undefined ? [] : mPage.metaIds;
					this.metas = mPage.metas == undefined ? [] : mPage.metas;


					// 对 this.page 进行初始化，否则如果是新页面的话，提交数据会没有主体
					this.page = {
						"id": this.pageId,
						"title": this.title,
						"slug": this.slug,
						"createTime": this.createTime,
						"disallowComment": this.disallowComment,
						"thumbnail": this.thumbnail,
						"summary": this.summary,
						"metaKeywords": this.metaKeywords,
						"metaDescription": this.metaDescription,
						"metaIds": this.metaIds,
						"metas": this.metas,
						"originalContent": mPage.originalContent
					};
				} else {
					// 当前是修改页面设置模式，通过页面 id 获取页面数据
					let that = this;
					getPage(this.pageId).then(data => {
						mPage = data;
						that.page = mPage;
						that.title = mPage.title;
						that.slug = mPage.slug;
						that.createTimeStamp = mPage.createTime;
						that.createTime = that.format(that.createTimeStamp);
						that.disallowComment = mPage.disallowComment;
						that.thumbnail = mPage.thumbnail;
						that.summary = mPage.summary;
						that.metaKeywords = mPage.metaKeywords;
						that.metaDescription = mPage.metaDescription;
						that.metaIds = mPage.metaIds;
						that.metas = mPage.metas;
					}).catch(err => {
						uni.showModal({
							title: "获取页面数据失败",
							content: err
						});
					});
				}
			},


			/**
			 * 发表时间框点击事件
			 */
			onUpdateCreateTimeClick: function() {
				this.dateVisible = true;
			},

			/**
			 * 开启评论 radio 点击事件
			 * @param {Object} e
			 */
			onCommentRadioChange: function(e) {
				this.disallowComment = e.detail.value == "true" ? false : true;
			},

			/**
			 * 是否置顶 radio 点击事件
			 * @param {Object} e
			 */
			onToppedRadioChange: function(e) {
				this.topped = e.detail.value == "true" ? true : false;
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
			 * 折叠面板状态改变事件
			 */
			onCollapseChange: function() {
				this.advanced = !this.advanced;
			},

			/**
			 * 保存按钮点击事件
			 * @param {Object} i 0 为页面设置的保存数据，1 为新增/编辑页面的保存草稿
			 */
			saving: function(i = 0) {
				if (this.title.length <= 0) {
					this.popup("页面标题不能为空");
					return;
				}

				var that = this;
				var json = this.page;
				json.title = this.title;
				json.slug = this.slug;
				json.createTime = this.createTimeStamp;
				json.metaKeywords = this.metaKeywords;
				json.metaDescription = this.metaDescription;
				json.summary = this.summary;
				json.thumbnail = this.thumbnail;
				json.disallowComment = this.disallowComment;
				json.metaIds = this.metaIds;
				json.topPriority = this.topped ? 1 : 0;
				if (i === 1) {
					// 保存草稿
					json.status = "DRAFT";

					if (json.id === undefined || json.id === 0) {
						// 当前页面没有 id 所以这里是创建一个页面并设置草稿状态
						addPage(json).then(data => {
							// 创建页面成功，保存 id
							that.toast("保存草稿成功")
							uni.navigateBack({
								delta: 2
							});
						}).catch(err => {
							uni.showModal({
								title: "保存草稿失败",
								content: err
							});
						});
					} else {
						// 当前页面有 id 所以是保存数据并设置草稿状态
						updatePage(json.id, json).then(data => {
							that.popup("保存草稿成功", "success");
							uni.navigateBack({
								delta: 2
							})
						}).catch(err => {
							uni.showModal({
								title: "保存草稿失败",
								content: err
							});
						});
					}
				} else {
					// i = 1，页面设置的保存数据
					updatePage(json.id, json).then(data => {
						that.popup("保存数据成功", "success");
						that.refreshPostData();
					}).catch(err => {
						uni.showModal({
							title: "保存数据失败",
							content: err
						});
					});
				}
			},

			/**
			 * 元数据新增按钮点击事件
			 */
			onMetaAddClick: function() {
				let json = {
					"value": "",
					"key": ""
				};
				this.metas.push(json);
			},

			/**
			 * 元数据删除按钮点击事件
			 * @param {Object} i 索引
			 */
			onMetaDeleteClick: function(i) {
				this.metas.splice(i, 1);
				this.metaIds.splice(i, 1);
			},

			/**
			 * 选择创建时间的确认事件
			 * @param {Object} res
			 * @param {Object} e
			 */
			onCreateTimeConfirm: function(res, e) {
				var date = new Date();
				date.setFullYear(res.obj.year);
				date.setMonth(res.obj.month - 1);
				date.setDate(res.obj.day);
				date.setHours(res.obj.hour);
				date.setMinutes(res.obj.minute);
				date.setSeconds(res.obj.second);
				this.createTimeStamp = date.getTime();
				this.createTime = this.format(this.createTimeStamp);
			},

			/**
			 * 发布按钮点击事件
			 */
			releasePage: function() {
				var that = this;
				var json = this.page;
				json.title = this.title;
				json.slug = this.slug;
				json.status = "PUBLISHED";
				json.createTime = this.createTimeStamp;
				json.metaKeywords = this.metaKeywords;
				json.metaDescription = this.metaDescription;
				json.summary = this.summary;
				json.thumbnail = this.thumbnail;
				json.disallowComment = this.disallowComment;
				json.metaIds = this.metaIds;
				json.topPriority = this.topped ? 1 : 0;
				if (json.id === undefined || json.id === 0) {
					// 当前页面没有 id 所以这里是创建页面
					addPage(json).then(data => {
						// 创建页面成功，保存 id
						that.toast("发布页面成功")
						uni.navigateBack({
							delta: 2
						})
					}).catch(err => {
						uni.showModal({
							title: "发布页面失败",
							content: err
						});
					});
				} else {
					// 当前页面有 id 所以是保存数据
					updatePage(json.id, json).then(data => {
						that.popup("保存数据成功", "success");
						uni.navigateBack({
							delta: 2
						})
					}).catch(err => {
						uni.showModal({
							title: "保存数据失败",
							content: err
						});
					});
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

	.split {
		margin-left: 20rpx;
		margin-right: 20rpx;
		height: 1px;
		background-color: var(--activatedColor);
	}

	.btn {
		color: #FFFFFF;
		height: 30px;
		line-height: 30px;
		margin-right: 20rpx;
		font-size: .9em;
	}

	.add-btn {
		background-color: var(--primaryColor);
	}

	.save-btn {
		display: inline-block;
		width: 47%;
		position: relative;
		background-color: var(--primaryColor);
	}

	.cancel-btn {
		display: inline-block;
		width: 47%;
		position: absolute;
		right: 0px;
		background-color: white;
		color: black;
	}

	.block-tag-item {
		margin-right: 20rpx;
		margin-top: 20rpx;
		display: inline-block;

	}

	.block-tag-item:first-child {
		margin-left: 0rpx;
	}

	.meta-add-btn {
		display: inline-block;
		height: 60rpx;
		line-height: 60rpx;
		width: 45.7%;
		margin-top: 10rpx;
		font-size: .9em;
	}

	.view-meta {
		margin-bottom: 20rpx;
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
</style>
