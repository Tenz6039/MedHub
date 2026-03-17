package com.medhub.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户优惠券分页查询 DTO
 */
@Data
public class UserCouponQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 优惠券状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;
}
