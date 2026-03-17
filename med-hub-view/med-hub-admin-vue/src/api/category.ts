import request from '@/utils/request';
/**
 *
 * 分类管理
 *
 **/

// 查询分类列表接口
export const getCategoryPage = (params: any) => {
  return request({
    url: '/admin/category/page',
    method: 'get',
    params
  });
};

// 删除当前列的接口
export const deleCategory = (ids: string) => {
  return request({
    url: '/admin/category',
    method: 'delete',
    params: { id:ids }
  });
};

// 修改接口
export const editCategory = (params: any) => {
  return request({
    url: '/admin/category',
    method: 'put',
    data: { ...params }
  });
};

// 新增接口
export const addCategory = (params: any) => {
  return request({
    url: '/admin/category',
    method: 'post',
    data: { ...params }
  });
};

// 修改---启用禁用接口
export const enableOrDisableEmployee = (params: any) => {
  return request({
    url: `/admin/category/status/${params.status}`,
    method: 'post',
    params: { id:params.id }
  })
}
