package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineItemVO implements Serializable {

    private String name;

    private Integer copies;

    private String image;

    private String description;
}
