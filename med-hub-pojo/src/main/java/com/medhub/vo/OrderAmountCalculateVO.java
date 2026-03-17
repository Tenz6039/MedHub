package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单金额计算结果 VO
 * 用于返回优惠券应用后的订单金额详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAmountCalculateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品总金额（优惠券计算前）
     */
    private BigDecimal goodsAmount;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 打包费
     */
    private BigDecimal packageFee;

    /**
     * 优惠前总金额（goodsAmount + deliveryFee + packageFee）
     */
    private BigDecimal totalAmount;

    /**
     * 使用的优惠券 ID
     */
    private Long userCouponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型：1-满减券，2-打折券
     */
    private Integer couponType;

    /**
     * 优惠金额（优惠券减免的金额）
     */
    private BigDecimal discountAmount;

    /**
     * 最终支付金额（totalAmount - discountAmount）
     */
    private BigDecimal payAmount;

    /**
     * 计算说明（用于前端展示优惠明细）
     */
    private String calculationDescription;
}
