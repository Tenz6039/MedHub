package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券统计 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券总数
     */
    private Integer totalCoupons;

    /**
     * 启用中的优惠券数量
     */
    private Integer activeCoupons;

    /**
     * 禁用中的优惠券数量
     */
    private Integer disabledCoupons;

    /**
     * 总发行数量
     */
    private Integer totalIssued;

    /**
     * 总使用数量
     */
    private Integer totalUsed;

    /**
     * 总优惠金额
     */
    private BigDecimal totalDiscountAmount;

    /**
     * 优惠券使用率（百分比）
     */
    private Double usageRate;
}
