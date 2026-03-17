import request from '@/utils/request'
/**
 *
 * 药品管理
 *
 **/
// 查询列表接口
export const getMedicinePage = (params: any) => {
  return request({
    url: '/admin/medicine/page',
    method: 'get',
    params
  })
}

// 删除接口
export const deleteMedicine = (ids: string) => {
  return request({
    url: '/admin/medicine',
    method: 'delete',
    params: { ids }
  })
}

// 修改接口
export const editMedicine = (params: any) => {
  return request({
    url: '/admin/medicine',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
export const addMedicine = (params: any) => {
  return request({
    url: '/admin/medicine',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情
export const queryMedicineById = (id: string | (string | null)[]) => {
  return request({
    url: `/admin/medicine/${id}`,
    method: 'get'
  })
}

// 获取药品分类列表
export const getCategoryList = (params: any) => {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params
  })
}

// 查药品列表的接口
export const queryMedicineList = (params: any) => {
  return request({
    url: '/admin/medicine/list',
    method: 'get',
    params
  })
}

// 文件 down 预览
export const commonDownload = (params: any) => {
  return request({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/admin/common/download',
    method: 'get',
    params
  })
}

// 起售停售---批量起售停售接口
export const medicineStatusByStatus = (params: any) => {
  return request({
    url: `/admin/medicine/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

//药品分类数据查询
export const medicineCategoryList = (params: any) => {
  return request({
    url: `/admin/category/list`,
    method: 'get',
    params: { ...params }
  })
}
