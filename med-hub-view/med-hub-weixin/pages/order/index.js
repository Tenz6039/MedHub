import {
	// 提交订单
	submitOrderSubmit,
	// 查询默认地址
	getAddressBookDefault,
	queryAddressBookList,
	// 优惠券相关
	getAvailableForOrder,
	calculateOrderAmount,
	applyCoupon,
	cancelCouponApply
} from '../api/api.js'
import {
	mapState,
	mapMutations,
} from 'vuex'
import {
	baseUrl
} from '../../utils/env'
import {
	getLableVal,
	dateFormat,
	presentFormat,
	getWeekDate

} from '../../utils/index.js'
import Pikers from '@/components/uni-piker/index.vue'//包装信息
import AddressPop from "./components/address.vue" //地址
import MedicineDetail from "./components/medicineDetail.vue" //药品详情
import MedicineInfo from "./components/medicineInfo.vue" //药品信息
import CouponSelect from "./components/couponSelect.vue" //优惠券选择
import dayjs from "@/utils/lib/dayjs.min.js";
export default {
	data() {
		return {
			platform: 'ios',
			orderMedicinePrice: 0,
			openPayType: false,
			psersonUrl: '../../static/btn_waiter_sel.png',
			nickName: '',//名字
			gender: 0,
			phoneNumber: '',//电话
			address: '',//地址
			remark: '',//备注
			arrivalTime: '',// 用户选择的送达时间
			orderTime: '',// 服务端返回的送达时间
			addressBookId: '',
			addressLabel: '',
			tagLabel: '',
			// 加入购物车数量
			orderMedicineNumber: 0,
			showDisplay: false,//是否显示更多收起
			type: 'center',
			expirationTime: '',
			// rocallTime:'',
			packageData: '无需包装',
			package: '',
			packAmount: 0,
			value: [0, 0],
			timeValue: [0, 0],
			indicatorStyle: `height: 44px;color:#333`,
			tabIndex: 0,
			scrollinto: 'tab0',
			scrollH: 0,
			popleft: ['今天', '明天'],// 时间选中的左侧数据（今天、明天）
			visible: true,
			baseData: [
				'无需包装', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10'
			],
			activeRadio: '无需包装', //存的是选中的value值
			radioGroup: ['依据药量提供', '无需包装'],
			popright: ['立即派送', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30', '12:00', '12:30', '13:00',
				'13:30', '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00', '17:30', '18:00', '18:30',
				'19:00', '19:30', '20:00', '20:30', '21:00', '21:30', '22:00', '22:30', '23:00'
			],
			newDateData: [],// 时间段
			// styleType: 'button',
			textTip: '',
			showConfirm: false,
			phoneData: '15200000001',
			toDate: null,
			tomorrowStart: null,
			newDate: null,
			selectValue: 0,
			selectDateValue: 0,
			timeout: false,
			isTomorrow: false,
			status: 0,
			num: 0,
			weeks: [],
			scrollTop: 0,
			addressList: [],
			isHandlePy: false,
			// 优惠券相关
			couponSelectVisible: false, // 优惠券选择弹窗是否显示
			selectedCoupon: null, // 已选中的优惠券
			availableCouponCount: 0, // 可用优惠券数量
			finalPrice: 0, // 最终价格（使用优惠券后）
			discountAmount: 0 // 优惠金额
		}
	},
	computed: {
		// 从 Vuex 映射状态
		...mapState(['orderListData', 'remarkData', 'addressData', 'storeInfo', 'shopInfo', 'deliveryFee']),
		// 药品数据
		orderListDataes: function () {
			return this.orderListData
		},
		// 药品数据
		orderDataes: function () {
			let testList = []
			if (this.showDisplay === false) {
				if (this.orderListDataes.length > 3) {
					for (var i = 0; i < 3; i++) {
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
	created() {
		let time = new Date()
		this.toDate = new Date(time.toLocaleDateString()).getTime()
		this.tomorrowStart = this.toDate + 3600 * 24 * 1000
		this.newDate = time.getHours() * 3600 + time.getMinutes() * 60

		const weekDay = [this.toDate, this.tomorrowStart]

		weekDay.forEach((date) => {
			this.weeks.push(getWeekDate(date))

		})

		this.getAddressList()
	},
	mounted() {
		this.countdown()
	},
	components: {
		Pikers,
		// Popup,
		AddressPop,
		MedicineDetail,
		MedicineInfo,
		CouponSelect
	},
	async onLoad(options) {
		this.initPlatform()
		this.psersonUrl = this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.avatarUrl
		this.nickName = this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.nickName
		this.gender = this.$store.state.baseUserInfo && this.$store.state.baseUserInfo.gender
		this.remark = this.remarkData
		this.init()
		// 存在 options 说明换地址了
		if (this.addressData && this.addressData.detail) {
			this.addressBookId = ''
			const newAddress = this.addressData
			this.address = newAddress.provinceName + newAddress.cityName + newAddress.districtName + newAddress.detail
			this.phoneNumber = newAddress.phone
			this.nickName = newAddress.consignee
			this.gender = newAddress.sex

			this.addressBookId = newAddress.id
			this.addressLabel = getLableVal(newAddress.label)
		} else {
			// 默认地址查询
			await this.getAddressBookDefault()
		}

		await this.getEstimatedDeliveryTime();
		this.getDateDate()
		this.setArrivalTime(this.arrivalTime)
		this.setGender(this.gender)
	},
	onReady() {
		uni.getSystemInfo({
			success: (res) => {
				this.scrollH = res.windowHeight - uni.upx2px(100)
			}
		})
	},
	methods: {
		...mapMutations(['setAddressBackUrl', 'setOrderData', 'setArrivalTime', 'setRemark', 'setGender']),
		init() {
			this.computOrderInfo()
		},
		initPlatform() {
			const res = uni.getSystemInfoSync()
			this.platform = res.platform
		},
		// 获取用户送餐期望时间
		async getEstimatedDeliveryTime() {
			// 使用当前时间作为默认送达时间
			const now = dayjs();
			this.orderTime = now.format('YYYY-MM-DD HH:mm:ss');
			this.arrivalTime = now.format('HH:mm');
		},
		// 根据系统派送时间 格式化时间  [16:00,16:30]
		getDateDate() {
			let currentDayjs = dayjs(this.orderTime);
			const list = ['立即派送']
			if (!(currentDayjs.hour() >= 22 && currentDayjs.minute() > 30)) {
				if (currentDayjs.minute() > 30) {
					currentDayjs = currentDayjs.add(1, 'hour').set('minute', 0)
				} else {
					currentDayjs = currentDayjs.set('minute', 30)
				}
				while (true) {
					if (currentDayjs.hour() === 23 && currentDayjs.minute() === 30) {
						break
					}
					const start = `${currentDayjs.format("HH")}:${currentDayjs.format('mm')}`;
					list.push(`${start}`)
					currentDayjs = currentDayjs.add(30, 'minute')
				}
			}
			this.newDateData = list
		},
		// 获取地址
		getAddressList() {
			this.testValue = false
			queryAddressBookList().then(res => {
				if (res.code === 1) {
					this.testValue = true
					this.addressList = res.data
				}
			})
		},
		// 默认地址查询
		getAddressBookDefault() {
			return getAddressBookDefault().then(res => {
				if (res.code === 1) {
					this.addressBookId = ''
					this.address = res.data.provinceName + res.data.cityName + res.data.districtName + res.data
						.detail
					this.phoneNumber = res.data.phone
					this.nickName = res.data.consignee
					this.gender = res.data.sex
					this.addressBookId = res.data.id
					this.addressLabel = getLableVal(res.data.label)
					this.tagLabel = res.data.label
				}
			})
		},
		// 去地址页面
		goAddress() {
			this.setAddressBackUrl('/pages/order/index')
			if (this.addressList.length === 0) {
				uni.navigateTo({
					url: '/pages/addOrEditAddress/addOrEditAddress'
				})
			} else {
				uni.navigateTo({
					url: '/pages/address/address'
				})
			}

		},
		// // 重新拼装image
		getNewImage(image) {
			return `${baseUrl}/common/download?name=${image}`
		},
		// 订单里和总订单价格计算
		computOrderInfo() {
			let oriData = this.orderListDataes || []
			this.orderMedicineNumber = 0
			this.orderMedicinePrice = 0
			oriData.forEach((n) => {
				// 确保 number 和 amount 是数字
				const number = Number(n.number) || 0
				const amount = Number(n.amount) || 0
				this.orderMedicinePrice += number * amount
				this.orderMedicineNumber += number
			})
			// 固定打包费 1 元
			const packageFee = 1
			// 确保配送费是数字
			const deliveryFee = Number(this.deliveryFee) || 0
			// 合计 = 药品总价 + 打包费 + 配送费
			this.orderMedicinePrice = this.orderMedicinePrice + packageFee + deliveryFee
			// 打包费固定为 1 元，与商品数量无关
			this.orderMedicineNumber = 1
			
			// 初始化最终价格
			this.finalPrice = this.orderMedicinePrice
			
			// 加载可用优惠券
			this.loadAvailableCoupons()
		},
		// 返回上一级
		goBack() {
			uni.navigateBack({ delta: 1 })
		},
		closeMask() {
			this.openPayType = false
		},
		// 支付下单
		payOrderHandle() {
			this.isHandlePy = true

			if (!this.address) {
				uni.showToast({
					title: '请选择收货地址',
					icon: 'none',
				})
				return false
			}
			const params = {
				payMethod: 1,
				addressBookId: this.addressBookId,
				estimatedDeliveryTime: this.arrivalTime === '立即派送' ? presentFormat() : dateFormat(this.isTomorrow,
					this.arrivalTime),
				deliveryStatus: this.arrivalTime === '立即派送' ? 1 : 0,
				remark: this.remark,
				packageStatus: this.status,
				packageNumber: this.num,
				packAmount: this.orderMedicineNumber,
				amount: this.selectedCoupon ? this.finalPrice : this.orderMedicinePrice,
				shopId: this.shopInfo.shopId,
				deliveryFee: this.deliveryFee,
				couponId: this.selectedCoupon ? this.selectedCoupon.userCouponId : null,
				couponDiscount: this.selectedCoupon ? this.discountAmount : null
			}

			submitOrderSubmit(params).then(res => {
				if (res.code === 1) {
					this.isHandlePy = false
					this.setOrderData(res.data)
					this.setRemark('')

					uni.navigateTo({
						url: '/pages/pay/index?orderId=' + res.data.id
					})
				} else {
					uni.showToast({
						title: res.msg || '操作失败',
						icon: 'none',
					})
				}
			})
		},
		// 拨打电话
		call() {
			uni.makePhoneCall({
				phoneNumber: '114' //仅为示例
			})
		},
		// // 联系药房进行取消弹层
		handleContact(type) {
			this.showConfirm = false
			this.openPopuos(type)
			this.textTip = '请联系药房进行取消！'
		},
		// 联系药房进行退款弹层
		handleRefund(type) {
			this.showConfirm = false
			this.openPopuos(type)
			this.textTip = '请联系药房进行退款！'
		},
		// 进入备注页
		goRemark() {
			this.setAddressBackUrl('/pages/order/index')
			uni.navigateTo({
				url: '/pages/remark/index'
			})
		},
		// 打开参数数量弹层
		openPopuos(type) {
			// open 方法传入参数 等同在 uni-popup 组件上绑定 type属性
			this.$refs.popup.open(type)
		},
		// 关闭包装弹层
		closePopup(type) {
			this.$refs.popup.close(type)
		},
		change(e) {
		},
		// 确定本单包装
		handlePiker() {
			if (this.package !== '') {
				this.num = Number(this.package)
				this.status = 0
				if (this.package === '无需包装') {
					this.num = 0
					this.status = 0
				}
				if (this.package === '依据药量提供') {
					this.num = this.orderMedicineNumber
					this.status = 1
				}

				if (this.package !== '依据药量提供' && this.package !== '无需包装') {
					this.packageData = this.package + '份'

				} else {
					this.packageData = this.package
				}
			} else {
				//是默认值，在点击的时候抛出去
				let cont = this.baseData[this.$refs.dishinfo.$refs.piker.defaultValue[0]]
				this.packageData = cont
				if (this.activeRadio === '依据药量提供') {
					this.num = this.orderMedicineNumber
					this.status = 1
				} else {
					this.num = 0
					this.status = 0
				}
			}
		},
		// 确定本单包装
		changeCont(val) {
			this.package = val
		},
		// 包装数量的后续订单包装设置
		handleRadio(e) {
			this.activeRadio = e.detail.value
		},
		countdown() {
			const end = Date.parse(new Date())
		},
		// 星期几选择
		dateChange(index) {
			if (index === 1) {
				this.newDateData = this.popright.slice(1)
				this.isTomorrow = true
			} else {
				this.isTomorrow = false
				this.newDateData = []
				this.getDateDate()
			}
			// 点击的还是当前数据的时候直接return
			if (this.tabIndex == index) {
				return
			}
			this.tabIndex = index
		},
		// 选中时间段
		timeClick: function (val) {
			this.selectValue = val.i
			this.setTime(val.val)
		},
		// 设置时间
		setTime(val) {
			if (val === '立即派送') {
				this.arrivalTime = dayjs(this.orderTime).format('HH:mm')
			} else {
				this.arrivalTime = val
			}

			this.setArrivalTime(this.arrivalTime)

		},
		touchstart(e) {
			if (e.changedTouches[0].clientY > 400) {
			}
		},
		
		// ==================== 优惠券相关方法 ====================
		
		// 打开优惠券选择弹窗
		openCouponSelect() {
			this.couponSelectVisible = true
		},
		
		// 加载可用优惠券列表
		async loadAvailableCoupons() {
			try {
				const res = await getAvailableForOrder(this.orderMedicinePrice)
				if (res.code === 1) {
					this.availableCouponCount = (res.data || []).length
				}
			} catch (error) {
				console.error('加载可用优惠券失败:', error)
			}
		},
		
		// 处理优惠券确认选择
		async handleCouponConfirm(coupon) {
			if (!coupon) {
				// 取消使用优惠券
				this.selectedCoupon = null
				this.finalPrice = this.orderMedicinePrice
				this.discountAmount = 0
				return
			}
			
			try {
				// 调用后端计算接口
				const res = await calculateOrderAmount(this.orderMedicinePrice, coupon.userCouponId)
				
				if (res.code === 1) {
					const result = res.data
					
					// 更新优惠券信息
					this.selectedCoupon = {
						...coupon,
						discountAmount: result.discountAmount,
						finalAmount: result.payAmount
					}
					
					// 更新价格
					this.discountAmount = result.discountAmount
					this.finalPrice = result.payAmount
					
					uni.showToast({
						title: `已优惠¥${result.discountAmount}`,
						icon: 'success'
					})
				} else {
					// 计算失败，提示用户
					uni.showToast({
						title: res.msg || '优惠券计算失败',
						icon: 'none'
					})
					
					// 重置选择
					this.selectedCoupon = null
					this.finalPrice = this.orderMedicinePrice
					this.discountAmount = 0
				}
			} catch (error) {
				console.error('优惠券计算失败:', error)
				uni.showToast({
					title: error.msg || '优惠券计算失败',
					icon: 'none'
				})
				
				// 重置选择
				this.selectedCoupon = null
				this.finalPrice = this.orderMedicinePrice
				this.discountAmount = 0
			}
		},
		
		// 应用优惠券到订单（在提交订单时调用）
		async applyCouponToOrder(orderId) {
			if (!this.selectedCoupon) {
				return null
			}
			
			try {
				const res = await applyCoupon(
					this.selectedCoupon.userCouponId,
					orderId,
					this.orderMedicinePrice
				)
				
				if (res.code === 1 && res.data.success) {
					return res.data
				} else {
					throw new Error(res.msg || '优惠券应用失败')
				}
			} catch (error) {
				console.error('优惠券应用失败:', error)
				throw error
			}
		},
		
		// 取消优惠券应用（在订单取消时调用）
		async cancelCoupon(orderId) {
			if (!this.selectedCoupon) {
				return
			}
			
			try {
				await cancelCouponApply(
					this.selectedCoupon.userCouponId,
					orderId
				)
			} catch (error) {
				console.error('取消优惠券失败:', error)
			}
		}
	}
}
