package com.medhub.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WebSocket 消息 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageVO implements Serializable {
    //消息类型 1:新订单 2:催单
    private Integer type;
    //消息内容
    private String content;
    //订单 ID
    private Long orderId;
    //订单号
    private String orderNumber;
}
