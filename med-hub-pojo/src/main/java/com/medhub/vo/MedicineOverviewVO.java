package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 药品总览 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineOverviewVO implements Serializable {

    // 已启售药品数量
    private Integer enabled;

    // 已停售药品数量
    private Integer disabled;

    // 药品总数
    private Integer total;
}
