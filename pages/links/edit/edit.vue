<template>
	<view class="content">
		<u-notify ref="popup"></u-notify>

		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>网站名称：</view>
				<u--input v-model="name"></u--input>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView"><text style="color: red;">*</text>网站地址：</view>
				<u--input v-model="mUrl" placeholder="需要加上 http://"></u--input>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">Logo：</view>
				<u--input v-model="logo"></u--input>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">分组：</view>
				<u--input v-model="team"></u--input>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">排序编号：</view>
				<u--input v-model="priority" type="number"></u--input>
			</view>
			
			<view class="view-input">
				<view class="view-input-titleView">描述：</view>
				<u--textarea v-model="description" type="number"></u--textarea>
			</view>
			
			<view class="view-input" style="position: relative;margin-top: 20px;">
				<button class="btn" type="primary" @click="releasePost">提交</button>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		createLink,
		updateLink
	} from "@/network/LinkApi.js";
	export default {
		data() {
			return {
				// 当前是编辑还是新增链接，add/update
				type: "",

				name: "",
				mUrl: "",
				logo: "",
				team: "",
				priority: 0,
				description: "",
				
				link: []
			}
		},
		onLoad(res) {
			this.type = res.type;
			if (this.type === "add") {
				uni.setNavigationBarTitle({
					title: "添加友情链接"
				});
			} else {
				uni.setNavigationBarTitle({
					title: "修改友情链接"
				});
				let index = res.i;
				// 从上个页面直接读取 link
				let pages = getCurrentPages();
				// 上一页面实例
				let prevPage = pages[pages.length - 2];
				this.link = prevPage.$vm._data.links[index];
				this.name = this.link.name;
				this.mUrl = this.link.url;
				this.logo = this.link.logo;
				this.team = this.link.team;
				this.priority = this.link.priority;
				this.description = this.link.description;
			}

		},
		methods: {
			/**
			 * 发布按钮点击事件
			 */
			releasePost: function() {
				let that = this;
				let name = this.name;
				let mUrl = this.mUrl;
				let logo = this.logo;
				let team = this.team;
				let priority = this.priority;
				let description = this.description;
				if (this.name.length === 0) {
					this.popup("网站名称不能为空");
					return;
				}
				if (this.mUrl.length === 0) {
					this.popup("网站地址不能为空");
					return;
				}
				this.link.name = name;
				this.link.url = mUrl;
				this.link.logo = logo;
				this.link.team = team;
				this.link.priority = priority;
				this.link.description = description;
				
				if (this.type === "add") {
					// 新增链接
					createLink(this.link).then(data => {
						that.toast("添加成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "添加失败",
							content: err
						});
					});
				} else {
					// 编辑链接
					updateLink(this.link).then(data => {
						that.toast("修改成功", "success");
						uni.navigateBack({
							delta: 1
						})
					}).catch(err => {
						uni.showModal({
							title: "修改失败",
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
	.content {
		padding-bottom: 50px;
	}

	.block {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.btn {
		color: #FFFFFF;
		height: 60rpx;
		width: 100%;
		line-height: 60rpx;
		margin-right: 20rpx;
		font-size: .9em;
	}

</style>
