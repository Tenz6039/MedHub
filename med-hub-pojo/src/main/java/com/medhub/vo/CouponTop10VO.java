package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券 TOP10 统计 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTop10VO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券 ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 已领取数量
     */
    private Integer issuedCount;

    /**
     * 已使用数量
     */
    private Integer usedCount;

    /**
     * 优惠总金额
     */
    private BigDecimal discountAmount;

    /**
     * 使用率（百分比）
     */
    private Double usageRate;
}
