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
 * 优惠券 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券 ID
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
     * 发行总量，-1 表示不限量
     */
    private Integer totalCount;

    /**
     * 已领取数量
     */
    private Integer issuedCount;

    /**
     * 剩余数量
     */
    private Integer remainCount;

    /**
     * 单用户限领数量，-1 表示不限
     */
    private Integer userLimit;

    /**
     * 有效期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人 ID
     */
    private Long createUser;

    /**
     * 修改人 ID
     */
    private Long updateUser;
}
