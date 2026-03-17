package com.medhub.util;

import com.medhub.constant.CouponConstant;
import com.medhub.dto.CouponCreateDTO;
import com.medhub.dto.CouponEditDTO;
import com.medhub.entity.Coupon;
import com.medhub.entity.UserCoupon;
import com.medhub.vo.CouponStatisticsVO;
import com.medhub.vo.CouponTop10VO;
import com.medhub.vo.CouponVO;
import com.medhub.vo.UserCouponVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券对象转换工具类
 */
public class CouponConvertUtil {

    private CouponConvertUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * CouponCreateDTO 转 Coupon
     */
    public static Coupon toCoupon(CouponCreateDTO dto) {
        return Coupon.builder()
                .name(dto.getName())
                .type(dto.getType())
                .minAmount(dto.getMinAmount())
                .discountValue(dto.getDiscountValue())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .description(dto.getDescription())
                .totalCount(dto.getTotalCount())
                .userLimit(dto.getUserLimit())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(CouponConstant.COUPON_STATUS_ENABLED)
                .build();
    }

    /**
     * Coupon 转 CouponVO
     */
    public static CouponVO toCouponVO(Coupon coupon) {
        if (coupon == null) {
            return null;
        }

        CouponVO.CouponVOBuilder builder = CouponVO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .type(coupon.getType())
                .typeDescription(getTypeDescription(coupon.getType()))
                .minAmount(coupon.getMinAmount())
                .discountValue(coupon.getDiscountValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .description(coupon.getDescription())
                .totalCount(coupon.getTotalCount())
                .issuedCount(coupon.getIssuedCount())
                .remainCount(calculateRemainCount(coupon))
                .userLimit(coupon.getUserLimit())
                .startTime(coupon.getStartTime())
                .endTime(coupon.getEndTime())
                .status(coupon.getStatus())
                .statusDescription(getStatusDescription(coupon.getStatus()))
                .createTime(coupon.getCreateTime())
                .updateTime(coupon.getUpdateTime())
                .createUser(coupon.getCreateUser())
                .updateUser(coupon.getUpdateUser());

        return builder.build();
    }

    /**
     * UserCoupon 转 UserCouponVO
     */
    public static UserCouponVO toUserCouponVO(UserCoupon userCoupon, String couponName, Integer couponType) {
        if (userCoupon == null) {
            return null;
        }

        return UserCouponVO.builder()
                .userCouponId(userCoupon.getId())
                .couponId(userCoupon.getCouponId())
                .name(couponName)
                .type(couponType)
                .typeDescription(getTypeDescription(couponType))
                .status(userCoupon.getStatus())
                .statusDescription(getUserCouponStatusDescription(userCoupon.getStatus()))
                .obtainTime(userCoupon.getObtainTime())
                .useTime(userCoupon.getUseTime())
                .orderId(userCoupon.getOrderId())
                .expireTime(userCoupon.getExpireTime())
                .build();
    }

    /**
     * CouponEditDTO 更新 Coupon
     */
    public static void updateCoupon(Coupon coupon, CouponEditDTO dto) {
        if (coupon == null || dto == null) {
            return;
        }

        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setTotalCount(dto.getTotalCount());
        coupon.setUserLimit(dto.getUserLimit());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        coupon.setStatus(dto.getStatus());
        coupon.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 计算剩余数量
     */
    public static Integer calculateRemainCount(Coupon coupon) {
        if (coupon == null) {
            return null;
        }

        // 不限量
        if (CouponConstant.UNLIMITED_COUNT.equals(coupon.getTotalCount())) {
            return CouponConstant.UNLIMITED_COUNT;
        }

        // 空值检查，将 null 当作 0 处理
        Integer totalCount = coupon.getTotalCount();
        Integer issuedCount = coupon.getIssuedCount() != null ? coupon.getIssuedCount() : 0;
        
        if (totalCount == null) {
            return null;
        }

        return totalCount - issuedCount;
    }

    /**
     * 获取优惠券类型描述
     */
    public static String getTypeDescription(Integer type) {
        if (type == null) {
            return "";
        }

        if (CouponConstant.COUPON_TYPE_DISCOUNT.equals(type)) {
            return "满减券";
        } else if (CouponConstant.COUPON_TYPE_PERCENT_OFF.equals(type)) {
            return "打折券";
        }

        return "未知类型";
    }

    /**
     * 获取优惠券状态描述
     */
    public static String getStatusDescription(Integer status) {
        if (status == null) {
            return "";
        }

        if (CouponConstant.COUPON_STATUS_ENABLED.equals(status)) {
            return "启用";
        } else if (CouponConstant.COUPON_STATUS_DISABLED.equals(status)) {
            return "禁用";
        }

        return "未知状态";
    }

    /**
     * 获取用户优惠券状态描述
     */
    public static String getUserCouponStatusDescription(Integer status) {
        if (status == null) {
            return "";
        }

        switch (status) {
            case 0:
                return "未使用";
            case 1:
                return "已使用";
            case 2:
                return "已过期";
            case 3:
                return "使用中";
            default:
                return "未知状态";
        }
    }

    /**
     * 计算优惠金额
     *
     * @param couponType 优惠券类型
     * @param discountValue 优惠值（金额或折扣率）
     * @param minAmount 满减门槛
     * @param maxDiscountAmount 最大优惠金额
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    public static BigDecimal calculateDiscountAmount(Integer couponType, BigDecimal discountValue,
                                                      BigDecimal minAmount, BigDecimal maxDiscountAmount,
                                                      BigDecimal orderAmount) {
        if (couponType == null || discountValue == null || orderAmount == null) {
            return BigDecimal.ZERO;
        }

        // 检查是否满足满减门槛
        if (CouponConstant.COUPON_TYPE_DISCOUNT.equals(couponType)) {
            if (minAmount != null && orderAmount.compareTo(minAmount) < 0) {
                return BigDecimal.ZERO;
            }
            // 满减券：直接返回优惠金额
            return discountValue.min(orderAmount);
        } else if (CouponConstant.COUPON_TYPE_PERCENT_OFF.equals(couponType)) {
            // 打折券：订单金额 * 折扣率
            BigDecimal discountAmount = orderAmount.multiply(discountValue);

            // 如果有最大优惠限制，取较小值
            if (maxDiscountAmount != null) {
                discountAmount = discountAmount.min(maxDiscountAmount);
            }

            return discountAmount.min(orderAmount);
        }

        return BigDecimal.ZERO;
    }

    /**
     * List<Coupon> 转 List<CouponVO>
     */
    public static List<CouponVO> toCouponVOList(List<Coupon> coupons) {
        if (coupons == null || coupons.isEmpty()) {
            return new ArrayList<>();
        }

        List<CouponVO> result = new ArrayList<>(coupons.size());
        for (Coupon coupon : coupons) {
            result.add(toCouponVO(coupon));
        }

        return result;
    }

    /**
     * 创建优惠券统计 VO
     */
    public static CouponStatisticsVO toCouponStatisticsVO(Integer totalCoupons, Integer activeCoupons,
                                                           Integer disabledCoupons, Integer totalIssued,
                                                           Integer totalUsed, BigDecimal totalDiscountAmount) {
        Double usageRate = 0.0;
        if (totalIssued != null && totalIssued > 0) {
            usageRate = (double) totalUsed / totalIssued * 100;
        }

        return CouponStatisticsVO.builder()
                .totalCoupons(totalCoupons != null ? totalCoupons : 0)
                .activeCoupons(activeCoupons != null ? activeCoupons : 0)
                .disabledCoupons(disabledCoupons != null ? disabledCoupons : 0)
                .totalIssued(totalIssued != null ? totalIssued : 0)
                .totalUsed(totalUsed != null ? totalUsed : 0)
                .totalDiscountAmount(totalDiscountAmount != null ? totalDiscountAmount : BigDecimal.ZERO)
                .usageRate(usageRate)
                .build();
    }

    /**
     * 创建优惠券 TOP10 VO
     */
    public static CouponTop10VO toCouponTop10VO(Long couponId, String couponName, Integer issuedCount,
                                                 Integer usedCount, BigDecimal discountAmount) {
        Double usageRate = 0.0;
        if (issuedCount != null && issuedCount > 0) {
            usageRate = (double) usedCount / issuedCount * 100;
        }

        return CouponTop10VO.builder()
                .couponId(couponId)
                .couponName(couponName)
                .issuedCount(issuedCount != null ? issuedCount : 0)
                .usedCount(usedCount != null ? usedCount : 0)
                .discountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO)
                .usageRate(usageRate)
                .build();
    }
}
