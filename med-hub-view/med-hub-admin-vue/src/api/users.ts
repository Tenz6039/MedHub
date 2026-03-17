import request from '@/utils/request'
// 修改密码
export const editPassword = (data: any) =>
  request({
    'url': '/admin/employee/editPassword',
    'method': 'put',
    data
  })
  // 获取营业状态
  export const getStatus = () =>
  request({
    'url': `/admin/shop/status`,
    'method': 'get'
  })
    // 设置营业状态
  export const setStatus = (data:any) =>
    request({
      'url': `/admin/shop/${data}`,
      'method': 'put'
    })
