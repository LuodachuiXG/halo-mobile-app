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
	@import '@/uni_modules/uview-ui/index.scss';
	/*每个页面公共css */
	@import '@/uni_modules/uni-scss';

	// 图标集
	@font-face {
		font-family: 'iconfont';
		src: url('@/static/iconfont.ttf?t=1658598266679') format('truetype'),
			url('@/static/iconfont.woff2?t=1658598266679') format('woff2'),
			url('@/static/iconfont.woff?t=1658598266679') format('woff');
	}
	// 图标集
	.iconfont {
		font-family: "iconfont" !important;
		font-style: normal;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}


	// 设置整个项目的背景色
	uni-page-body,
	html,
	body {
		width: 100%;
		height: 100%;
		--primaryColor: #007AFF;
		--primaryDarkColor: #0067ce;
		--textPrimaryColor: #616255;
		--textContentColor: #495060;
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
		color: var(--textPrimaryColor);
	}

	.input:hover {
		border: 1px solid #597EF7;
		box-shadow: 0px 0 3px .1px rgba(0, 122, 255, .5)
	}

	.button {
		font-size: 1.1em;
		height: 40px;
		line-height: 40px;
	}

	.block {
		background-color: white;
		margin: 30rpx;
		box-shadow: 0 2px 10px rgba(216, 216, 216, .5);
		border-radius: 4px;
	}
	
	.active:active{
		background-color: var(--activatedColor);
	}

	.item {
		position: relative;
		padding: 30rpx;
		border-bottom: 1px solid #eeeeee;
		line-height: 40rpx;
	}

	.item:first-child {
		border-radius: 4px 4px 0px 0px;
	}

	.item:last-child {
		border-radius: 0px 0px 4px 4px;
		border-bottom: none;
	}

	.item:active {
		background-color: var(--activatedColor);
	}
	
	.item-icon {
		color: var(--textPrimaryColor);
		font-size: 40rpx;
		position: relative;
		top: 5rpx;
	}
	
	.item-title {
		margin-left: 18rpx;
		font-size: 28rpx;
		color: var(--textPrimaryColor);
	}

	.item-sign {
		position: absolute;
		top: calc(50% - 15rpx);
		right: 40rpx;
		background-color: #cbcbcb;
		width: 4rpx;
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
		color: white;
		text-shadow: 2px 2px 5px rgba(0, 0, 0, .5);
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
		position: relative;
		margin: 30rpx;
		margin-left: 20rpx;
		margin-right: 20rpx;
	}

	.view-input-titleView text {
		position: relative;
		top: 6rpx;
		margin-right: 10rpx;
	}

	.view-input textarea {
		width: auto;
	}

	.view-input-titleView {
		margin-bottom: 10rpx;
		color: #616255;
	}

	// 右边带按钮input
	.right-button-input {
		position: relative;
	}

	.right-button-input input {
		padding-right: 70rpx;
	}

	.right-button-input image {
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
	.tips-info,
	.tips-warning {
		font-size: .8em;
		color: white;
		padding-right: 20rpx;
		padding-left: 20rpx;
		display: inline;
		height: 20px;
		line-height: 20px;
		border-radius: 999999px;
		background-color: var(--primaryColor);
	}

	.tips-warning {
		background-color: var(--warningColor);
	}
	
	.color-info{
		color: var(--primaryColor);
	}
	
	.color-error {
		color: var(--errorColor);
	}
	
	.color-warning {
		color: var(--warningColor);
	}
	
	.color-success {
		color: var(--successColor);
	}
	
	.background-error {
		background-color: var(--errorColor);
	}
	
	.background-warning {
		background-color: var(--warningColor);
	}

	// 点击时不改变背景颜色
	.noActive:hover {
		background-color: white;
	}
	
	// 设置选项单元格
	.item-option {
		position: relative;
		padding: 30rpx;
		border-bottom: 1px solid #eeeeee;
		line-height: 40rpx;
	}
	
	.item-option-title {
		margin-left: 0rpx;
		font-size: 28rpx;
		color: var(--textPrimaryColor);
	}
	
	.item-option:first-child {
		border-radius: 4px 4px 0px 0px;
	}
	
	.item-option:last-child {
		border-radius: 0px 0px 4px 4px;
		border-bottom: none;
	}
	
	.item-option-switch {
		position: absolute;
		top: 25rpx;
		right: 30rpx;
	}
	
</style>
