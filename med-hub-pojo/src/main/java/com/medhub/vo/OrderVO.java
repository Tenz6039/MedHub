package com.medhub.vo;

import com.medhub.entity.OrderDetail;
import com.medhub.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单药品信息
    private String orderMedicines;

    //订单详情
    private List<OrderDetail> orderDetailList;

}
