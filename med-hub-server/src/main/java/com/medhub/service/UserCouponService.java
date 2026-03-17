package com.medhub.service;

import com.medhub.dto.UserCouponQueryDTO;
import com.medhub.result.PageResult;
import com.medhub.vo.CouponApplyResultVO;
import com.medhub.vo.CouponValidateResultVO;
import com.medhub.vo.OrderAmountCalculateVO;
import com.medhub.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户优惠券 Service
 */
public interface UserCouponService {

    /**
     * 查询可领取的优惠券列表
     * @param couponPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    PageResult availableList(com.medhub.dto.CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 领取优惠券
     * @param couponId 优惠券 ID
     * @return 用户优惠券 ID
     */
    Long obtain(Long couponId);

    /**
     * 查询我的优惠券列表
     * @param userCouponQueryDTO 查询条件
     * @return 分页结果
     */
    PageResult myCoupons(UserCouponQueryDTO userCouponQueryDTO);

    /**
     * 查询下单可用优惠券列表
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    List<UserCouponVO> getAvailableForOrder(BigDecimal orderAmount);

    /**
     * 验证优惠券有效性
     * @param userCouponId 用户优惠券 ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    CouponValidateResultVO validateCoupon(Long userCouponId, BigDecimal orderAmount);

    /**
     * 计算订单金额（使用优惠券）
     * @param orderAmount 订单商品总金额
     * @param userCouponId 用户优惠券 ID
     * @return 订单金额计算结果
     */
    OrderAmountCalculateVO calculateOrderAmount(BigDecimal orderAmount, Long userCouponId);

    /**
     * 应用优惠券到订单
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     * @param orderAmount 订单金额
     * @return 应用结果
     */
    CouponApplyResultVO applyCoupon(Long userCouponId, Long orderId, BigDecimal orderAmount);

    /**
     * 取消优惠券应用（解锁优惠券）
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     */
    void cancelCouponApply(Long userCouponId, Long orderId);
}
