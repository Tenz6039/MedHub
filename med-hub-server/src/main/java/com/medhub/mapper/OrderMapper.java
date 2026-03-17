package com.medhub.mapper;

import com.medhub.entity.Orders;
import com.medhub.dto.OrdersPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     * @param orders
     */
    void insert(Orders orders);
    
    /**
     * 根据订单号查询订单
     * @param number
     * @return
     */
    Orders getByNumber(String number);
    
    /**
     * 更新订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单搜索总数
     * @param ordersPageQueryDTO
     * @return
     */
    Integer countByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据ID查询订单
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 统计各个状态的订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 查询用户历史订单
     * @param page
     * @param pageSize
     * @param status
     * @param userId
     * @return
     */
    List<Orders> historyOrders(Integer page, Integer pageSize, Integer status, Long userId);

    /**
     * 查询用户历史订单总数
     * @param status
     * @param userId
     * @return
     */
    Integer countHistoryOrders(Integer status, Long userId);
}
