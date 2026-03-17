package com.medhub.service;

import com.medhub.dto.OrdersCancelDTO;
import com.medhub.dto.OrdersConfirmDTO;
import com.medhub.dto.OrdersPageQueryDTO;
import com.medhub.dto.OrdersPaymentDTO;
import com.medhub.dto.OrdersRejectionDTO;
import com.medhub.dto.OrdersSubmitDTO;
import com.medhub.result.PageResult;
import com.medhub.vo.OrderPaymentVO;
import com.medhub.vo.OrderStatisticsVO;
import com.medhub.vo.OrderSubmitVO;
import com.medhub.vo.OrderVO;

public interface OrderService {
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    
    /**
     * 生成支付参数
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);
    
    /**
     * 处理支付结果
     * @param outTradeNo
     * @return
     */
    public void paySuccess(String outTradeNo);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO statistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    public void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    public void complete(Long id);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    public OrderVO getOrderDetail(Long id);

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult historyOrders(Integer page, Integer pageSize, Integer status);

    /**
     * 用户取消订单
     * @param id
     */
    public void userCancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     */
    public void repetition(Long id);

    /**
     * 催单
     * @param id
     */
    public void reminder(Long id);
}
