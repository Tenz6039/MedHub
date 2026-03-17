package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券验证结果 VO
 * 用于返回优惠券的有效性验证信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidateResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户优惠券 ID
     */
    private Long userCouponId;

    /**
     * 优惠券 ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型：1-满减券，2-打折券
     */
    private Integer couponType;

    /**
     * 优惠券类型描述
     */
    private String typeDescription;

    /**
     * 优惠规则描述（如"满 30 减 5"、"8.5 折"）
     */
    private String ruleDescription;

    /**
     * 满减门槛（元），0 表示无门槛
     */
    private BigDecimal minAmount;

    /**
     * 优惠金额或折扣率
     * 满减券：减免金额
     * 打折券：折扣率（如 0.85 表示 85 折）
     */
    private BigDecimal discountValue;

    /**
     * 最大优惠金额（仅打折券）
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 无效原因（当 valid=false 时填写）
     */
    private String invalidReason;

    /**
     * 优惠券状态：0-未使用，1-已使用，2-已过期，3-已锁定
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 有效期开始时间
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否即将过期（7 天内）
     */
    private Boolean expiringSoon;
}
