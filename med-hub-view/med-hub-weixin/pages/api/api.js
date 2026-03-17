import { request } from "../../utils/request.js"
	
// ==================== 以下接口已废弃，保留用于兼容性 ====================

// 开桌（已废弃）
// export const openTable = (params) =>
// 	request({
// 		url: `/user/table/open/${params.tableId}/${params.seatNumber}`,
// 		method: 'GET',
// 		params
// 	})

// 获取桌台状态（已废弃）
// export const getTableState = (params) =>
// 	request({
// 		url: `/user/table/tableStatus/${params.shopId}/${params.storeId}/${params.tableId}`,
// 		method: 'GET',
// 		params,
// 	})

// 获取购物车药品（已废弃）
// export const getTableOrderDishList = (params) =>
// 	request({
// 		url: `/user/order/shopCart//${params.tableId}`,
// 		method: 'GET',
// 		params
// 	})

// 获取药品规格（已废弃）
// export const getMoreNorm = (params) =>
// 	request({
// 		url: `/user/dish/flavor/${params.dishId}`,
// 		method: 'GET',
// 		params,
// 	})

// 获取药品分类列表（已废弃）
// export const getList = (params) =>
// 	request({
// 		url: `/user/dish/category`,
// 		method: 'GET',
// 		params,
// 	})

// 获取药品详情（已废弃）
// export const getDishDetail = (params) =>
// 	request({
// 		url: `/user/dish/setmealDishList/${params.setmealId}`,
// 		method: 'GET',
// 		params,
// 	})

// 根据分类获取药品列表（已废弃）
// export const getDishList = (params) =>
// 	request({
// 		url: `/user/dish/dishPageList/${params.categoryId}/${params.type}/${params.page}/${params.pageSize}`,
// 		method: 'GET',
// 		params
// 	})

// 加药（已废弃）
// export const addDish = (params) =>
// 	request({
// 		url: `/user/order/addDish`,
// 		method: 'POST',
// 		params
// 	})

// 减药（已废弃）
// export const delDish = (params) =>
// 	request({
// 		url: `/user/order/decreaseDish/${params.tableId}/${params.dishId}`,
// 		method: 'GET',
// 		params
// 	})

// 清空购物车（已废弃）
// export const clearOrder = (params) =>
// 	request({
// 		url: `/user/order/cleanShopCart/${params.tableId}`,
// 		method: 'GET',
// 		params
// 	})

// 提交订单（已废弃）
// export const payOrder = (params) =>
// 	request({
// 		url: `/user/order/pay/${params.tableId}/${params.jsCode}`,
// 		method: 'GET',
// 		params
// 	})

// ==================== 以下是当前使用的接口 ====================


// 用户登录
export const userLogin = (params) => {
	return request({
		url: '/user/user/login',
		method: 'POST',
		params
	})
}

// 用户退出
export const userLogout = (params) => {
	return request({
		url: '/user/user/logout',
		method: 'POST',
		params
	})
}

// 药品和药品组合的分类
export const getCategoryList = (params) => {
	return request({
		url: '/user/category/list',
		method: 'GET',
		params
	})
}

// 查询药品管理列表
export const medicineListByCategoryId = (params) => {
	return request({
		url: '/user/medicine/list',
		method: 'GET',
		params
	})
}

// 文件下载---预览（已废弃）
// export const commonDownload = (params) => {
// 	return request({
// 		url: '/user/common/download',
// 		method: 'GET',
// 		params
// 	})
// }


// 购物车----加药功能实现（已废弃）
// export const addShoppingCart = (params) => {
// 	return request({
// 		url: '/user/shoppingCart',
// 		method: 'POST',
// 		params
// 	})
// }

// 根据type类型查询是药品组合的接口
export const querySetmeaList = (params) => {
	return request({
		url: '/user/medicineCombo/list',
		method: 'GET',
		params
	})
}

// 获取购物车集合
export const getShoppingCartList = (params) => {
	return request({
		url: '/user/shoppingCart/list',
		method: 'GET',
		params
	})
}


// 修改购物车数量功能（已废弃）
// export const editHoppingCart = (params) => {
// 	return request({
// 		url: '/user/shoppingCart',
// 		method: 'PUT',
// 		params
// 	})
// }


// 购物车新增接口-new
export const newAddShoppingCartAdd = (params) => {
	return request({
		url: '/user/shoppingCart/add',
		method: 'POST',
		params
	})
}


// 购物车减药接口-new 
export const newShoppingCartSub = (params) => {
	return request({
		url: '/user/shoppingCart/sub',
		method: 'POST',
		params
	})
}


// 清除购物车
export const delShoppingCart = (params) => {
	return request({
		url: '/user/shoppingCart/clean',
		method: 'DELETE',
		params
	})
}


// 最近订单和历史订单（已废弃）
// export const queryOrderUserPage = (params) => {
// 	return request({
// 		url: '/user/order/historyOrders',
// 		method: 'GET',
// 		params
// 	})
// }


// 用户下单
export const submitOrderSubmit = (params) => {
	return request({
		url: '/user/order/submit',
		method: 'POST',
		params
	})
}


// 查询地址列表
export const queryAddressBookList = (params) => {
	return request({
		url: '/user/addressBook/list',
		method: 'GET',
		params
	})
}

// 新增默认接口
export const putAddressBookDefault = (params) => {
	return request({
		url: '/user/addressBook/default',
		method: 'PUT',
		params
	})
}


// 新增地址接口
export const addAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'POST',
		params
	})
}

// 修改地址接口
export const editAddressBook = (params) => {
	return request({
		url: '/user/addressBook',
		method: 'PUT',
		params
	})
}

// 删除地址
export const delAddressBook = (id) => {
	return request({
		url: `/user/addressBook`,
		method: 'DELETE',
		params: { id }
	})
}

// 查询地址通过id
export const queryAddressBookById = (params) => {
	return request({
		url: `/user/addressBook/${params.id}`,
		method: 'GET',
		params
	})
}


// 再来一单（已废弃）
// export const oneOrderAgain = (params) => {
// 	return request({
// 		url: '/user/order/again',
// 		method: 'POST',
// 		params
// 	})
// }

// 查询默认地址
export const getAddressBookDefault = () => {
	return request({
		url: '/user/addressBook/default',
		method: 'GET'
	})
}


// 此接口为首页查询药品组合详情展示的接口
export const queryCombinationMedicineById = (params) => {
	return request({
		url: `/user/medicineCombo/medicine/${params.id}`,
		method: 'GET'
	})
}
// ==================== v2.0 添加接口 ====================
// 获取首页药房信息
export const getShopStatus = (params) => {
	return request({
		url: `/user/shop/status`,
		method: 'GET'
	})
}
// 获取药房信息
export const getMerchantInfo = (params) => {
	return request({
		url: `/user/shop/getMerchantInfo`,
		method: 'GET'
	})
}
// 获取店铺详细信息
export const getShopInfo = (params) => {
	return request({
		url: `/user/shop/info`,
		method: 'GET'
	})
}
// 历史订单
export const getOrderPage = (params) => {
	return request({
		url: '/user/order/historyOrders',
		method: 'GET',
		params
	})
}
// 订单详情
export const getOrderDetail = (params) =>
	request({
		url: `/user/order/orderDetail/${params}`,
		method: 'GET'
	})
// 取消订单
export const cancelOrder = (params) =>
	request({
		url: `/user/order/cancel/${params}`,
		method: 'PUT'
	})
// 催单
export const reminderOrder = (params) =>
	request({
		url: `/user/order/reminder/${params}`,
		method: 'GET'
	})
// 订单支付
export const paymentOrder = (params) =>
	request({
		url: `/user/order/payment`,
		method: 'PUT',
		params
	})
// 支付成功回调
export const paySuccess = (params) =>
	request({
		url: `/user/order/paySuccess`,
		method: 'GET',
		params
	})
// 再来一单
export const repetitionOrder = (params) =>
	request({
		url: `/user/order/repetition/${params}`,
		method: 'POST',
		params
	})


// 获取用户送餐期望时间
export const getEstimatedDeliveryTime = (params) =>
	request({
		url: `/user/order/getEstimatedDeliveryTime`,
		method: 'get',
		params
	})
// 查询用户订单支付状态列表信息
export const queryOrdersCheckStatus = (params) =>
	request({
		url: `/user/order/queryOrdersCheckStatus`,
		method: 'get',
		params
	})

// ==================== 优惠券相关接口 ====================

/**
 * 可领取优惠券列表
 * @param {Object} params - 分页参数
 * @param {Number} params.page - 页码，默认 1
 * @param {Number} params.pageSize - 每页记录数，默认 10
 */
export const getAvailableCoupons = (params) => {
	return request({
		url: '/user/coupon/available',
		method: 'GET',
		params
	})
}

/**
 * 领取优惠券
 * @param {Number} couponId - 优惠券 ID
 */
export const obtainCoupon = (couponId) => {
	return request({
		url: `/user/coupon/obtain/${couponId}`,
		method: 'POST'
	})
}

/**
 * 我的优惠券列表
 * @param {Object} params - 查询参数
 * @param {Number} params.page - 页码
 * @param {Number} params.pageSize - 每页记录数
 * @param {Number} params.status - 优惠券状态：0-未使用，1-已使用，2-已过期
 */
export const getMyCoupons = (params) => {
	return request({
		url: '/user/coupon/my',
		method: 'GET',
		params
	})
}

/**
 * 下单可用优惠券列表
 * @param {Number} orderAmount - 订单金额
 */
export const getAvailableForOrder = (orderAmount) => {
	console.log('getAvailableForOrder 调用，orderAmount:', orderAmount)
	return request({
		url: '/user/coupon/available-for-order',
		method: 'GET',
		params: {
			orderAmount
		}
	})
}

/**
 * 验证优惠券有效性
 * @param {Number} userCouponId - 用户优惠券 ID
 * @param {Number} orderAmount - 订单金额
 */
export const validateCoupon = (userCouponId, orderAmount) => {
	return request({
		url: '/user/coupon/validate',
		method: 'GET',
		params: {
			userCouponId,
			orderAmount
		}
	})
}

/**
 * 计算订单金额（使用优惠券）
 * @param {Number} orderAmount - 订单金额
 * @param {Number} userCouponId - 用户优惠券 ID
 */
export const calculateOrderAmount = (orderAmount, userCouponId) => {
	console.log('calculateOrderAmount 调用，orderAmount:', orderAmount, 'userCouponId:', userCouponId)
	return request({
		url: '/user/coupon/calculate',
		method: 'POST',
		params: {
			orderAmount,
			userCouponId
		}
	})
}

/**
 * 应用优惠券到订单
 * @param {Number} userCouponId - 用户优惠券 ID
 * @param {Number} orderId - 订单 ID
 * @param {Number} orderAmount - 订单金额
 */
export const applyCoupon = (userCouponId, orderId, orderAmount) => {
	return request({
		url: '/user/coupon/apply',
		method: 'POST',
		params: {
			userCouponId,
			orderId,
			orderAmount
		}
	})
}

/**
 * 取消优惠券应用
 * @param {Number} userCouponId - 用户优惠券 ID
 * @param {Number} orderId - 订单 ID
 */
export const cancelCouponApply = (userCouponId, orderId) => {
	return request({
		url: '/user/coupon/cancel',
		method: 'POST',
		params: {
			userCouponId,
			orderId
		}
	})
}


