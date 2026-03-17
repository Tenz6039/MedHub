import navBar from "../common/Navbar/navbar.vue" //标题
import Phone from "@/components/uni-phone/index.vue" //拨打电话
import popMask from "./components/popMask.vue" //规格
import popCart from "./components/popCart.vue" //购物车弹出层
import medicineDetail from "./components/medicineDetail.vue" //药品详情
import {
	// 智慧购药平台相关的接口
	userLogin,
	getCategoryList,
	medicineListByCategoryId,
	// 查询药品组合列表的接口
	querySetmeaList,
	// 获取购物车集合
	getShoppingCartList,
	// 新的购物车添加逻辑接口
	newAddShoppingCartAdd,
	// 新的购物车减少接口
	newShoppingCartSub,
	// 清空购物车
	delShoppingCart,
	// 此接口为首页查询药品组合详情展示的接口
	queryCombinationMedicineById,
	// 获取药房信息
	getShopStatus,
	// 获取药房联系方式
	getMerchantInfo,
	// 获取店铺详细信息
	getShopInfo,
} from "../api/api.js"
import { mapState, mapMutations } from "vuex"
import { baseUrl } from "../../utils/env"
export default {
	data() {
		return {
			title: "Hello",
			// 去结算部分
			openOrderCartList: false,
			// 存放左侧滚动区域药品分类数组
			typeListData: [],
			medicineListData: [],
			// 存放右侧对应药品每个药名称的数组
			medicineListItems: [],
			medicineDetailes: {},
			openDetailPop: false,
			openMoreNormPop: false,
			moreNormDataes: null,
			tableInfo: null,
			moreNormMedicinedata: null,
			moreNormdata: null,
			// 药品组合中查询到的药品名称
			medicineCombinationData: null,
			openTablePeoPleNumber: 1,
			orderData: 0,
			// 选中左侧药品的索引
			typeIndex: 0,
			// 控制药品详情显示
			openTablePop: false,
			// 规格有关的数组
			specificationDataes: [],
			// 加入购物车数量
			orderMedicineNumber: 0,
			// 药品金额
			orderMedicinePrice: 0,
			params: {
				shopId: "f3deb",
				storeId: "1282344676983062530",
				tableId: "1282346960773238786",
			},
			// 添加一个右侧 number 更新以后重新刷新接口的 id --- 这个 id 来自左侧药品分类的 id
			rightIdAndType: {},
			phoneData: "",
			packageNumber: 0,
			shopStatus: null,
			scrollTop: 0,
			menuHeight: 0, // 左边分类的高度
			menuItemHeight: 0, // 左边分类 item 的高度
			itemId: "", // 栏目右边 scroll-view 用于滚动的 id
			arr: [],
		}
	},
	//   组件
	components: {
		navBar,
		Phone,
		popMask,
		popCart,
		medicineDetail,
	},
	//   计算属性
	computed: {
		// 从 vuex 信息
		...mapState([
			"shopInfo", //药房信息
			"shopPhone", //电话
			"orderListData",
			"baseUserInfo", //用户信息
			"lodding",
			"token", //token
			"deliveryFee", //配送费
		]),
		// 购物车信息列表
		orderListDataes: function () {
			return this.orderListData
		},
		loaddingSt: function () {
			return this.lodding
		},
		// 计算购物车清单
		orderAndUserInfo: function () {
			let orderData = []
			Array.isArray(this.orderListDataes) &&
				this.orderListDataes.forEach((n, i) => {
					let userData = {}
					userData.nickName = n.name ?? ""
					userData.avatarUrl = n.image ?? ""
					userData.medicineList = [n]
					const num = orderData.findIndex(
						(o) => o.nickName == userData.nickName
					)
					if (num != -1) {
						orderData[num].medicineList.push(n)
					} else {
						orderData.push(userData)
					}
				})
			return orderData
		},
		ht: function () {
			return (
				uni.getMenuButtonBoundingClientRect().top +
				uni.getMenuButtonBoundingClientRect().height +
				7
			)
		},
	},

	onReady() {
		this.getMenuItemTop()
	},
	onLoad(options) {
		uni.onNetworkStatusChange(function (res) {
			if (res.isConnected == false) {
				uni.navigateTo({
					url: "/pages/nonet/index",
				})
			}
		})
		if (options) {
			if (!options.status && !options.formOrder) {
				this.getData()
			}
		}
	},
	onShow() {
		if (this.token) {
			this.init()
		}
	},
	methods: {
		//   vuex 储存信息
		...mapMutations([
			"setShopInfo", //设置药房信息
			"setShopPhone", //设置电话
			"setShopStatus", //设置药房状态
			"initMedicineListMut", //设置购物车订单
			"setStoreInfo",
			"setBaseUserInfo", //设置用户基本信息
			"setLodding",
			"setToken", //设置 token
			"setDeliveryFee", //设置配送费
		]),
		loginSync() {
			return new Promise((resolve, reject) => {
				uni.login({
					success: (loginRes) => {
						if (loginRes.errMsg === "login:ok") {
							resolve(loginRes.code)
						}
					},
				})
			})
		},
		// 获取用户信息
		getData() {
			let _this = this
			// 获取药房状态
			this.getShopInfo()
			if (this.token === "") {
				uni.showModal({
					title: "温馨提示",
					content: "亲，授权微信登录后才能点药！",
					showCancel: false,
					success(res) {
						if (res.confirm) {
							let jsCode = ""
							uni.login({
								provider: "weixin",
								success: (loginRes) => {
									if (loginRes.errMsg === "login:ok") {
										jsCode = loginRes.code
									}
								},
							})
							// 授权
							uni.getUserProfile({
								desc: "登录",
								success: function (userInfo) {
									_this.setBaseUserInfo(userInfo.userInfo)
									const params = {
										code: jsCode,
										// 传递地理位置信息
									}
									// 获取定位信息
									uni.getLocation({
										type: 'gcj02', isHighAccuracy: true
									}).then(([err, result]) => {
										if (err) {
											uni.showToast({
												title: "获取地理位置失败",
												icon: "none"
											})
										} else {
											if (process.env.NODE_ENV === '"development"') {
												params.location = `116.481488,39.990464`//	先写死在北京
											} else {
												params.location = `${result.longitude},${result.latitude}`
											}

													userLogin(params)
												.then((success) => {
													if (success.code === 1) {
														_this.setToken(success.data.token)
														// 调用店铺信息接口，获取配送费和店铺信息
														_this.getShopDetailInfo()
														_this.init()
													}
												})
												.catch((err) => { })



										}

									})

								},
								fail: function (err) { },
							})
						}
					},
				})
			}
		},

		async init() {
			// 获取药品和药品组合分类接口
			if (this.typeIndex !== 0) {
				this.typeIndex = 0
			}

			// 获取药房联系方式
			this.getMerchantInfo()
			getCategoryList().then((res) => {
				if (res && res.code === 1) {
					this.typeListData = [...res.data]
					if (res.data.length > 0) {
						this.getMedicineListDataes(res.data[this.typeIndex || 0])
					}
				}
			})
			// 调用一次购物车集合---初始化
			this.getMedicineOrderList()
		},
		// 点击左边的栏目切换
		async swichMenu(params, index) {
			if (this.arr.length == 0) {
				await this.getMenuItemTop()
			}
			if (index == this.typeIndex) return
			this.$nextTick(function () {
				this.typeIndex = index
				this.leftMenuStatus(index)
			})
			this.getMedicineListDataes(params, index)
		},
		// 获取一个目标元素的高度
		getElRect(elClass, dataVal) {
			new Promise((resolve, reject) => {
				const query = uni.createSelectorQuery().in(this)
				query
					.select("." + elClass)
					.fields(
						{
							size: true,
						},
						(res) => {
							// 如果节点尚未生成，res值为null，循环调用执行
							if (!res) {
								setTimeout(() => {
									this.getElRect(elClass)
								}, 10)
								return
							}
							this[dataVal] = res.height
							resolve()
						}
					)
					.exec()
			})
		},
		// 设置左边分类的滚动状态
		async leftMenuStatus(index) {
			this.typeIndex = index
			// 如果为0，意味着尚未初始化
			if (this.menuHeight == 0 || this.menuItemHeight == 0) {
				await this.getElRect("menu-scroll-view", "menuHeight")
				await this.getElRect("type_item", "menuItemHeight")
			}
			// 将分类活动item垂直居中
			this.scrollTop =
				index * this.menuItemHeight +
				this.menuItemHeight / 2 -
				this.menuHeight / 2
		},
		// 获取右边分类每个item到顶部的距离
		getMenuItemTop() {
			new Promise((resolve) => {
				let selectorQuery = uni.createSelectorQuery()
				selectorQuery
					.selectAll(".class-item")
					.boundingClientRect((rects) => {
						// 如果节点尚未生成，rects值为[](因为用selectAll，所以返回的是数组)，循环调用执行
						if (!rects.length) {
							setTimeout(() => {
								this.getMenuItemTop()
							}, 10)
							return
						}
					})
					.exec()
			})
		},
		// 获取药品列表
		async getMedicineListDataes(params, index) {
			this.rightIdAndType = {}
			this.rightIdAndType = {
				id: params.id,
				type: params.type,
			}
			const param = {
				categoryId: params.id,
			}
			// type：1 是药品、2 是药品组合
			if (params.type === 1) {
				await medicineListByCategoryId(param)
					.then((res) => {
						if (res && res.code === 1) {
							// 添加一个字段去实时更新加入购物车 number 数量 ----- newCardNumber
							this.medicineListData =
								res.data &&
								res.data.map((obj) => ({
									...obj,
									type: 1,
									newCardNumber: 0,
								}))
						}
					})
					.catch((err) => { })
			} else {
				// 药品组合
				await querySetmeaList(param)
					.then((success) => {
						if (success && success.code === 1) {
							// medicineListItems 被转换数组---原始 this.medicineListData
							this.medicineListData =
								success.data &&
								success.data.map((obj) => ({
									...obj,
									type: 2,
									newCardNumber: 0,
								}))
						}
					})
					.catch((err) => { })
			}
			this.typeIndex = index
			this.setOrderNum()
		},
		// 获取首页药房信息
		async getShopInfo() {
			await getShopStatus()
				.then((res) => {
					this.shopStatus = res.data
					console.log(res.data);
					this.setShopStatus(res.data)
				})
				.catch((err) => { })
		},
		// 获取店铺详细信息（包含配送费、地址等）
		async getShopDetailInfo() {
			await getShopInfo()
				.then((res) => {
					if (res.code === 1 && res.data) {
						const shopData = res.data
						// 保存配送费
						this.setDeliveryFee(shopData.deliveryFee || 0)
						// 保存店铺信息
						this.setShopInfo({
							shopName: shopData.name || '智慧购药平台',
							shopAddress: shopData.address || '',
							shopId: shopData.id || '',
							phone: shopData.phone || '',
							deliveryFee: shopData.deliveryFee || 0,
							minDeliveryAmount: shopData.minDeliveryAmount || 0,
							status: shopData.status || 1,
						})
						console.log('店铺信息获取成功:', shopData)
					}
				})
				.catch((err) => {
					console.error('获取店铺信息失败:', err)
					// 失败时使用默认值
					this.setDeliveryFee(0)
					this.setShopInfo({
						shopName: '智慧购药平台',
						shopAddress: '',
						shopId: '',
					})
				})
		},
		// 获取药房电话
		async getMerchantInfo() {
			await getMerchantInfo()
				.then((res) => {
					this.phoneData = res.data.phone
					console.log(res);
					this.setShopPhone(res.data)
				})
				.catch((err) => { })
		},
		// 重新拼装image
		getNewImage(image) {
			return `${baseUrl}/common/download?name=${image}`
		},
		// 获取购物车订单列表
		async getMedicineOrderList() {
			// 调用获取购物车集合接口
			await getShoppingCartList({})
				.then((res) => {
					if (res.code === 1) {
						this.initMedicineListMut(res.data)
						this.computOrderInfo()
					}
				})
				.catch((err) => { })
		},
		// 去订单页面
		goOrder() {
			uni.navigateTo({
				url: "/pages/order/index",
			})
		},
		// 加药 - 添加药品
		async addMedicineToCartAction(item, form) {
			// 规格
			if (
				this.openMoreNormPop &&
				(!this.specificationDataes || this.specificationDataes.length <= 0)
			) {
				uni.showToast({
					title: "请选择规格",
					icon: "none",
				})
				return false
			}
			this.openMoreNormPop = false
			// 实时更新 obj.newCardNumber 新添加的字段----加入购物车数量 number
			this.packageNumber++
			this.medicineDetailes.medicineNumber++
			if (
				this.orderListDataes &&
				!this.orderListDataes.some((n) => n.id == item.medicineId) &&
				this.specificationDataes.length > 0
			) {
				item.specificationRemark = JSON.stringify(this.specificationDataes)
			}
			// 有 sort 字段是药品
			let medicineSpecData = ""
			let specificationRemark = []
			if (item.specificationRemark) {
				specificationRemark = JSON.parse(item.specificationRemark)
			}
			if (item.medicineSpec !== "" && item.medicineSpec) {
				medicineSpecData = item.medicineSpec
			} else if (specificationRemark.length > 0) {
				medicineSpecData = specificationRemark.join(',')
			} else {
				medicineSpecData = null
			}
			let params = {
				medicineSpec: medicineSpecData,
			}
			console.log('添加药品参数:', {
				item: item,
				type: item.type,
				id: item.id,
				medicineId: item.medicineId,
				setmealId: item.setmealId
			})
			if (item.type === 1) {
				params = {
					...params,
					medicineId: item.id,
				}
			} else if (item.type === 2) {
				params = {
					comboId: item.id,
				}
			} else if (form === "购物车") {
				if (item.medicineId) {
					params = {
						...params,
						medicineId: item.medicineId,
					}
				} else {
					params = {
						comboId: item.setmealId,
					}
				}
			}
			console.log('最终参数:', params)
			newAddShoppingCartAdd(params)
				.then((res) => {
					if (res.code === 1) {
						// 调用一次购物车集合---初始化
						this.getMedicineOrderList()
						// 重新调取刷新右侧具体药品列表
						this.getMedicineListDataes(this.rightIdAndType)
						this.specificationDataes = []
						uni.showToast({
							title: '添加成功',
							icon: 'success',
							duration: 2000
						})
					} else {
						uni.showToast({
							title: res.msg || '添加失败',
							icon: 'none',
							duration: 2000
						})
					}
				})
				.catch((err) => {
					console.error('添加购物车失败:', err)
					uni.showToast({
						title: err.msg || err.message || '添加失败，请稍后重试',
						icon: 'none',
						duration: 2000
					})
				})
		},
		// 从购物车中加药
		addShop(item) {
			console.log(item);
			this.medicineDetailes = item
			this.addMedicineToCartAction(item, "购物车")
		},
		// 减药 - 添加药品
		async redMedicineFromCartAction(item, form) {
			// 实时更新 obj.newCardNumber 新添加的字段----加入购物车数量 number
			this.packageNumber--
			this.medicineDetailes.medicineNumber--
			let medicineSpecData = ""
			let specificationRemark = []
			if (item.specificationRemark) {
				specificationRemark = JSON.parse(item.specificationRemark)
			}
			if (item.medicineSpec !== "" && item.medicineSpec) {
				medicineSpecData = item.medicineSpec
			} else if (specificationRemark.length > 0) {
				medicineSpecData = specificationRemark[0]
			} else {
				medicineSpecData = null
			}
			let params = {
				medicineSpec: medicineSpecData,
			}
			if (item.type === 1) {
				params = {
					...params,
					medicineId: item.id,
				}
			} else if (item.type === 2) {
				params = {
					comboId: item.id,
				}
			} else if (form === "购物车") {
				if (item.medicineId) {
					params = {
						...params,
						medicineId: item.medicineId,
					}
				} else {
					params = {
						comboId: item.setmealId,
					}
				}
			}
			await newShoppingCartSub(params)
				.then((res) => {
					if (res.code === 1) {
						// 调用一次购物车集合---初始化
						this.getMedicineOrderList()
						// 重新调取刷新右侧具体药品列表
						this.getMedicineListDataes(this.rightIdAndType)
					}
				})
				.catch((err) => {
					console.error('减少购物车商品失败:', err)
					uni.showToast({
						title: err.msg || err.message || '操作失败，请稍后重试',
						icon: 'none',
						duration: 2000
					})
				})
		},
		// 清空购物车
		clearCardOrder() {
			delShoppingCart()
				.then((res) => {
					this.openOrderCartList = false
					// 调用一次购物车集合---初始化
					this.getMedicineOrderList()
					// 重新调取刷新右侧具体药品列表
					this.getMedicineListDataes(this.rightIdAndType)
					uni.showToast({
						title: '清空成功',
						icon: 'success',
						duration: 2000
					})
				})
				.catch((err) => {
					console.error('清空购物车失败:', err)
					uni.showToast({
						title: err.msg || err.message || '清空失败，请稍后重试',
						icon: 'none',
						duration: 2000
					})
				})
		},
		// 打开药品详情
		openDetailHandle(item) {
			console.log('点击商品:', item);
			console.log('商品类型:', item.type);
			this.medicineDetailes = item
			if (item.type === 2) {
				queryCombinationMedicineById({
					id: item.id,
				})
					.then((res) => {
						if (res.code === 1) {
							this.openDetailPop = true
							this.medicineCombinationData = res.data
							console.log('药品组合详情数据:', res.data);
						}
					})
					.catch((err) => { })
			} else {
				this.openDetailPop = true
				console.log('打开药品详情，状态:', this.openDetailPop);
			}
		},
		// 关闭药品详情
		medicineClose() {
			this.openDetailPop = false
		},
		// 多规格数据处理
		moreNormDataesHandle(item) {
			this.specificationDataes.splice(0)
			this.moreNormMedicinedata = item
			this.openDetailPop = false
			this.openMoreNormPop = true
			this.moreNormdata = item.specifications.map((obj) => ({
				...obj,
				value: JSON.parse(obj.value),
			}))
			this.moreNormdata.forEach((item) => {
				if (item.value && item.value.length > 0) {
					this.specificationDataes.push(item.value[0])
				}
			})
		},
		// 选规格 处理一行只能选择一种
		checkMoreNormPop(val) {
			let obj = val.obj
			let item = val.item
			let ind
			let findst = obj.some((n) => {
				ind = this.specificationDataes.findIndex((o) => o == n)
				return ind != -1
			})
			const num = this.specificationDataes.findIndex((it) => it == item)
			if (num == -1 && !findst) {
				this.specificationDataes.push(item)
			} else if (findst) {
				this.specificationDataes.splice(ind, 1)
				this.specificationDataes.push(item)
			} else {
				this.specificationDataes.splice(num, 1)
			}
		},
		// 关闭选规格弹窗
		closeMoreNorm(moreNormMedicinedata) {
			this.specificationDataes.splice(0, this.specificationDataes.length)
			this.openMoreNormPop = false
		},
		// 订单里和总订单价格计算
		computOrderInfo() {
			let oriData = this.orderListDataes || []
			this.orderMedicineNumber = 0
			this.orderMedicinePrice = 0
			oriData.forEach((n) => {
				if (n.number && n.amount) {
					this.orderMedicineNumber += n.number
					this.orderMedicinePrice += n.number * n.amount
				}
			})
		},
		// 处理点药数量 - 更新药品已点药数量
		setOrderNum() {
			let ODate = this.medicineListData
			let CData = this.orderListDataes
			ODate &&
				ODate.map((obj, index) => {
					obj.medicineNumber = 0
					// 去除空的规格
					if (obj.specifications) {
						obj.specifications.forEach((value, i) => {
							if (value.name === "") {
								obj.specifications.splice(i, 1)
							}
						})
					}

					if (CData.length > 0) {
						CData &&
							CData.forEach((tg, ind) => {
								if (obj.id === tg.medicineId) {
									obj.medicineNumber = tg.number
								}
								if (obj.id === tg.setmealId) {
									obj.medicineNumber = tg.number
								}
							})
					}
				})
			if (this.medicineListItems.length == 0) {
				this.medicineListItems = ODate
			} else {
				this.medicineListItems.splice(0, this.medicineListItems.length, ...ODate)
			}
		},
		// 拨打电话弹层
		handlePhone(type) {
			this.$refs.phone.$refs.popup.open(type)
		},
		// 关闭电话弹层
		closePopup(type) {
			this.$refs.phone.$refs.popup.close(type)
		},
		disabledScroll() {
			return false
		},
	},
}
