<!--药品信息-->
<template>
  <view class="order_list">
    <view class="word_text">
      <text class="word_style">{{ shopInfo.shopName }}</text>
    </view>
    <view class="order-type">
      <view class="type_item" v-for="(obj, index) in orderDataes" :key="index">
        <view class="dish_img">
          <image mode="aspectFill" :src="obj.image || obj.medicineImage" class="dish_img_url"></image>
        </view>
        <view class="dish_info">
          <view class="dish_name"> {{ obj.name || obj.medicineName }} </view>
          <view class="dish_dishFlavor" v-if="obj.medicineSpec">
            {{ obj.medicineSpec }}
          </view>
          <view class="dish_price">× {{ obj.number || 0 }}</view>
          <view class="dish_active">
            <text>￥</text> {{ (obj.amount || obj.medicinePrice || 0).toFixed(2) }}
          </view>
        </view>
      </view>
      <view class="iconUp">
        <view @click="handleToggleDisplay" v-if="orderListDataes.length > 3">
          <text>{{ !showDisplay ? "展开更多" : "点击收起" }}</text>
          <image class="icon_img" :class="showDisplay ? 'icon_imgDown' : ''" src="../../../static/toRight.png" mode="">
          </image>
        </view>
      </view>
      <view class="orderList">
        <view class="orderInfo">
          <text class="text">打包费</text>
          <text class="may">￥</text>{{ orderMedicineNumber }}
        </view>
        <view class="orderInfo">
          <text class="text">配送费</text>
          <text class="may">￥</text>{{ deliveryFee }}
        </view>
        <view class="totalMoney">
          合计<text class="text"><text>￥</text>{{ orderMedicinePrice.toFixed(2) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>
<script>
export default {
  props: {
    orderDataes: {
      type: Array,
      default: () => [],
    },
    showDisplay: {
      type: Boolean,
      default: false,
    },
    orderListDataes: {
      type: Array,
      default: () => [],
    },
    orderMedicineNumber: {
      type: Number,
      default: 0,
    },
    orderMedicinePrice: {
      type: Number,
      default: 0,
    },
    deliveryFee: {
      type: Number,
      default: 0,
    },
    shopInfo: {
      type: Object,
      default: () => ({ shopName: '' }),
    },
  },
  methods: {
    handleToggleDisplay() {
      this.$emit('update:showDisplay', !this.showDisplay)
    },
  },
};
</script>
<style lang="scss">
.word_text {
  padding-bottom: 16rpx;
  padding-top: 16rpx;
  margin: 0 20rpx;
  border-bottom: 1px solid #efefef;
}
.word_style {
  height: 44rpx;
  font-size: 28rpx;
  font-weight: 500;
  text-align: left;
  color: #333333;
  line-height: 44rpx;
  padding-left: 6rpx;
}
.order_list {
  margin-bottom: 20rpx;
}
.order-type {
  padding: 40rpx 0 10rpx 0;
}
.type_item {
  display: flex;
  margin-bottom: 40rpx;
}
.dish_img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.dish_img_url {
  width: 160rpx;
  height: 160rpx;
}
.dish_info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  padding-bottom: 20rpx;
  border-bottom: 1px solid #f5f5f5;
}
.dish_name {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
}
.dish_dishFlavor {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}
.dish_price {
  font-size: 28rpx;
  color: #818693;
  height: 40rpx;
  line-height: 40rpx;
  margin-top: 10rpx;
  display: flex;
  align-items: center;
}
.dish_active {
  position: absolute;
  right: 0;
  top: 0;
  display: flex;
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  align-items: center;
}
.iconUp {
  text-align: center;
  font-size: 24rpx;
  color: #666;
  padding: 8rpx 0;
}
.icon_img {
  width: 30rpx;
  height: 30rpx;
  vertical-align: middle;
}
.orderList {
  margin: 0 20rpx 20rpx;
}
.orderInfo {
  display: flex;
  font-size: 28rpx;
  padding: 10rpx 0 16rpx;
  font-weight: 600;
  align-items: center;
  color: #333;
}
.totalMoney {
  border-top: 1px solid #efefef;
  text-align: right;
  margin-top: 20rpx;
  padding-top: 20rpx;
  font-size: 24rpx;
}
</style>