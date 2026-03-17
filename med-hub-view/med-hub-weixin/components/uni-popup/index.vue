<!--联系药房弹层-->
<template>
  <view class="popup-content" :class="{ 'popup-height': type === 'left' || type === 'right' }">
  	<text class="text">请联系药房进行取消！</text>
  </view>
</template>
<script src="../../pages/order/index.js"></script>
<style lang="scss">
	@mixin flex {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
	}

	@mixin height {
		/* #ifndef APP-NVUE */
		height: 100%;
		/* #endif */
		/* #ifdef APP-NVUE */
		flex: 1;
		/* #endif */
	}

	.box {
		@include flex;
	}

	.button {
		@include flex;
		align-items: center;
		justify-content: center;
		flex: 1;
		height: 35px;
		margin: 0 5px;
		border-radius: 5px;
	}

	.example-body {
		background-color: #fff;
		padding: 10px 0;
	}

	.button-text {
		color: #fff;
		font-size: 12px;
	}

	.popup-content {
		@include flex;
		align-items: center;
		justify-content: center;
		padding: $uni-spacing-row-lg;
		height: auto;
		background-color: $uni-bg-color;
		border-radius: $uni-border-radius-xl;
		box-shadow: $uni-shadow-lg;
	}

	.popup-height {
		@include height;
		width: 200px;
	}

	.text {
		font-size: $uni-font-size-base;
		color: $uni-text-color-secondary;
	}

	.popup-success {
		color: #fff;
		background-color: $tag-bg-success;
	}

	.popup-warn {
		color: #fff;
		background-color: $tag-bg-warning;
	}

	.popup-error {
		color: #fff;
		background-color: $tag-bg-error;
	}

	.popup-info {
		color: #fff;
		background-color: $tag-bg-info;
	}

	.success-text {
		color: $uni-color-success;
	}

	.warn-text {
		color: $uni-color-warning;
	}

	.error-text {
		color: $uni-color-error;
	}

	.info-text {
		color: $uni-color-info;
	}

	.dialog,
	.share {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
	}

	.dialog-box {
		padding: $uni-spacing-row-lg;
	}

	.dialog .button,
	.share .button {
		/* #ifndef APP-NVUE */
		width: 100%;
		/* #endif */
		margin: 0;
		margin-top: 10px;
		padding: 3px 0;
		flex: 1;
	}

	.dialog-text {
		font-size: 14px;
		color: #333;
	}
</style>

