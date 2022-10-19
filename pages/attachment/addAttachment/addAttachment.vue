<template>
	<view>
		<u-notice-bar :text="notice" direction="column" :duration="2800"></u-notice-bar>
		<view class="block">
			<u-upload 
				:fileList="imgList"
				@afterRead="imgAfterRead" 
				name="imgUpload" 
				multiple 
				:maxCount="20"
				:previewFullImage="true" 
				:deletable="false"
				:disabled="isUploading"
				uploadText="选择图片"></u-upload>
		</view>
		
		<view class="block">
			<u-upload 
				accept="video"
				:fileList="videoList"
				@afterRead="videoAfterRead" 
				name="videoUpload" 
				multiple 
				:maxCount="20"
				:deletable="false"
				:disabled="isUploading"
				uploadText="选择视频"></u-upload>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 上传的图片列表
				imgList: [],
				
				// 上传的视频列表
				videoList: [],
				
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
			async imgAfterRead(event) {
				let that = this;
				let lists = [].concat(event.file);
				let imgListLen = this.imgList.length;
				// 将选择的附件加入上传列表中
				lists.map((item) => {
					this.imgList.push({
						...item,
						status: "uploading",
						message: '上传中'
					});
				});
				this.isUploading = true;
				for (let i = 0; i < lists.length; i++) {
					let item = this.imgList[imgListLen];
					await this.uploadFilePromise(lists[i].url).then(data => {
						that.imgList.splice(imgListLen, 1, Object.assign(item, {
							status: "success",
							message: ""
						}));
					}).catch(err => {
						that.imgList.splice(imgListLen, 1, Object.assign(item, {
							status: "uploading",
							message: "上传失败"
						}));
					});
					console.log(i);
					imgListLen++;
				}
				this.isUploading = false;
			},
			
			/**
			 * 上传视频组件读取事件
			 * @param {Object} e
			 */
			async videoAfterRead(event) {
				let that = this;
				let lists = [].concat(event.file);
				let videoListLen = this.videoList.length;
				// 将选择的附件加入上传列表中
				lists.map((item) => {
					this.videoList.push({
						...item,
						status: "uploading",
						message: '上传中'
					});
				});
				this.isUploading = true;
				for (let i = 0; i < lists.length; i++) {
					let item = this.videoList[videoListLen];
					await this.uploadFilePromise(lists[i].url).then(data => {
						that.videoList.splice(videoListLen, 1, Object.assign(item, {
							status: "success",
							message: ""
						}));
					}).catch(err => {
						that.videoList.splice(videoListLen, 1, Object.assign(item, {
							status: "uploading",
							message: "上传失败"
						}));
					});
					console.log(i);
					videoListLen++;
				}
				this.isUploading = false;
			},
			
			/**
			 * 上传附件
			 * @param {Object} url
			 */
			uploadFilePromise: function(url) {
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
