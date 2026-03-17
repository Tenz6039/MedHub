import request from '@/utils/request'
// 营业额数据
// export const getTurnoverDataes = (data) =>
//   request({
//     'url': `/admin/report/turnoverStatistics`,
//     'method': 'get',
//     data
//   })
// 首页数据
// // 今日数据
// export const getTodayDataes = () =>
//   request({
//     'url': `/admin/workspace/todaydate`,
//     'method': 'get'
//   })
// 订单管理
  export const getOrderData = () =>
  request({
    'url': `/admin/workspace/overviewOrders`,
    'method': 'get'
  })
// 药品总览
export const getOverviewMedicines = () =>
request({
  'url': `/admin/workspace/overviewMedicines`,
  'method': 'get'
})
// 药品组合总览
export const getOverviewMedicineCombos = () =>
request({
  'url': `/admin/workspace/overviewMedicineCombos`,
  'method': 'get'
})
// 营业数据
export const getBusinessData= () =>
request({
  'url': `/admin/workspace/businessData`,
  'method': 'get'
})
/**
 *
 * 报表数据
 *
 **/
// 统计
// 获取当日销售数据 -> 顶部数据
// export const getDataes = (params: any) =>
//   request({
//     'url': `/admin/report/amountCollect/${params.date}`,
//     'method': 'get'
//   })

// 营业额统计
export const getTurnoverStatistics= (params: any) =>
  request({
    'url': `/admin/report/turnoverStatistics`,
    'method': 'get',
    params
  })

// 用户统计
export const getUserStatistics= (params: any) =>
  request({
    'url': `/admin/report/userStatistics`,
    'method': 'get',
    params
  })
  // 订单统计
export const getOrderStatistics= (params: any) =>
request({
  'url': `/admin/report/ordersStatistics`,
  'method': 'get',
  params
})
  // 销量排名 TOP10
  export const getTop= (params: any) =>
  request({
    'url': `/admin/report/top10`,
    'method': 'get',
    params
  })
  // 数据概览
  export const getDataOverView= (params: any) =>
  request({
    'url': `/admin/report/dataOverView`,
    'method': 'get',
    params
  })
  // 导出
  export function exportInfor() {
    return request({
      url: '/admin/report/export',
      method: 'get',
      responseType: 'blob'
    })
  }
