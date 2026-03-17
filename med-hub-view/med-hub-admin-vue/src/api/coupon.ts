import request from '@/utils/request';

/**
 *
 * 优惠券管理
 *
 **/

// 查询优惠券列表接口
export const getCouponPage = (params: any) => {
  return request({
    url: '/admin/coupon/page',
    method: 'get',
    params
  });
};

// 删除优惠券接口
export const deleteCoupon = (id: string) => {
  return request({
    url: `/admin/coupon/${id}`,
    method: 'delete'
  });
};

// 修改优惠券接口
export const editCoupon = (params: any) => {
  return request({
    url: '/admin/coupon',
    method: 'put',
    data: { ...params }
  });
};

// 新增优惠券接口
export const addCoupon = (params: any) => {
  return request({
    url: '/admin/coupon',
    method: 'post',
    data: { ...params }
  });
};

// 查询优惠券详情
export const queryCouponById = (id: string) => {
  return request({
    url: `/admin/coupon/${id}`,
    method: 'get'
  });
};

// 修改优惠券状态（启用/禁用）
export const enableOrDisableCoupon = (params: any) => {
  return request({
    url: `/admin/coupon/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  });
};
