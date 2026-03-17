package com.medhub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long medicineId;

    private String name;

    private String value;

}
