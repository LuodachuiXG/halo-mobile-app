<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>

		<!-- 基本设置 -->
		<view class="block-title">
			<text>基本设置</text>
			<view></view>
		</view>
		<view class="block">
			<!-- 文章标题 -->
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>文章标题：</view>
				<input class="input" type="text" v-model="title" />
			</view>

			<!-- 文章别名 -->
			<view class="view-input">
				<view class="view-input-titleView">文章别名：</view>
				<input class="input" type="text" v-model="slug" />
			</view>

			<!-- 发表时间 -->
			<view class="view-input">
				<view class="view-input-titleView">发表时间：</view>
				<input class="input" type="text" :value="createTime" disabled="true" @click="onUpdateCreateTimeClick" />
				<w-picker :visible.sync="dateVisible" mode="date" startYear="1970" endYear="2037" :value="createTime"
					:current="false" fields="second" @confirm="onCreateTimeConfirm($event,'date1')" :disabled-after="false"
					ref="date1"></w-picker>
			</view>

			<!-- 开启评论 -->
			<view class="view-input">
				<view class="view-input-titleView">开启评论：</view>
				<radio-group @change="onCommentRadioChange">
					<label><radio value="true" :checked="!disallowComment"/>开启</label>
					<label style="margin-left: 30rpx;">
						<radio value="false" :checked="disallowComment"/>关闭</label>
				</radio-group>
			</view>
			
			<!-- 置顶 -->
			<view class="view-input">
				<view class="view-input-titleView">是否置顶：</view>
				<radio-group @change="onToppedRadioChange">
					<label><radio value="true" :checked="topped"/>是</label>
					<label style="margin-left: 30rpx;">
						<radio value="false" :checked="!topped"/>否</label>
				</radio-group>
			</view>
			
			<!-- 分割线 -->
			<view class="split"></view>

			<!-- 分类目录 -->
			<view class="view-input" style="margin-right: 0rpx;">
				<view class="view-input-titleView">分类目录：</view>
				<uni-data-checkbox :wrap="true" mode="button" multiple v-model="categoriesValue"
					:localdata="categoriesRange"></uni-data-checkbox>
				
				<view style="margin-top: 30rpx;" v-if="addCategory">
					<view class="input" style="margin-right: 20rpx;">
						<picker @change="parentChange" :value="parentIndex" :range="parentText">
							<view>{{parentText[parentIndex]}}</view>
						</picker>
					</view>
					<view class="view-input">
						<input class="input" type="text" v-model="categoryName" 
							style="margin-left: -20rpx;" placeholder="分类名称"/>
					</view>
					<view class="view-input">
						<input class="input" type="text" v-model="categorySlug" 
							style="margin-left: -20rpx;" placeholder="分类别名"/>
					</view>
					
				</view>
				
				<button class="btn add-btn" @click="onAddCategoryClick" v-if="!addCategory" 
					style="margin-top: 20rpx;">新增分类</button>
				<view v-if="addCategory" style="position: relative;">
					<button class="btn save-btn" @click="onSaveClick">保存</button>
					<button class="btn cancel-btn" @click="onCancelClick">返回</button>
				</view>
			</view>
			
			<view class="split"></view>
			
			<!-- 标签 -->
			<view class="view-input" style="margin-right: 0rpx;">
				<view class="view-input-titleView">标签：</view>		 			 
				<view class="block-tag">
					<view class="block-tag-item"v-for="(tag, j) in tagsRange">
						<u-tag :text="tag.text" :plain="!(tagsValue.indexOf(tag.value) > -1)" 
							 type="success" @click="onTagClick(tag.value)"></u-tag>	
					</view>
				</view>	 

				<uni-tag class="block-tag-item" text="新增 +" 
					type="default" :inverted="true" @click="onAddTagClick"></uni-tag>
			</view>
			
			<view class="split"></view>
			
			<!-- 摘要 -->
			<view class="view-input">
				<view class="view-input-titleView">摘要：</view>
				<textarea class="input" v-model="summary" 
					placeholder="如不填写,将从文章中自动截取"></textarea>
			</view>
			
			<view class="split"></view>
			
			<!-- 封面图 -->
			<view class="view-input">
				<view class="view-input-titleView">封面图：</view>
				<!-- 判断图片地址是否是绝对地址 -->
				<image :src="thumbnail.length <= 0 ? '/static/images/nothumbnail.jpg' 
							: (thumbnail.indexOf('http') < 0  ? getUrl() + thumbnail : thumbnail)" 
							style="width: 100%;"></image>
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
				<button class="btn right-btn" type="primary" @click="releasePost">发布</button>
			</view>
		</view>
		
		<!-- 高级设置 -->
		<view class="block-title" style="margin-top: -20rpx;">
			<text>高级设置</text>
			<view></view>
		</view>
		<view class="block" style="margin-bottom: 60rpx;">
			<uni-collapse @change="onCollapseChange">
				<uni-collapse-item title="高级设置" >
					<!-- 访问密码 -->
					<view class="view-input">
						<view class="view-input-titleView">访问密码：</view>
						<uni-easyinput type="password" v-model="password"></uni-easyinput>
					</view>
					
					<!-- SEO设置 -->
					<view class="view-input">
						<view class="view-input-titleView">SEO 自定义关键词：</view>
						<input class="input" type="text" v-model="metaKeywords"/>
						<text class="view-input-text">
							多个关键字以英文逗号隔开，如不填将自动使用标签作为关键字
							</text>
					</view>
					
					<!-- SEO自定义描述 -->
					<view class="view-input">
						<view class="view-input-titleView">SEO 自定义描述：</view>
						<textarea class="input" v-model="metaDescription" 
							placeholder="如不填写,会从文章中自动截取"></textarea>
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
					
					<button class="button save-button" type="primary" @click="saving" v-if="advanced && type === 'update'">
						保存
					</button>
					<view class="view-input" style="position: relative;margin-top: 20px;" v-if="advanced && type === 'add'">
						<button class="btn left-btn" type="warn" @click="saving(1)">保存草稿</button>
						<button class="btn right-btn" type="primary" @click="releasePost">发布</button>
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
		getPost,
		getCategories,
		getTags,
		addCategory,
		addTag,
		updatePost,
		addPost
		
	} from "../../../common/api.js";
	export default {
		data() {
			return {
				// 标记当前是旧文章的设置更新，还是新文章的设置发布，add/update
				type: "",
				
				post: [],
				postId: 0,
				categories: [],
				tags: [],

				title: "",
				slug: "",
				
				// 格式化后的时间
				createTime: "",
				// 时间戳
				createTimeStamp: 0,
				
				// 控制时间日期选择器是否显示
				dateVisible: false,
				
				disallowComment: false,
				topped: false,
				
				// 记录分类目录选择的索引和分类目录数据
				categoriesValue: [],
				categoriesRange: [],
				
				// 所有分类的名称，用于新增分类的时候选择父分类
				parentText: [],
				// 所有分类的id
				parentValue: [],
				parentIndex: 0,
				
				// 是否显示新增分类的表单
				addCategory: false,
				
				// 新增分类名称
				categoryName: "",
				categorySlug: "",
				
				// 记录标签选择的索引和标签数据
				tagsValue: [],
				tagsRange: [],
				
				// 摘要
				summary: "",
				
				// 封面图
				thumbnail: "",
				
				// 标记高级设置折叠菜单是否展开
				advanced: false,
				
				password: "",
				
				// SEO 自定义关键词
				metaKeywords: "",
				// SEO 自定义描述
				metaDescription: "",
				
				metaIds: [],
				metas: [],

				popupType: "",
				popupMessage: "",
			}
		},
		onLoad(res) {
			// 如果上个页面没有传来文章 id， 证明当前是 新增/编辑文章 页面跳转的文章设置
			if (res.id == undefined) {
				this.type = "add";
			} else {
				this.postId = res.id;
				this.type = "update";
			}
			this.refreshPostData();
		},

		mounted() {
			
		},

		methods: {
			/**
			 * 刷新文章数据
			 */
			refreshPostData: function() {
				let mPost;
				if (this.type === "add") {
					// 当前是 新增/编辑文章 页面跳转的文章设置，从上个页面直接读取 post
					let pages = getCurrentPages();
					// 上一页面实例
					let prevPage = pages[pages.length - 2];
					mPost = prevPage.$vm._data.post;
					this.postId = 
						(mPost.id !== undefined  && mPost.id !== 0) ? mPost.id : 0;
					this.title = mPost.title;
					this.slug = mPost.slug == undefined ? '' : mPost.slug;
					this.topped = mPost.topped == undefined ? false : mPost.topped;
					this.createTimeStamp = mPost.createTime == undefined ? new Date() : mPost.createTime;
					this.createTime = this.format(this.createTimeStamp);
					this.disallowComment = mPost.disallowComment == undefined ? false : mPost.disallowComment;
					// 保存当前文章的分类目录ID
					this.categoriesValue = mPost.categoryIds == undefined ? [] : mPost.categoryIds;
					// 保存当前文章的标签ID
					this.tagsValue = mPost.tagIds == undefined ? [] :  mPost.tagIds;
					this.thumbnail = mPost.thumbnail == undefined ? "" :  mPost.thumbnail;
					this.summary = mPost.summary == undefined ? "" :  mPost.summary;
					this.password = mPost.password == undefined ? "" :  mPost.password;
					this.metaKeywords = mPost.metaKeywords == undefined ? "" :  mPost.metaKeywords;
					this.metaDescription = mPost.metaDescription == undefined ? "" :  mPost.metaDescription;
					this.metaIds = mPost.metaIds == undefined ? [] :  mPost.metaIds;
					this.metas = mPost.metas == undefined ? [] :  mPost.metas;
					
					
					// 对 this.post 进行初始化，否则如果是新文章的话，提交数据会没有主体
					this.post = {
						"id": this.postId,
						"title" : this.title,
						"slug" : this.slug,
						"topPriority" : this.topped ? 1 : 0,
						"createTime" : this.createTime,
						"disallowComment" : this.disallowComment,
						"thumbnail" : this.thumbnail,
						"summary" : this.summary,
						"password" : this.password,
						"metaKeywords" : this.metaKeywords,
						"metaDescription" : this.metaDescription,
						"metaIds" : this.metaIds,
						"metas" : this.metas,
						"originalContent" : mPost.originalContent
					};
				} else {
					// 当前是修改文章设置模式，通过文章 id 获取文章数据
					let that = this;
					getPost(this.postId).then(data => {
						mPost = data;
						that.post = mPost;
						that.title = mPost.title;
						that.slug = mPost.slug;
						that.topped = mPost.topped;
						that.createTimeStamp = mPost.createTime;
						that.createTime = that.format(that.createTimeStamp);
						that.disallowComment = mPost.disallowComment;
						// 保存当前文章的分类目录ID
						that.categoriesValue = mPost.categoryIds;
						// 保存当前文章的标签ID
						that.tagsValue = mPost.tagIds;
						that.thumbnail = mPost.thumbnail;
						that.summary = mPost.summary;
						that.password = mPost.password;
						that.metaKeywords = mPost.metaKeywords;
						that.metaDescription = mPost.metaDescription;
						that.metaIds = mPost.metaIds;
						that.metas = mPost.metas;
					}).catch(err => {
						uni.showModal({
							title: "获取文章数据失败",
							content: err
						});
					});
				}
				// 刷新分类数据
				this.refreshCategoryData();
				// 刷新标签数据
				this.refreshTagData();
			},
			
			
			/**
			 * 刷新分类数据
			 */
			refreshCategoryData: function() {
				let that = this;
				getCategories("false").then(data => {
					that.categories = data;
					that.categoriesRange = [];
					// 将分类目录的数据按照 [{"value": 分类ID,"text": 分类名	}]的格式存入数组
					for (var i = 0; i < that.categories.length; i++) {
						let categoriesId = that.categories[i].id;
						let categoriesName = that.categories[i].name;
						let json = {"value": categoriesId,
							"text": categoriesName};
						that.categoriesRange.push(json)
					}
					
					// 将所有分类目录的名称和id保存到新增分类的 “上级目录” 选项卡的数组中
					for (var i = 0; i < that.categories.length; i++) {
						// 因为要在数组的 0 索引预留一个 “0” 选项，所以这里 +1
						that.parentText[i + 1] = that.categories[i].name;
						that.parentValue[i + 1] = that.categories[i].id;
					}
					that.parentText[0] = "0";
					that.parentValue[0] = 0;
					
				}).catch(err => {
					uni.showModal({
						title: "获取分类数据失败",
						content: err
					});
				});
			},
			
			
			/**
			 * 刷新标签数据
			 */
			refreshTagData: function() {
				let that = this;
				
				getTags().then(data => {
					that.tags = data;
					that.tagsRange = [];
					// 将标签的数据按照 [{"value": 标签ID,"text": 标签名	}]的格式存入数组
					for (var i = 0; i < that.tags.length; i++) {
						let tagId = that.tags[i].id;
						let tagName = that.tags[i].name;
						let json = {"value": tagId,
							"text": tagName};
						that.tagsRange.push(json);
					}
					
				}).catch(err => {
					uni.showModal({
						title: "获取标签数据失败",
						content: err
					});
				});
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
			 * 是否指定 radio 点击事件
			 * @param {Object} e
			 */
			onToppedRadioChange: function(e) {
				this.topped = e.detail.value == "true" ? true : false;
			},
			
			/**
			 * 新增分类按钮点击事件
			 */
			onAddCategoryClick: function() {
				this.addCategory = true;
			},
			
			/**
			 * 新增分类返回按钮点击事件
			 */
			onCancelClick: function() {
				this.addCategory = false;
			},
			
			/**
			 * 新增分类保存按钮点击事件
			 */
			onSaveClick: function() {
				if (this.categoryName.length <= 0) {
					this.popup("分类名称不能为空");
					return ;
				}
				
				let that = this;
				let json = {"name": this.categoryName,
					"slug": this.categorySlug,
					"parentId": this.parentValue[this.parentIndex]};
				// 提交分类
				addCategory(json).then(data => {
					that.popup("添加分类目录成功", "success");
					that.parentIndex = 0;
					that.categoryName = "";
					that.categorySlug = "";
					that.addCategory = false;
					that.refreshCategoryData();
				}).catch(err => {
					uni.showModal({
						title: "添加分类目录失败",
						content: err
					});
				});
			},
			
			/**
			 * 上级目录选项更改事件
			 * @param {Object} e
			 */
			parentChange: function(e) {
				this.parentIndex = e.detail.value
			},
			
			/**
			 * 标签点击事件
			 * @param {Object} id
			 */
			onTagClick: function(id) {
				if (this.tagsValue.indexOf(id) > -1) {
					// 标签已经选中（已经存在数组中）
					this.tagsValue = this.arrayRemove(this.tagsValue, id);
				} else {
					// 标签未选中（不在数组中）
					this.tagsValue.push(id);
				}
			},
			
			/**
			 * 新增标签点击事件
			 */
			onAddTagClick: function() {
				let that = this;
				uni.showModal({
					title: "新增标签",
					editable: true,
					placeholderText: "标签名",
					success: function(res) {
						let thatt = that;
						if (res.confirm == true && res.content.length > 0) {
							let newTagName = res.content;
							let json = {"name": newTagName};
							// 提交标签
							addTag(json).then(data => {
								thatt.popup("添加标签成功", "success");
								thatt.refreshTagData();
							}).catch(err => {
								uni.showModal({
									title: "添加标签失败",
									content: err
								});
							});
						}
					}
				})
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
			  * @param {Object} i 0 为文章设置的保存数据，1 为新增/编辑文章的保存草稿
			 */
			saving: function(i = 0) {
				if (this.title.length <= 0) {
					this.popup("文章标题不能为空");
					return ;
				}
				
				var that = this;
				var json = this.post;
				json.title = this.title;
				json.slug = this.slug;
				json.createTime = this.createTimeStamp;
				json.metaKeywords = this.metaKeywords;
				json.metaDescription = this.metaDescription;
				json.summary = this.summary;
				json.thumbnail = this.thumbnail;
				json.disallowComment = this.disallowComment;
				json.password = this.password;
				json.tagIds = this.tagsValue;
				json.categoryIds = this.categoriesValue;
				json.metaIds = this.metaIds;
				json.topPriority = this.topped ? 1 : 0;
				console.log("id:" + json.id)
				if (i === 1) {
					console.log(1)
					// 保存草稿
					json.status = "DRAFT";
					
					if (json.id === undefined || json.id === 0) {
						// 当前文章没有 id 所以这里是创建一个文章并设置草稿状态
						addPost(json).then(data => {
							// 创建文章成功，保存 id
							that.toast("保存草稿成功")
							uni.navigateBack({
								delta: 2
							})
						}).catch(err => {
							uni.showModal({
								title: "保存草稿失败",
								content: err
							});
						});
					} else {
						// 当前文章有 id 所以是保存数据并设置草稿状态
						updatePost(json.id, json).then(data => {
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
					console.log(0)
					// i = 1，文章设置的保存数据
					updatePost(json.id, json).then(data => {
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
				let json = {"value":"", "key": ""};
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
			releasePost: function() {
				var that = this;
				var json = this.post;
				json.title = this.title;
				json.slug = this.slug;
				json.status = "PUBLISHED";
				json.createTime = this.createTimeStamp;
				json.metaKeywords = this.metaKeywords;
				json.metaDescription = this.metaDescription;
				json.summary = this.summary;
				json.thumbnail = this.thumbnail;
				json.disallowComment = this.disallowComment;
				json.password = this.password;
				json.tagIds = this.tagsValue;
				json.categoryIds = this.categoriesValue;
				json.metaIds = this.metaIds;
				json.topPriority = this.topped ? 1 : 0;
				if (json.id === undefined || json.id === 0) {
					// 当前文章没有 id 所以这里是创建文章
					addPost(json).then(data => {
						// 创建文章成功，保存 id
						that.toast("发布文章成功")
						uni.navigateBack({
							delta: 2
						})
					}).catch(err => {
						uni.showModal({
							title: "发布文章失败",
							content: err
						});
					});
				} else {
					// 当前文章有 id 所以是保存数据
					updatePost(json.id, json).then(data => {
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
		height: 60rpx;
		line-height: 60rpx;
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
