
import {
	getOrderDetail,
	repetitionOrder,
	delShoppingCart,
	reminderOrder,
	cancelOrder,
} from '../api/api.js'
import { mapState, mapMutations } from 'vuex'
import { baseUrl } from '../../utils/env'
import { call } from '@/utils/index.js'
import Status from './components/status.vue'//订单状态
import OrderDetail from "./components/orderDetail.vue" //药品详情
import DeliveryInfo from "./components/deliveryInfo.vue" //配送信息
import OrderInfo from "./components/orderInfo.vue" //订单信息

export default {
	data() {
		return {
			showDisplay: false,
			rocallTime: '',
			textTip: '',
			showConfirm: false,
			orderDetailsData: {},
			timeout: false,
			isOvertime: false,
			orderId: null,
			activeRadio: 0,
			time: null,
			isPayment: false,
			times: null,
			phone: ""
		}
	},

	computed: {
		orderListDataes: function () {
			return this.orderListData()
		},

		// // 处理订单详情列表
		orderDataes: function () {
			let testList = []
			if (this.showDisplay === false) {
				if (this.orderListDataes.length > 2) {
					for (var i = 0; i < 2; i++) {
						testList.push(this.orderListDataes[i])
					}
				} else {
					testList = this.orderListDataes
				}
				return testList
			} else {
				return this.orderListDataes
			}
		}
	},
	//   组件
	components: { Status, OrderDetail, DeliveryInfo, OrderInfo },
	onLoad(options) {
		this.orderId = options.orderId
		this.getBaseData(options.orderId)
	},
	onShow() {
		// 页面显示时刷新订单状态
		if (this.orderId) {
			this.getBaseData(this.orderId)
		}
	},

	methods: {
		...mapMutations(['setOrderData', 'initMedicineListMut']),
		...mapState(['shopInfo', 'orderListData', 'shopPhone', 'orderData']),
		// 获取订单详情
		getBaseData(id) {
			getOrderDetail(id).then(res => {
				if (res.code === 1) {
					this.orderDetailsData = res.data
					this.initMedicineListMut(this.orderDetailsData.orderDetailList)
					if (this.orderDetailsData.status === 1) {
						this.runTimeBack(this.orderDetailsData.orderTime)
					}
				}
			})
		},

		// 催单
		handleReminder(val) {
			reminderOrder(val.id).then(res => {
				if (res.code === 1) {
					this.showConfirm = true
					this.textTip = '您的催单信息已发出！'
					this.$refs.commonPopup.open()
					this.orderId = val.id
				}
			})
		},
		// 取消订单接口
		cancel(type, obj) {
			cancelOrder(obj.id).then(res => {
				if (res.code === 1) {
					this.isPayment = true
					this.showConfirm = true
					this.textTip = '您的订单已取消！'
					this.$refs.commonPopup.open()
					this.orderId = obj.id
				} else {
					uni.showToast({
						title: res.msg || '取消订单失败',
						icon: 'none'
					})
				}
			}).catch(error => {
				console.error('取消订单失败:', error)
				uni.showToast({
					title: '取消订单失败',
					icon: 'none'
				})
			})
		},
		// 取消订单
		handleCancel(val) {
			if (val.obj.status === 1 || val.obj.status === 2) {
				this.cancel(val.type, val.obj)
			} else {
				this.showConfirm = false
				this.$refs.commonPopup.open()
				this.textTip = '请联系药房进行取消！'
			}
		},
		// 处理状态
		statusWord(status) {
			if (this.timeout && status === 1 || this.orderDetailsData.status === 6) {
				return '订单已取消'
			}
			switch (status) {
				case 2:
					return '等待药房接单'
				case 3:
					return '药房已接单'
				case 4:
					return '订单派送中'
				case 5:
					return '订单已完成'
			}
		},
		// 订单倒计时
		runTimeBack(time) {
			const end = Date.parse(time.replace(/-/g, "/"))

			const now = Date.parse(new Date())
			const m15 = 15 * 60 * 1000
			const msec = m15 - (now - end)
			if (msec < 0) {
				this.timeout = true
				clearTimeout(this.times)
				this.cancel('center', this.orderDetailsData) //超时的时候取消订单
			} else {
				let min = parseInt(msec / 1000 / 60 % 60)
				let sec = parseInt(msec / 1000 % 60)
				if (min < 10) {
					min = '0' + min
				} else {
					min = min
				}
				if (sec < 10) {
					sec = '0' + sec
				} else {
					sec = sec
				}
				this.rocallTime = min + ':' + sec
				let that = this
				if (min >= 0 && sec >= 0) {
					if (min === 0 && sec === 0) {
						this.timeout = true
						clearTimeout(this.times)
						this.cancel('center', this.orderDetailsData) //超时的时候取消订单
						return
					}
					this.times = setTimeout(function () {
						that.runTimeBack(time)
					}, 1000)
				}
			}
		},

		// 重新拼装image
		getNewImage(image) {
			return `${baseUrl}/common/download?name=${image}`
		},
		// 返回上一级
		goBack() {
			// 直接跳转到个人中心页面，关闭其他所有页面
			uni.reLaunch({
				url: '/pages/my/my'
			})
		},
		openPopuos(type) {
			this.$refs.commonPopup.open()
		},
		// 联系药房进行退款弹层
		handleRefund(type) {
			this.showConfirm = false
			this.openPopuos(type)
			this.textTip = '请联系药房进行退款！'
		},
		// 拨打电话弹层
		handlePhone(type, phone) {
			// 暂时关闭打电话
			this.$refs.phone.open(type)

			this.phone = phone;
		},
		// 关闭弹层
		closePopup(type) {
			this.$refs.phone.close(type)
		},
		// closePopupInfo
		closePopupInfo(type) {
			this.$refs.commonPopup.close()
			this.getBaseData(this.orderId)
			// 如果订单已取消，返回上一页
			if (this.isPayment) {
				setTimeout(() => {
					uni.navigateBack({
						delta: 1
					})
				}, 500)
			}
		},
		// 立即支付
		handlePay(id) {
			const obj = {
				orderNumber: this.orderDetailsData.number,
				orderAmount: this.orderDetailsData.amount,
				orderTime: this.orderDetailsData.orderTime
			}
			this.setOrderData(obj)
			uni.redirectTo({
				url: '/pages/pay/index?orderId=' + id
			})
		},
		// 再来一单
		async oneMoreOrder(id) {
			try {
				// 先清空购物车
				await delShoppingCart()
				// 重新下单
				repetitionOrder(id).then(res => {
					if (res.code === 1) {
						// 跳转到购药页面
						uni.switchTab({
							url: '/pages/index/index'
						})
					} else {
						uni.showToast({
							title: res.msg || '操作失败',
							icon: 'none'
						})
					}
				})
			} catch (error) {
				console.error('再来一单失败:', error)
				uni.showToast({
					title: '操作失败',
					icon: 'none'
				})
			}
		},
		call() {
			call(this.phone)
		},
	}
}
