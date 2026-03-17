package com.medhub.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private Long medicineId;
    private Long comboId;
    private String medicineSpec;

}
