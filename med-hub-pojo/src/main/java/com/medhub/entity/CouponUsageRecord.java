package com.medhub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券使用记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 优惠券 ID
     */
    private Long couponId;

    /**
     * 用户优惠券 ID
     */
    private Long userCouponId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 订单 ID
     */
    private Long orderId;

    /**
     * 订单原金额
     */
    private BigDecimal orderAmount;

    /**
     * 优惠券抵扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
