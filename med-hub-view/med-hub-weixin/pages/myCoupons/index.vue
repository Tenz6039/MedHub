<template>
	<view class="coupon-activity-page">
		<!-- 导航栏 -->
		<uni-nav-bar 
			@clickLeft="goBack" 
			left-icon="back" 
			title="我的优惠券"
			statusBar="true"
			fixed="true"
			color="#1D1D1F"
			backgroundColor="#FFFFFF"
			border
			class="ios-nav-bar"
		/>
		
		<!-- 标签页切换 -->
		<view class="tabs">
			<view 
				v-for="(tab, index) in tabs" 
				:key="index"
				class="tab-item"
				:class="{ active: currentTab === index }"
				@click="switchTab(index)"
			>
				<text class="tab-text">{{ tab.name }}</text>
			</view>
		</view>
		
		<!-- 优惠券列表 -->
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
					</view>
					
					<view class="coupon-info-row">
						<text class="info-tag">{{ coupon.typeDescription }}</text>
					</view>
					
					<view class="coupon-validity">
						<text>有效期至：{{ formatDate(coupon.expireTime) }}</text>
					</view>
					
					<!-- 状态按钮 -->
					<view class="coupon-status">
						<text 
							class="status-btn" 
							:class="{
								'unused': coupon.status === 0,
								'used': coupon.status === 1,
								'expired': coupon.status === 2
							}"
						>
							<text v-if="coupon.status === 0">未使用</text>
							<text v-else-if="coupon.status === 1">已使用</text>
							<text v-else-if="coupon.status === 2">已过期</text>
						</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view v-if="couponList.length === 0 && !loading" class="empty-state">
				<text class="empty-text">{{ getEmptyText() }}</text>
			</view>
			
			<!-- 加载更多 -->
			<view v-if="loading" class="loading-text">加载中...</view>
			<view v-if="!hasMore && couponList.length > 0" class="no-more-text">没有更多了</view>
		</scroll-view>
	</view>
</template>

<script>
import { getMyCoupons } from '@/pages/api/api.js'
import dayjs from '@/utils/lib/dayjs.min.js'

export default {
	data() {
		return {
			tabs: [
				{ name: '未使用', status: 0 },
				{ name: '已使用', status: 1 },
				{ name: '已过期', status: 2 }
			],
			currentTab: 0,
			couponList: [],
			loading: false,
			page: 1,
			pageSize: 10,
			total: 0,
			hasMore: true
		}
	},
	
	onLoad() {
		this.loadCoupons()
	},
	
	onPullDownRefresh() {
		this.refresh()
	},
	
	methods: {
		goBack() {
			uni.navigateBack()
		},
		
		switchTab(index) {
			if (this.currentTab === index) return
			this.currentTab = index
			this.refresh()
		},
		
		refresh() {
			this.page = 1
			this.hasMore = true
			this.couponList = []
			this.loadCoupons()
		},
		
		loadMore() {
			if (!this.hasMore || this.loading) return
			this.page++
			this.loadCoupons()
		},
		
		async loadCoupons() {
			if (this.loading) return
			
			this.loading = true
			try {
				const status = this.tabs[this.currentTab].status
				const params = {
					page: this.page,
					pageSize: this.pageSize,
					status: status
				}
				
				const res = await getMyCoupons(params)
				
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
		
		getStatusClass(status) {
			const classes = {
				0: 'unused',
				1: 'used',
				2: 'expired'
			}
			return classes[status] || ''
		},
		
		getStatusText(status) {
			const texts = {
				0: '未使用',
				1: '已使用',
				2: '已过期'
			}
			return texts[status] || '未知'
		},
		
		getEmptyText() {
			const texts = {
				0: '暂无未使用优惠券',
				1: '暂无已使用优惠券',
				2: '暂无已过期优惠券'
			}
			return texts[this.tabs[this.currentTab].status] || '暂无优惠券'
		},
		
		formatDate(date) {
			if (!date) return ''
			return dayjs(date).format('YYYY-MM-DD')
		}
	}
}
</script>

<style lang="scss" scoped>
.coupon-activity-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
	
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

.tabs {
	flex-shrink: 0;
	display: flex;
	background-color: #fff;
	padding: 0 32rpx;
	border-bottom: 1rpx solid #f0f0f0;
	
	.tab-item {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		height: 88rpx;
		position: relative;
		
		.tab-text {
			font-size: 30rpx;
			color: #666;
			font-weight: 500;
		}
		
		&.active {
			.tab-text {
				color: #ff6b35;
				font-weight: 600;
			}
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 48rpx;
				height: 6rpx;
				background: linear-gradient(90deg, #ff6b35, #ff8f6b);
				border-radius: 3rpx;
			}
		}
	}
}

.coupon-list {
	flex: 1;
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
		
		&.unused {
			background: #ff6b6b;
			color: #fff;
		}
		
		&.used {
			background: #f5f5f5;
			color: #999;
		}
		
		&.expired {
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
