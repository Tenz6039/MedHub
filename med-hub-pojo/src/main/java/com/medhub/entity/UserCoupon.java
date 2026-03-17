package com.medhub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券关联
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon implements Serializable {

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
     * 用户 ID
     */
    private Long userId;

    /**
     * 状态：0-未使用，1-已使用，2-已过期，3-已锁定
     */
    private Integer status;

    /**
     * 领取时间
     */
    private LocalDateTime obtainTime;

    /**
     * 使用时间（订单支付时间）
     */
    private LocalDateTime useTime;

    /**
     * 关联订单 ID（使用时填写）
     */
    private Long orderId;

    /**
     * 过期时间（从优惠券继承，但独立存储以防优惠券修改）
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ========== 以下字段从优惠券表关联查询 ==========
    
    /**
     * 优惠券名称
     */
    private String couponName;
    
    /**
     * 优惠券类型：1-满减券，2-折扣券
     */
    private Integer couponType;
    
    /**
     * 使用门槛（满多少元可用）
     */
    private java.math.BigDecimal minAmount;
    
    /**
     * 优惠金额（折扣券为折扣比例）
     */
    private java.math.BigDecimal discountValue;
    
    /**
     * 最大优惠金额（折扣券专用）
     */
    private java.math.BigDecimal maxDiscountAmount;
    
    /**
     * 优惠券说明
     */
    private String description;
    
    /**
     * 用户限领数量（-1 表示不限）
     */
    private Integer userLimit;
    
    /**
     * 优惠券结束时间
     */
    private LocalDateTime endTime;
}
