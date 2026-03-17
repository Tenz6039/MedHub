import store from './../store'
import { baseUrl } from './env'

// 参数：url:请求地址  param：请求参数  method：请求方式 callBack：回调函数
export function request({url='', params={}, method='GET'}) {
	uni.getStorage({
		key: ''
	})
	const storeInfo = store.state
	let header = {
			'Accept': 'application/json',
			'Access-Control-Allow-Origin':'*',
			// 'shopid':storeInfo.storeInfo.shopId ?? '',
			// 'storeid':storeInfo.storeInfo.storeId ?? '',
			'authentication': storeInfo.token
		}
	
	// 根据请求方法设置 Content-Type 和数据格式
	let requestData = params
	if (method === 'POST') {
		header['Content-Type'] = 'application/json'
		// POST 请求需要将数据转换为 JSON 字符串
		requestData = JSON.stringify(params)
	} else {
		header['Content-Type'] = 'application/json'
	}
	
	const requestRes = new Promise((resolve, reject) => {
		store.commit('setLodding', false)
		
		console.log('request 调用:', {
			url: baseUrl+url,
			method: method,
			data: requestData,
			header: header
		})
		
		 uni.request({
			url: baseUrl+url, 
			data: requestData,
			header: header,
			method: method,
			success: (res) => {
				const { data } = res
				if (data.code == 200 || data.code === 1) {
					// store.commit('setLodding', false)
					resolve(res.data)
				} else if (data.code === 401) {
					// 401 未授权，调用微信登录
					uni.showModal({
						title: '提示',
						content: '请先登录',
						showCancel: false,
						success: () => {
							// 调用微信登录
							uni.login({
								provider: 'weixin',
								success: (loginRes) => {
									// 登录成功后，将 code 发送给后端获取 token
									uni.request({
										url: baseUrl + '/user/wx/login',
										method: 'POST',
										data: {
											code: loginRes.code
										},
										success: (loginRes) => {
											if (loginRes.data.code === 1) {
												// 保存 token
												store.commit('setToken', loginRes.data.data.token)
												uni.setStorageSync('token', loginRes.data.data.token)
												// 重新请求原接口
												request({url, params, method}).then(resolve).catch(reject)
											}
										}
									})
								},
								fail: (err) => {
									console.error('微信登录失败', err)
								}
							})
						}
					})
					reject(data)
				} else {
					// store.commit('setLodding', true)
					reject(res.data)
				}
			},
			fail: (err) => {
				const error = {data:{msg:err.data}}
				// store.commit('setLodding', true)
				reject(error)
			}
		});
	})
	return requestRes
}

