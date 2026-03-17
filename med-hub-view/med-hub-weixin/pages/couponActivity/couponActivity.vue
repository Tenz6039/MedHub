<template>
	<view class="coupon-activity-page">
		<!-- 导航栏 -->
		<uni-nav-bar 
			@clickLeft="goBack" 
			left-icon="back" 
			title="领取优惠券"
			statusBar="true"
			fixed="true"
			color="#1D1D1F"
			backgroundColor="#FFFFFF"
			border
			class="ios-nav-bar"
		/>
		
		<!-- 优惠券列表 -->
		<view class="coupon-list-wrapper">
			<scroll-view 
				scroll-y 
				class="coupon-list"
				@scrolltolower="loadMore"
			>
			<view 
				v-for="(coupon, index) in couponList" 
				:key="index"
				class="coupon-item"
			>
				<!-- 左侧优惠券金额区域 -->
				<view class="coupon-left">
					<view class="coupon-amount-wrapper">
						<text v-if="coupon.type === 1" class="currency">¥</text>
						<text class="amount">{{ coupon.type === 2 ? coupon.discountValue * 10 : coupon.discountValue }}</text>
						<text v-if="coupon.type === 2" class="unit">折</text>
					</view>
					<view class="coupon-condition">
						<text v-if="coupon.minAmount > 0">满{{ coupon.minAmount }}元可用</text>
						<text v-else>无门槛</text>
					</view>
				</view>
				
				<!-- 右侧优惠券信息区域 -->
				<view class="coupon-right">
					<view class="coupon-header">
						<text class="coupon-name">{{ coupon.name }}</text>
						<text class="coupon-tag">{{ coupon.remainCount === -1 ? '不限' : '剩余' + coupon.remainCount }}</text>
					</view>
					
					<view class="coupon-info-row">
						<text class="info-tag">限领{{ coupon.userLimit === -1 ? '不限' : coupon.userLimit + '张' }}</text>
					</view>
					
					<view class="coupon-validity">
						<text>有效期至：{{ formatDate(coupon.endTime) }}</text>
					</view>
					
					<!-- 领取状态按钮 -->
					<view class="coupon-status">
						<text 
							class="status-btn" 
							:class="{ 
								'obtained': coupon.obtained,
								'disabled': coupon.remainCount === 0
							}"
							@click="handleObtain(coupon.id)"
						>
							<text v-if="coupon.remainCount === 0">已领完</text>
							<text v-else-if="coupon.obtained">已领取</text>
							<text v-else>立即领取</text>
						</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
      <view v-if="couponList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无可领取优惠券</text>
      </view>
			
			<!-- 加载更多 -->
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!hasMore && couponList.length > 0" class="no-more-text">没有更多了</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
import { getAvailableCoupons, obtainCoupon, getMyCoupons } from '@/pages/api/api.js'
import dayjs from '@/utils/lib/dayjs.min.js'

export default {
	data() {
		return {
			couponList: [],
			loading: false,
			page: 1,
			pageSize: 10,
			total: 0,
			hasMore: true,
			obtainedCouponIds: [] // 已领取的优惠券 ID 列表
		}
	},
	
	onLoad() {
		// 加载优惠券列表
		this.loadCoupons()
	},
	
	onPullDownRefresh() {
		this.refresh()
	},
	
	onShow() {
		// 页面显示时刷新优惠券列表
		this.refresh()
	},
	
	methods: {
		// 返回上一页
		goBack() {
			uni.navigateBack()
		},
		
		// 加载已领取的优惠券列表（包括未使用和已使用）
		async loadObtainedCoupons() {
			try {
				// 查询未使用的优惠券
				const resUnused = await getMyCoupons({
					page: 1,
					pageSize: 100,
					status: 0
				})
				
				// 查询已使用的优惠券
				const resUsed = await getMyCoupons({
					page: 1,
					pageSize: 100,
					status: 1
				})
				
				// 合并已领取的优惠券 ID（包括未使用和已使用）
				const unusedIds = (resUnused.data.records || []).map(item => item.couponId)
				const usedIds = (resUsed.data.records || []).map(item => item.couponId)
				this.obtainedCouponIds = [...unusedIds, ...usedIds]
				
				// 更新优惠券列表的 obtained 状态
				this.updateCouponObtainedStatus()
				
			} catch (error) {
				console.error('加载已领取优惠券失败:', error)
			}
		},
		
		// 更新优惠券列表的 obtained 状态
		updateCouponObtainedStatus() {
			this.couponList.forEach(coupon => {
				if (this.obtainedCouponIds.includes(coupon.id)) {
					coupon.obtained = true
				}
			})
		},
		
		// 加载优惠券列表
		async loadCoupons() {
			if (this.loading) return
			
			this.loading = true
			try {
				const params = {
					page: this.page,
					pageSize: this.pageSize
				}
				
				const res = await getAvailableCoupons(params)
				
				if (!res || res.code !== 1) {
					this.couponList = []
					this.loading = false
					uni.showToast({
						title: res.msg || '加载失败',
						icon: 'none'
					})
					return
				}
				
				if (!res.data || !res.data.records) {
					this.couponList = []
					this.loading = false
					return
				}
				
				const { records, total } = res.data
				
				if (this.page === 1) {
					this.couponList = records || []
				} else {
					this.couponList = [...this.couponList, ...(records || [])]
				}
				
				this.total = total || 0
				this.hasMore = this.couponList.length < this.total
				
				// 加载完可领取的优惠券后，再加载已领取的优惠券列表并更新状态
				await this.loadObtainedCoupons()
				
				if (this.couponList.length === 0) {
					uni.showToast({
						title: '暂无可领取优惠券',
						icon: 'none'
					})
				}
				
			} catch (error) {
				uni.showToast({
					title: error.msg || '加载失败',
					icon: 'none',
					duration: 3000
				})
			} finally {
				this.loading = false
				uni.stopPullDownRefresh()
			}
		},
		
		// 加载更多
		loadMore() {
			if (!this.hasMore || this.loading) return
			this.page++
			this.loadCoupons()
		},
		
		// 刷新
		refresh() {
			this.page = 1
			this.hasMore = true
			this.loadCoupons()
		},
		
		// 领取优惠券
		async handleObtain(couponId) {
			// 检查是否已领取
			const coupon = this.couponList.find(c => c.id === couponId)
			if (!coupon) {
				uni.showToast({
					title: '优惠券不存在',
					icon: 'none'
				})
				return
			}
			
			// 已领取或已领完，不可点击
			if (coupon.obtained || coupon.remainCount === 0) {
				return
			}
			
			// 防止重复点击
			if (this.loading) return
			
			this.loading = true
			try {
				const res = await obtainCoupon(couponId)
				
				uni.showToast({
					title: '领取成功',
					icon: 'success'
				})
				
				// 更新该优惠券的状态
				coupon.obtained = true
				
				// 添加到已领取列表
				if (!this.obtainedCouponIds.includes(couponId)) {
					this.obtainedCouponIds.push(couponId)
				}
				
				// 减少剩余数量
				if (coupon.remainCount > 0) {
					coupon.remainCount--
				}
				
			} catch (error) {
				uni.showToast({
					title: error.msg || '领取失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 格式化日期
		formatDate(date) {
			return dayjs(date).format('YYYY-MM-DD')
		}
	}
}
</script>

<style lang="scss" scoped>
.coupon-activity-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	
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

.coupon-list-wrapper {
	height: 100vh;
}

.coupon-list {
	height: 100%;
	padding: 20rpx;
}

.coupon-item {
	position: relative;
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	display: flex;
	overflow: hidden;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
}

.coupon-left {
	position: relative;
	width: 240rpx;
	background: linear-gradient(135deg, #5ac8fa 0%, #7dd3fc 100%);
	padding: 40rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.coupon-amount-wrapper {
	display: flex;
	align-items: baseline;
	color: #fff;
	
	.currency {
		font-size: 32rpx;
		margin-right: 4rpx;
	}
	
	.amount {
		font-size: 72rpx;
		font-weight: bold;
		line-height: 1;
	}
	
	.unit {
		font-size: 28rpx;
		margin-left: 8rpx;
	}
}

.coupon-condition {
	margin-top: 20rpx;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.95);
	background: rgba(255, 255, 255, 0.25);
	padding: 8rpx 24rpx;
	border-radius: 20rpx;
}

.coupon-right {
	flex: 1;
	padding: 32rpx 24rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.coupon-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
	
	.coupon-name {
		font-size: 32rpx;
		font-weight: 600;
		color: #333;
	}
	
	.coupon-tag {
		font-size: 22rpx;
		color: #999;
		background: #f5f5f5;
		padding: 6rpx 16rpx;
		border-radius: 20rpx;
	}
}

.coupon-info-row {
	margin-bottom: 16rpx;
	
	.info-tag {
		font-size: 22rpx;
		color: #999;
		background: #f5f5f5;
		padding: 6rpx 16rpx;
		border-radius: 20rpx;
	}
}

.coupon-validity {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 20rpx;
}

.coupon-status {
	display: flex;
	justify-content: flex-end;
	
	.status-btn {
		font-size: 26rpx;
		padding: 12rpx 40rpx;
		border-radius: 24rpx;
		background: #ff6b6b;
		color: #fff;
		
		// 已领取状态 - 灰色不可点击
		&.obtained {
			background: #f5f5f5;
			color: #999;
		}
		
		// 已领完状态
		&.disabled {
			background: #f5f5f5;
			color: #999;
		}
	}
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 200rpx;
	
	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
}

.loading-text,
.no-more-text {
	text-align: center;
	font-size: 24rpx;
	color: #999;
	padding: 20rpx 0;
}
</style>
