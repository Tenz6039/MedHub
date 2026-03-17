package com.medhub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：1-满减券，2-打折券
     */
    private Integer type;

    /**
     * 满减条件（元），0 表示无门槛
     */
    private BigDecimal minAmount;

    /**
     * 优惠金额/折扣率
     * 满减券：减免的金额（元）
     * 打折券：折扣率（如 0.85 表示 85 折）
     */
    private BigDecimal discountValue;

    /**
     * 最大优惠金额（仅打折券）
     * 防止高金额订单折扣过大
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 发行总量，-1 表示不限量
     */
    private Integer totalCount;

    /**
     * 已领取数量
     */
    private Integer issuedCount;

    /**
     * 单用户限领数量，-1 表示不限
     */
    private Integer userLimit;

    /**
     * 有效期开始时间
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人 ID（管理员）
     */
    private Long createUser;

    /**
     * 修改人 ID（管理员）
     */
    private Long updateUser;
}
