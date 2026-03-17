package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 药品组合总览 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineComboOverviewVO implements Serializable {

    // 已启售药品组合数量
    private Integer enabled;

    // 已停售药品组合数量
    private Integer disabled;

    // 药品组合总数
    private Integer total;
}
