package com.medhub.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券创建 DTO
 */
@Data
public class CouponCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 单用户限领数量，-1 表示不限
     */
    private Integer userLimit;

    /**
     * 有效期开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
