<template>
  <view class="activity-page">
    <!-- 导航栏 -->
    <uni-nav-bar 
      title="活动中心"
      statusBar="true"
      fixed="true"
      color="#1D1D1F"
      backgroundColor="#FFFFFF"
      border
      class="ios-nav-bar"
    />
    
    <view class="activity-content">
      <!-- 优惠券入口 -->
      <view class="activity-item" @click="goCouponActivity">
        <view class="activity-icon">
          <text class="coupon-icon-text">¥</text>
        </view>
        <view class="activity-info">
          <text class="activity-title">领取优惠券</text>
          <text class="activity-desc">海量优惠等你来</text>
        </view>
        <uni-icons type="arrowright" size="20" color="#ccc"></uni-icons>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {};
  },
  onLoad() {
    console.log('活动页面加载成功')
  },
  methods: {
    // 跳转到优惠券活动页面
    goCouponActivity() {
      console.log('点击跳转到优惠券页面')
      uni.navigateTo({
        url: '/pages/couponActivity/couponActivity',
        fail: (err) => {
          console.error('跳转失败:', err)
          uni.showToast({
            title: '跳转失败',
            icon: 'none'
          })
        }
      })
    }
  }
};
</script>

<style lang="scss" scoped>
.activity-page {
  min-height: 100vh;
  background-color: #F5F5F7;
  
  // iOS 风格导航栏样式
  .ios-nav-bar {
    ::v-deep .uni-navbar__content-view {
      font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
      font-weight: 600;
      font-size: 34rpx;
      letter-spacing: 0;
    }
    
    ::v-deep .uni-navbar__header-btn {
      font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "PingFang SC", "Helvetica Neue", Arial, sans-serif;
    }
  }
}

.activity-content {
  padding: 32rpx;
  
  .activity-item {
    display: flex;
    align-items: center;
    padding: 32rpx;
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
    
    .activity-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48rpx;
      height: 48rpx;
      margin-right: 24rpx;
      background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
      border-radius: 12rpx;
      
      .coupon-icon-text {
        font-size: 28rpx;
        font-weight: bold;
        color: #fff;
      }
    }
    
    .activity-info {
      flex: 1;
      
      .activity-title {
        display: block;
        font-size: 30rpx;
        font-weight: 600;
        color: #333;
        margin-bottom: 8rpx;
      }
      
      .activity-desc {
        display: block;
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}
</style>
