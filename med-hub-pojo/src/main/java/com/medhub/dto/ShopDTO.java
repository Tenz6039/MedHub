package com.medhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 最低起送金额
     */
    private BigDecimal minDeliveryAmount;

    /**
     * 营业时间 - 开始
     */
    private String openTime;

    /**
     * 营业时间 - 结束
     */
    private String closeTime;

    /**
     * 营业状态 0:休息 1:营业
     */
    private Integer status;

    /**
     * 店铺公告
     */
    private String notice;

    /**
     * 店铺描述
     */
    private String description;
}
