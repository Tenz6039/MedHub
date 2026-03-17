<template>
  <view>
    <!-- 导航 -->
    <uni-nav-bar 
      @click-left="goBack" 
      left-icon="back" 
      title="订单详情" 
      statusBar="true" 
      fixed="true"
      color="#1D1D1F" 
      backgroundColor="#FFFFFF" 
      border 
      class="ios-nav-bar">
    </uni-nav-bar>
    <!-- end -->
    <view class="order_content orderDetail">
      <view class="order_content_box">
        <!-- 支付状态 -->
        <status ref="status" :timeout="timeout" :orderDetailsData="orderDetailsData" :rocallTime="rocallTime"
          @statusWord="statusWord" @paymentTime="paymentTime" @handlePay="handlePay" @handleReminder="handleReminder"
          @handleRefund="handleRefund" @handleCancel="handleCancel" @oneMoreOrder="oneMoreOrder"></status>
        <!-- end -->
        <!-- 订单详情 -->
        <order-detail :orderDataes="orderDataes" :orderDetailsData="orderDetailsData"
          :showDisplay="showDisplay" @update:showDisplay="showDisplay = $event"></order-detail>
        <!-- end -->
        <!-- 联系药房 -->
        <view class="box contactMerchant">
          <button @click="handlePhone('bottom', orderDetailsData.shopTelephone)">
            <view class="phoneIcon"></view>
            联系药房
          </button>
        </view>
        <!-- end -->
        <!-- 配送信息 -->
        <delivery-info :orderDetailsData="orderDetailsData"></delivery-info>
        <!-- end -->
        <!-- 订单信息 -->
        <order-info :orderDetailsData="orderDetailsData"></order-info>
        <!-- end -->
      </view>
      <!-- 联系药房弹层 -->
      <uni-popup ref="commonPopup" class="comPopupBox">
        <view class="popup-content">
          <view class="text">{{ textTip }}</view>
          <view class="btn" v-if="showConfirm">
            <view @click="closePopupInfo">确认</view>
          </view>
          <view class="btn" v-else>
            <view @click="closePopupInfo">先等等</view>
            <view @click="handlePhone('bottom')">拨打电话</view>
          </view>
        </view>
      </uni-popup>
      <!-- 拨打电话弹层 -->
      <view class="container phoneCon">
        <uni-popup ref="phone" @change="change" class="popupBox">
          <view class="popup-content">
            <view>{{ phone }}</view>
            <view @click="call">呼叫</view>
            <view @click="closePopup" class="closePopup">取消</view>
          </view>
        </uni-popup>
      </view>
      <!-- end -->
    </view>
  </view>
</template>
<script src="../../utils/common.js"></script>
<script src="./index.js"></script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="../order/style.scss" lang="scss"></style>
<style lang="scss" scoped>
.order_content.orderDetail {
  .order_content_box {
    padding-top: 200rpx;
    min-height: 100vh;
  }
}
</style>
