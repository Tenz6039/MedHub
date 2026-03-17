package com.medhub.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponVO implements Serializable {

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
    private String name;

    /**
     * 优惠券类型：1-满减券，2-打折券
     */
    private Integer type;

    /**
     * 类型描述
     */
    private String typeDescription;

    /**
     * 满减条件（元），0 表示无门槛
     */
    private BigDecimal minAmount;

    /**
     * 优惠金额/折扣率
     */
    private BigDecimal discountValue;

    /**
     * 最大优惠金额（仅打折券）
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 状态：0-未使用，1-已使用，2-已过期，3-已锁定
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 领取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime obtainTime;

    /**
     * 使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;

    /**
     * 关联订单 ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 预估优惠金额（用于下单页展示）
     */
    private BigDecimal discountAmount;

    /**
     * 使用优惠券后预估金额（用于下单页展示）
     */
    private BigDecimal actualAmount;
    
    /**
     * 用户限领数量（-1 表示不限）
     */
    private Integer userLimit;
    
    /**
     * 优惠券结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
