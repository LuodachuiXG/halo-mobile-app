<script>
	export default {
		onLaunch: function() {
			console.warn('当前组件仅支持 uni_modules 目录结构 ，请升级 HBuilderX 到 3.1.0 版本以上！')
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import '@/uni_modules/uni-scss';
	/* #ifndef APP-NVUE */
	@import '@/static/customicons.css';
	
	

	// 设置整个项目的背景色
	uni-page-body,
	html,
	body {
		width: 100%;
		height: 100%;
		--primaryColor: #007AFF;
		--textPrimaryColor: #616255;
		--activatedColor: #e2e2e2;
		--warningColor: #f3a73f;
		--errorColor: #e43d33;
		--successColor: #18bc37;
	}

	page {
		width: 100%;
		height: 100%;
		background-color: #F0F2F5;
	}

	/* #endif */
	.example-info {
		font-size: 14px;
		color: #333;
		padding: 10px;
	}
	
	* {
		transition-property: background-color;
		transition-duration: .2s;
		transition-timing-function: ease-out;
	}
	
	.input {
		border: 1px solid #D9D9D9;
		padding: 5px;
		padding-left: 10px;
		padding-right: 10px;
		border-radius: 4px;
		color: #595959;
	}
	
	.input:hover{
		border: 1px solid #597EF7;
		box-shadow: 0px 0 3px .1px rgba(0, 122, 255, .5)
	}
	
	.button {
		font-size: 1.1em;
		height: 40px;
		line-height: 40px;
		// background-color: #007AFF;
	}
	
	.block {
		background-color: #FFFFFF;
		margin: 30rpx;
		box-shadow: 0 2px 10px rgba(216, 216, 216, .5);
		border-radius: 6px;
	}
	
	.item {
		position: relative;
		padding: 30rpx;
		border-bottom: 1px solid #eeeeee;
		line-height: 40rpx;
	}
		
	.item:first-child {
		border-radius: 6px 6px 0px 0px;
	}
		
	.item:last-child {
		border-radius: 0px 0px 6px 6px;
	}
		
	.item:active {
		background-color: #e9ebec;
	}
	
	.item:last-child {
		border-bottom: none;
	}
	
	.item text {
		margin-left: 20rpx;
		font-size: .9em;
		color: #616255;
	}
	
	.item image {
		position: relative;
		top: 9rpx;
		width: 35rpx;
		height: 35rpx;
	}
	
	.item-sign {
		position: absolute;
		top: calc(50% - 15rpx);
		right: 40rpx;
		background-color: #cbcbcb;
		width: 8rpx;
		height: 30rpx;
		border-radius: 99999px;
	}
	
	.block-title {
		display: inline-block;
		margin-left: 40rpx;
		margin-right: 30rpx;
		margin-top: 20rpx;
		margin-bottom: -20rpx;
		position: relative;
		z-index: 10;
	}
	
	.block-title text {
		margin-left: 15rpx;
		font-size: 1.1em;
		font-weight: bold;
		color: #FFFFFF;
		text-shadow: 2px 2px 5px rgba(0, 0, 0,.5);
	}
	
	
	.block-title view {
		position: absolute;
		top: 40%;
		width: 130%;
		height: 20px;
		border-radius: 999999px;
		background-color: var(--primaryColor);
		z-index: -1;
		opacity: 1;
	}
	
	.view-input {
		margin: 30rpx;
		margin-left: 20rpx;
		margin-right: 20rpx;
	}
	.view-input-titleView text {
		position: relative;
		top: 6rpx;
		margin-right: 10rpx;
	}
	
	.view-input textarea{
		width: auto;
	}
	.view-input-titleView {
		margin-bottom: 10rpx;
		color: #616255;
	}
	
	// 右边带按钮input
	.right-button-input{
		position: relative;
	}
	
	.right-button-input input{
		padding-right: 70rpx;
	}
	
	.right-button-input image{
		width: 45rpx;
		height: 45rpx;
		position: absolute;
		top: calc(50% - 22.5rpx);
		right: 15rpx;
	}
	
	// 输入框下面的灰色提示小字
	.view-input-text {
		color: #9d9d9d;
		font-size: .8em;
	}
	
	// 蓝色背景圆角小提示
	.tips-info, .tips-warning{
		font-size: .8em;
		color: white;
		padding-right: 20rpx;
		padding-left: 20rpx;
		display: inline;
		height: 20px;
		border-radius: 999999px;
		background-color: var(--primaryColor);
	}
	
	.tips-warning{
		background-color: var(--warningColor);
	}
</style>
