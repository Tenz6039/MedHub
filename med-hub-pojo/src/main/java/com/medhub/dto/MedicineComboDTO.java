package com.medhub.dto;

import com.medhub.entity.MedicineComboMedicine;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class MedicineComboDTO implements Serializable {

    private Long id;

    private Long categoryId;

    private String name;

    private BigDecimal price;

    private Integer status;

    private String description;

    private String image;

    private List<MedicineComboMedicine> medicineComboMedicines = new ArrayList<>();

}
