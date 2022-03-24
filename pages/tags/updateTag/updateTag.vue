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
				<view class="view-input-titleView">颜色：</view>
				<input class="input" type="text" v-model="color" @click="onColorClick" disabled="true"/>
				<view class="color-show" :style="'background-color:' + color"></view>
				<t-color-picker ref="colorPicker" @confirm="onColorConfirm" :color="hexToRgb(color)"></t-color-picker>
			</view>

			<view class="view-input">
				<view class="view-input-titleView">封面图：</view>
				<view class="right-button-input">
					<input class="input" type="text" v-model="thumbnail" />
					<image src="/static/images/picture.png" @click="selectAttachment('thumbnail')"></image>
				</view>
				<text class="view-input-text">* 在分类页面可展示，需要主题支持</text>
			</view>

			<button class="button save-button" type="primary" @click="saving">
				{{ type == "add" ? "添加" : "保存" }}
			</button>
		</view>
	</view>
</template>

<script>
	import {
		addTag,
		updateTag
	} from "../../../common/api.js";
	export default {
		data() {
			return {
				tagId: "",
				// 上个标签 tags 页面中，点击的要修改的标签在 tags 变量中的索引
				tagIndex: 0,
				// 标签页面的标签数组
				tags: [],
				type: "",

				name: "",
				slug: "",
				color: "",
				thumbnail: "",
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
			this.tags = prevPage.$vm['tags'];

			// 判断当前是添加还是修改标签
			if (res.type == "add") {
				// 添加标签
				this.type = "add";
				uni.setNavigationBarTitle({
					title: "添加标签"
				})
			} else {
				// 修改标签
				this.tagId = res.id;
				this.type = "update";
				this.tagIndex = res.index;
				uni.setNavigationBarTitle({
					title: "修改标签"
				})

				this.name = this.tags[this.tagIndex].name;
				this.slug = this.tags[this.tagIndex].slug;
				this.thumbnail = this.tags[this.tagIndex].thumbnail;
				this.color = this.tags[this.tagIndex].color;
			}
		},
		mounted() {
			
		},

		methods: {
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
					return;
				}

				let json = {
					"name": this.name,
					"slug": this.slug,
					"thumbnail": this.thumbnail,
					"color": this.color
				};

				if (this.type == "add") {
					// 当前是添加标签
					addTag(json).then(data => {
						that.toast("添加标签成功");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "添加标签失败",
							content: err
						});
					});
				} else {
					// 当前是修改标签
					updateTag(this.tagId, json).then(data => {
						that.toast("修改标签成功");
						uni.navigateBack({
							delta: 1
						});
					}).catch(err => {
						uni.showModal({
							title: "修改标签失败",
							content: err
						});
					});

				}
			},
			
			
			/**
			 * 选择颜色点击选项，弹出颜色选择框
			 */
			onColorClick: function() {
				this.$refs.colorPicker.open();
			},
			
			/**
			 * 颜色选择器选择事件
			 * @param {Object} e
			 */
			onColorConfirm: function(e) {
				this.color = e.hex;
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
	.color-show {
		position: absolute;
		top: 32px;
		right: 10px;
		border-radius: 9999px;
		width: 24px;
		height: 24px;
	}
</style>
