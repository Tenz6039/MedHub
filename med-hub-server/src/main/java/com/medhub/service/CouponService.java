package com.medhub.service;

import com.medhub.dto.CouponCreateDTO;
import com.medhub.dto.CouponEditDTO;
import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.result.PageResult;
import com.medhub.vo.CouponVO;

public interface CouponService {
    /**
     * 创建优惠券
     * @param couponCreateDTO 优惠券创建 DTO
     * @return 优惠券 ID
     */
    Long createCoupon(CouponCreateDTO couponCreateDTO);
    
    /**
     * 修改优惠券
     * @param couponEditDTO 优惠券编辑 DTO
     */
    void updateCoupon(CouponEditDTO couponEditDTO);
    
    /**
     * 删除优惠券
     * @param id 优惠券 ID
     */
    void deleteCoupon(Long id);
    
    /**
     * 分页查询优惠券
     * @param couponPageQueryDTO 分页查询 DTO
     * @return 分页结果
     */
    PageResult pageCoupon(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 查询优惠券详情
     * @param id 优惠券 ID
     * @return 优惠券 VO
     */
    CouponVO getById(Long id);

    /**
     * 修改优惠券状态
     * @param id 优惠券 ID
     * @param status 状态：0-禁用，1-启用
     */
    void updateStatus(Long id, Integer status);
}
