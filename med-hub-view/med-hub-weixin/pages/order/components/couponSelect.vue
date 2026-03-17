<!-- 优惠券选择弹窗 -->
<template>
  <view class="coupon-select-modal" v-if="visible">
    <!-- 遮罩层 -->
    <view class="modal-mask" @click="close"></view>
    
    <!-- 弹窗内容 -->
    <view class="modal-content">
      <!-- 弹窗头部 -->
      <view class="modal-header">
        <text class="header-title">选择优惠券</text>
        <text class="header-close" @click="close">✕</text>
      </view>
      
      <!-- 优惠券列表 -->
      <scroll-view scroll-y class="coupon-list">
        <!-- 可用优惠券列表 -->
        <view 
          v-for="(coupon, index) in couponList" 
          :key="index"
          class="coupon-item"
          :class="{ 
            'selected': selectedCoupon && selectedCoupon.userCouponId === coupon.userCouponId
          }"
          @click="selectCoupon(coupon)"
        >
          <!-- 左侧优惠券金额 -->
          <view class="coupon-left">
            <view class="coupon-amount">
              <text v-if="coupon.type === 1" class="symbol">¥</text>
              <text class="value">{{ coupon.type === 2 ? coupon.discountValue * 10 : coupon.discountValue }}</text>
              <text v-if="coupon.type === 2" class="discount-text">折</text>
            </view>
            <view class="coupon-condition">
              <text v-if="coupon.minAmount > 0">满{{ coupon.minAmount }}元可用</text>
              <text v-else>无门槛</text>
            </view>
          </view>
          
          <!-- 右侧优惠券信息 -->
          <view class="coupon-right">
            <view class="coupon-name">{{ coupon.name }}</view>
            <view class="coupon-desc">{{ coupon.type === 1 ? '满减券' : '折扣券' }}</view>
            <view class="coupon-time">
              有效期至：{{ formatDate(coupon.expireTime) }}
            </view>
          </view>
          
          <!-- 选中图标 -->
          <view v-if="selectedCoupon && selectedCoupon.userCouponId === coupon.userCouponId" class="selected-icon">
            <text>✓</text>
          </view>
        </view>
        
        <!-- 空状态 -->
        <view v-if="couponList.length === 0 && !loading" class="empty-state">
          <text class="empty-text">暂无可用优惠券</text>
        </view>
        
        <!-- 加载中 -->
        <view v-if="loading" class="loading-text">加载中...</view>
      </scroll-view>
      
      <!-- 底部按钮 -->
      <view class="modal-footer">
        <button class="cancel-btn" @click="close">取消</button>
        <button 
          class="confirm-btn" 
          :disabled="!selectedCoupon"
          @click="confirm"
        >
          确定{{ selectedCoupon ? `（减${selectedCoupon.type === 2 ? selectedCoupon.discountValue * 10 + '折' : '¥' + selectedCoupon.discountValue}）` : '' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { getAvailableForOrder, validateCoupon } from '@/pages/api/api.js'
import dayjs from '@/utils/lib/dayjs.min.js'

export default {
  name: 'CouponSelect',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    orderAmount: {
      type: Number,
      default: 0
    },
    selectedCoupon: {
      type: Object,
      default: null
    }
  },
  
  data() {
    return {
      couponList: [],
      loading: false
    }
  },
  
  watch: {
    visible(val) {
      if (val) {
        this.loadCoupons()
      }
    }
  },
  
  methods: {
    // 关闭弹窗
    close() {
      this.$emit('update:visible', false)
    },
    
    // 加载可用优惠券
    async loadCoupons() {
      if (this.loading) return
      
      // 验证订单金额
      if (!this.orderAmount || this.orderAmount <= 0) {
        console.error('订单金额无效:', this.orderAmount)
        uni.showToast({
          title: '订单金额无效',
          icon: 'none'
        })
        return
      }
      
      console.log('加载优惠券，订单金额:', this.orderAmount)
      
      this.loading = true
      try {
        const res = await getAvailableForOrder(this.orderAmount)
        console.log('优惠券响应:', res)
        
        if (res.code === 1) {
          this.couponList = res.data || []
          console.log('优惠券列表:', this.couponList)
        } else {
          uni.showToast({
            title: res.msg || '加载失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载优惠券失败:', error)
        uni.showToast({
          title: error.msg || '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 选择优惠券
    selectCoupon(coupon) {
      // 检查是否满足使用门槛
      if (coupon.minAmount > 0 && this.orderAmount < coupon.minAmount) {
        uni.showToast({
          title: `满${coupon.minAmount}元可用`,
          icon: 'none'
        })
        return
      }
      
      // 如果已经选中，则取消选中
      if (this.selectedCoupon && this.selectedCoupon.userCouponId === coupon.userCouponId) {
        this.$emit('update:selectedCoupon', null)
      } else {
        this.$emit('update:selectedCoupon', coupon)
      }
    },
    
    // 确认选择
    confirm() {
      if (!this.selectedCoupon) {
        this.close()
        return
      }
      
      // 触发确认事件，传递选中的优惠券
      this.$emit('confirm', this.selectedCoupon)
      this.close()
    },
    
    // 格式化日期
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD')
    }
  }
}
</script>

<style lang="scss" scoped>
.coupon-select-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  
  .header-title {
    font-size: 34rpx;
    font-weight: 600;
    color: #333;
  }
  
  .header-close {
    font-size: 40rpx;
    color: #999;
    padding: 8rpx;
  }
}

.coupon-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 32rpx;
  max-height: 50vh;
}

.coupon-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  position: relative;
  
  &.selected {
    background-color: #fff8f0;
    margin: 0 -32rpx;
    padding: 24rpx 32rpx;
  }
  
  &.disabled {
    opacity: 0.6;
  }
}

.coupon-left {
  width: 160rpx;
  flex-shrink: 0;
  margin-right: 24rpx;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
  color: #ff6b35;
  
  .symbol {
    font-size: 28rpx;
    margin-right: 4rpx;
  }
  
  .value {
    font-size: 48rpx;
    font-weight: 700;
    line-height: 1;
  }
  
  .discount-text {
    font-size: 24rpx;
    margin-left: 4rpx;
  }
}

.coupon-condition {
  font-size: 22rpx;
  color: #ff6b35;
  margin-top: 8rpx;
}

.coupon-right {
  flex: 1;
  min-width: 0;
}

.coupon-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 8rpx;
}

.coupon-desc {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.coupon-time {
  font-size: 22rpx;
  color: #bbb;
}

.selected-icon {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 40rpx;
  height: 40rpx;
  background-color: #ff6b35;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
  
  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

.loading-text {
  text-align: center;
  padding: 32rpx;
  font-size: 28rpx;
  color: #999;
}

.modal-footer {
  display: flex;
  padding: 24rpx 32rpx 40rpx;
  border-top: 1rpx solid #f0f0f0;
  
  button {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 30rpx;
    border-radius: 40rpx;
    border: none;
  }
  
  .cancel-btn {
    background-color: #f5f5f5;
    color: #666;
    margin-right: 24rpx;
  }
  
  .confirm-btn {
    background: linear-gradient(90deg, #ff6b35, #ff8f6b);
    color: #fff;
    
    &[disabled] {
      background: #e0e0e0;
      color: #999;
    }
  }
}
</style>
