package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券应用结果 VO
 * 用于返回优惠券应用到订单后的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponApplyResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否应用成功
     */
    private Boolean success;

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
     * 订单原金额（优惠券应用前）
     */
    private BigDecimal originalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 最终支付金额
     */
    private BigDecimal finalAmount;

    /**
     * 优惠说明
     */
    private String discountDescription;

    /**
     * 优惠券锁定状态：true-已锁定，false-未锁定
     * 锁定表示优惠券正在被该订单使用，防止重复使用
     */
    private Boolean locked;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 失败原因（当 success=false 时填写）
     */
    private String failReason;

    /**
     * 错误码
     */
    private String errorCode;
}
