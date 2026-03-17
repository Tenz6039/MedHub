package com.medhub.task;

import com.medhub.entity.Orders;
import com.medhub.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 检查超时订单
     */
    //每分钟触发一次
    @Scheduled(cron = "0 * * * * ?")
    public void checkTimeoutOrder() {
        log.info("检查超时订单");
        //查询状态为待付款，且下单时间超过15分钟的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));
        if(ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(order -> {
                log.info("超时订单：{}", order);
                //更新订单状态为已取消
                order.setStatus(Orders.CANCELLED);
                orderMapper.update(order);
            });
        }

    }

    /**
     * 处理一直处于派送中状态的订单
     */
    //每天凌晨4点触发一次
    @Scheduled(cron = "0 0 4 * * ?")
    public void processDeliveredOrder() {
        log.info("处理一直处于派送中状态的订单");
        //查询状态为派送中，且配送时间超过1小时的订单

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusHours(1));
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                log.info("一直处于派送中状态的订单：{}", order);
                //更新订单状态为已完成
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }

        }
    }
}
