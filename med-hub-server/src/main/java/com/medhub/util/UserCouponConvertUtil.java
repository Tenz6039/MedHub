package com.medhub.util;

import com.medhub.constant.CouponConstant;
import com.medhub.entity.UserCoupon;
import com.medhub.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户优惠券对象转换工具类
 */
public class UserCouponConvertUtil {

    private UserCouponConvertUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * UserCoupon 转 UserCouponVO
     */
    public static UserCouponVO toUserCouponVO(UserCoupon userCoupon) {
        if (userCoupon == null) {
            return null;
        }

        return UserCouponVO.builder()
                .userCouponId(userCoupon.getId())
                .couponId(userCoupon.getCouponId())
                .status(userCoupon.getStatus())
                .statusDescription(CouponConvertUtil.getUserCouponStatusDescription(userCoupon.getStatus()))
                .obtainTime(userCoupon.getObtainTime())
                .useTime(userCoupon.getUseTime())
                .orderId(userCoupon.getOrderId())
                .expireTime(userCoupon.getExpireTime())
                .build();
    }

    /**
     * UserCoupon 转 UserCouponVO（包含优惠券信息）
     */
    public static UserCouponVO toUserCouponVO(UserCoupon userCoupon, String couponName, Integer couponType,
                                               BigDecimal minAmount, BigDecimal discountValue, BigDecimal maxDiscountAmount,
                                               String description) {
        if (userCoupon == null) {
            return null;
        }

        return UserCouponVO.builder()
                .userCouponId(userCoupon.getId())
                .couponId(userCoupon.getCouponId())
                .name(couponName)
                .type(couponType)
                .typeDescription(CouponConvertUtil.getTypeDescription(couponType))
                .minAmount(minAmount)
                .discountValue(discountValue)
                .maxDiscountAmount(maxDiscountAmount)
                .description(description)
                .status(userCoupon.getStatus())
                .statusDescription(CouponConvertUtil.getUserCouponStatusDescription(userCoupon.getStatus()))
                .obtainTime(userCoupon.getObtainTime())
                .useTime(userCoupon.getUseTime())
                .orderId(userCoupon.getOrderId())
                .expireTime(userCoupon.getExpireTime())
                .build();
    }

    /**
     * List<UserCoupon> 转 List<UserCouponVO>
     */
    public static List<UserCouponVO> toUserCouponVOList(List<UserCoupon> userCoupons) {
        if (userCoupons == null || userCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserCouponVO> result = new ArrayList<>(userCoupons.size());
        for (UserCoupon userCoupon : userCoupons) {
            result.add(toUserCouponVO(userCoupon));
        }

        return result;
    }

    /**
     * List<UserCoupon> 转 List<UserCouponVO>（包含优惠券信息和折扣计算）
     * 注意：此方法假设 UserCoupon 对象已经通过 Mapper 关联查询包含了优惠券信息
     */
    public static List<UserCouponVO> toUserCouponVOListWithDiscount(List<UserCoupon> userCoupons, BigDecimal orderAmount) {
        if (userCoupons == null || userCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserCouponVO> result = new ArrayList<>(userCoupons.size());
        for (UserCoupon userCoupon : userCoupons) {
            // 由于 UserCoupon 实体中没有优惠券信息，需要通过额外查询获取
            // 这里简化处理，返回基本 VO，折扣计算在 Service 层完成
            UserCouponVO vo = UserCouponVO.builder()
                    .userCouponId(userCoupon.getId())
                    .couponId(userCoupon.getCouponId())
                    .status(userCoupon.getStatus())
                    .statusDescription(CouponConvertUtil.getUserCouponStatusDescription(userCoupon.getStatus()))
                    .obtainTime(userCoupon.getObtainTime())
                    .useTime(userCoupon.getUseTime())
                    .orderId(userCoupon.getOrderId())
                    .expireTime(userCoupon.getExpireTime())
                    .build();

            result.add(vo);
        }

        return result;
    }

    /**
     * 计算优惠金额
     * @param couponType 优惠券类型
     * @param discountValue 优惠值
     * @param minAmount 满减门槛
     * @param maxDiscountAmount 最大优惠金额
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    public static BigDecimal calculateDiscountAmount(Integer couponType, BigDecimal discountValue,
                                                      BigDecimal minAmount, BigDecimal maxDiscountAmount,
                                                      BigDecimal orderAmount) {
        return CouponConvertUtil.calculateDiscountAmount(couponType, discountValue, minAmount, maxDiscountAmount, orderAmount);
    }
}
