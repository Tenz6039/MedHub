import request from '@/utils/request'
/**
 *
 * 药品组合管理
 *
 **/

// 查询列表数据
export const getMedicineComboPage = (params: any) => {
  return request({
    url: '/admin/medicineCombo/page',
    method: 'get',
    params,
  },)
}

// 删除数据接口
export const deleteMedicineCombo = (ids: string) => {
  return request({
    url: '/admin/medicineCombo',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
export const editMedicineCombo = (params: any) => {
  return request({
    url: '/admin/medicineCombo',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
export const addMedicineCombo = (params: any) => {
  return request({
    url: '/admin/medicineCombo',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情接口
export const queryMedicineComboById = (id: string | (string | null)[]) => {
  return request({
    url: `/admin/medicineCombo/${id}`,
    method: 'get'
  })
}

// 批量起售禁售
export const medicineComboStatusByStatus = (params: any) => {
  return request({
    url: `/admin/medicineCombo/status/${params.status}`,
    method: 'post',
    params: { id: params.ids }
  })
}

//药品分类数据查询
export const medicineComboCategoryList = (params: any) => {
  return request({
    url: `/admin/category/list`,
    method: 'get',
    params: { ...params }
  })
}
