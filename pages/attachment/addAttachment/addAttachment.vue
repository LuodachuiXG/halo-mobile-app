<template>
	<view>
		<u-notice-bar :text="notice" direction="column" :duration="2800"></u-notice-bar>
		<view class="block">
			<u-upload 
				:fileList="fileList"
				@afterRead="afterRead" 
				name="imgUpload" 
				multiple 
				:maxCount="20"
				:previewFullImage="true" 
				:deletable="false"
				:disabled="isUploading"
				uploadText="选择图片"></u-upload>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 上传的图片列表
				fileList: [],
				
				// 标记当前是否正在上传中
				isUploading: false,
				
				notice: ["上传图片时如果不选择\"原图\",会对图片进行压缩", "拍摄的图片会默认进行压缩"]
			}
		},
		methods: {
			/**
			 * 上传图片组件读取事件
			 * @param {Object} e
			 */
			async afterRead(event) {
				let that = this;
				let lists = [].concat(event.file);
				let fileListLen = this.fileList.length;
				// 将选择的图片加入上传列表中
				lists.map((item) => {
					this.fileList.push({
						...item,
						status: "uploading",
						message: '上传中'
					});
				});
				this.isUploading = true;
				for (let i = 0; i < lists.length; i++) {
					let item = this.fileList[fileListLen];
					await this.uploadImagePromise(lists[i].url).then(data => {
						that.fileList.splice(fileListLen, 1, Object.assign(item, {
							status: "success",
							message: ""
						}));
					}).catch(err => {
						that.fileList.splice(fileListLen, 1, Object.assign(item, {
							status: "uploading",
							message: "上传失败"
						}));
					});
					console.log(i);
					fileListLen++;
				}
				this.isUploading = false;
			},
			
			/**
			 * 上传图片
			 * @param {Object} url
			 */
			uploadImagePromise: function(url) {
				let that = this;
				return new Promise((resolved, rejected) => {
					uni.uploadFile({
						url: that.getUrl() + "/api/admin/attachments/upload",
						filePath: url,
						name: 'file',
						header: {
							"ADMIN-Authorization": that.getAccessToken()
						},
						success: (res) => {
							resolved(res);
						},
						fail: (err) => {
							rejected(err.errMsg);
						}
					});
				});
			}
		}
	}
</script>

<style>
	.block {
		padding: 20rpx;
		padding-bottom: 0rpx;
	}
</style>
