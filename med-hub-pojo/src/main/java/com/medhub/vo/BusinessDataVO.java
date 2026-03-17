package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 营业数据 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO implements Serializable {

    // 营业额
    private BigDecimal turnover;

    // 有效订单数
    private Integer validOrderCount;

    // 订单完成率
    private BigDecimal orderCompletionRate;

    // 平均客单价
    private BigDecimal averageCustomerPrice;

    // 新增用户数
    private Integer newUserCount;
}
