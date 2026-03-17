<!--提交订单-->
<template>
  <view>
    <!-- 导航 -->
    <uni-nav-bar
      @clickLeft="goBack"
      left-icon="back"
      leftIcon="arrowleft"
      title="提交订单"
      statusBar="true"
      fixed="true"
      color="#1D1D1F"
      backgroundColor="#FFFFFF"
      class="ios-nav-bar"
    ></uni-nav-bar>
    <!-- end -->
    <view class="order_content" @touchstart="touchstart">
      <view class="order_content_box">
        <!-- 地址 -->
        <address-pop
          :address="address"
          :tagLabel="tagLabel"
          :addressLabel="addressLabel"
          :nickName="nickName"
          :phoneNumber="phoneNumber"
          :gender="gender"
          :arrivalTime="arrivalTime"
          :popleft="popleft"
          :weeks="weeks"
          :newDateData="newDateData"
          :selectValue="selectValue"
          @change="change"
          @goAddress="goAddress"
          @dateChange="dateChange"
          @timeClick="timeClick"
        ></address-pop>
        <!-- end -->
        <!-- 订单明细 -->
        <view class="order_list_cont">
          <!-- 药品详情 -->
          <medicine-detail
            :orderDataes="orderDataes"
            :showDisplay="showDisplay"
            :orderMedicineNumber="orderMedicineNumber"
            :orderListDataes="orderListDataes"
            :orderMedicinePrice="orderMedicinePrice"
            :deliveryFee="deliveryFee"
            :shopInfo="shopInfo"
            @update:showDisplay="showDisplay = $event"
          ></medicine-detail>
          <!-- end -->
          <!-- 优惠券 -->
          <view class="order_coupon" @click="openCouponSelect">
            <view class="coupon_label">优惠券</view>
            <view class="coupon_value">
              <text v-if="selectedCoupon" class="coupon_text">{{ selectedCoupon.couponName }}</text>
              <text v-else class="coupon_text">暂无优惠</text>
              <text v-if="availableCouponCount > 0" class="coupon_count">共{{ availableCouponCount }}张可用</text>
              <text class="arrow">›</text>
            </view>
          </view>
          
          <!-- 备注和发票 -->
          <view class="order_extra">
            <view class="extra_item" @click="goRemark">
              <view class="extra_label">备注</view>
              <view class="extra_value">
                <text v-if="!remark">填写药品需求、用药说明等</text>
                <text v-else>{{ remark }}</text>
                <text class="arrow">›</text>
              </view>
            </view>
            <view class="extra_item">
              <view class="extra_label">发票</view>
              <view class="extra_value invoice_text">
                <text>请联系商家提供</text>
              </view>
            </view>
          </view>
        </view>
        <!-- end -->
      </view>
      <!-- 底部购物车、去支付 -->
      <view class="footer_order_buttom order_form">
        <view class="order_number">
          <image
            src="../../static/btn_waiter_sel.png"
            class="order_number_icon"
            mode=""
          ></image>
          <view class="order_dish_num"> {{ orderMedicineNumber }} </view>
        </view>
        <view class="order_price">
          <text class="ico">￥ </text> {{ (finalPrice || 0).toFixed(2) }}
        </view>
        <view class="order_but">
          <view v-if="isHandlePy" class="order_but_rit">去支付</view>
          <view v-else class="order_but_rit" @click="payOrderHandle()">
            去支付
          </view>
        </view>
      </view>
      <!-- end -->
    </view>
    
    <!-- 优惠券选择弹窗 -->
    <coupon-select
      :visible="couponSelectVisible"
      :order-amount="orderMedicinePrice"
      :selected-coupon="selectedCoupon"
      @update:visible="couponSelectVisible = $event"
      @update:selectedCoupon="selectedCoupon = $event"
      @confirm="handleCouponConfirm"
    ></coupon-select>
  </view>
</template>
<script src="./index.js"></script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="./style.scss" lang="scss" scoped></style>