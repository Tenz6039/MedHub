package com.medhub.service.impl;

import com.medhub.constant.MessageConstant;
import com.medhub.context.BaseContext;
import com.medhub.dto.OrdersCancelDTO;
import com.medhub.dto.OrdersConfirmDTO;
import com.medhub.dto.OrdersPageQueryDTO;
import com.medhub.dto.OrdersPaymentDTO;
import com.medhub.dto.OrdersRejectionDTO;
import com.medhub.dto.OrdersSubmitDTO;
import com.medhub.entity.AddressBook;
import com.medhub.entity.Coupon;
import com.medhub.entity.OrderDetail;
import com.medhub.entity.Orders;
import com.medhub.entity.ShoppingCart;
import com.medhub.entity.UserCoupon;
import com.medhub.exception.AddressBookBusinessException;
import com.medhub.exception.ShoppingCartBusinessException;
import com.medhub.mapper.AddressBookMapper;
import com.medhub.mapper.CouponMapper;
import com.medhub.mapper.OrderDetailMapper;
import com.medhub.mapper.OrderMapper;
import com.medhub.mapper.ShopMapper;
import com.medhub.mapper.ShoppingCartMapper;
import com.medhub.mapper.UserCouponMapper;
import com.medhub.result.PageResult;
import com.medhub.service.OrderService;
import com.medhub.service.UserCouponService;
import com.medhub.vo.OrderPaymentVO;
import com.medhub.vo.OrderStatisticsVO;
import com.medhub.vo.OrderSubmitVO;
import com.medhub.vo.OrderVO;
import com.medhub.vo.WebSocketMessageVO;
import com.medhub.websocket.WebSocketServer;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理异常情况（地址簿和购物车不能为空）
        //判断地址簿是否存在
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        log.info("前端传递的地址簿 ID：{}", addressBookId);
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            log.error("地址簿不存在，ID：{}", addressBookId);
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        log.info("查询到的地址簿信息：ID={}, 姓名={}, 性别={}, 电话={}", 
                addressBook.getId(), addressBook.getConsignee(), addressBook.getSex(), addressBook.getPhone());
        //判断购物车是否为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 获取店铺信息（获取默认店铺，ID=1）
        com.medhub.entity.Shop shop = shopMapper.getById(1L);
        if (shop == null) {
            log.warn("店铺信息不存在，使用默认配送费 0");
        } else {
            log.info("店铺信息：ID={}, 名称={}, 配送费={}", shop.getId(), shop.getName(), shop.getDeliveryFee());
        }

        //向订单表插入一条记录
        Orders orders= new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        log.info("前端传递的参数：amount={}, deliveryFee={}, packAmount={}", 
                orders.getAmount(), ordersSubmitDTO.getDeliveryFee(), ordersSubmitDTO.getPackAmount());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        log.info("地址簿性别：{}", addressBook.getSex());
        orders.setSex(addressBook.getSex()); // 从地址簿复制性别
        log.info("订单设置的性别：{}", orders.getSex());
        orders.setUserId(userId);
        // 设置完整地址
        String address = addressBook.getProvinceName() + addressBook.getCityName() + 
                        addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);
        // 设置店铺 ID
        orders.setShopId(shop != null ? shop.getId() : 1L);
        // 设置配送费（从店铺信息中获取，覆盖前端传递的值）
        BigDecimal deliveryFee = shop != null ? shop.getDeliveryFee() : java.math.BigDecimal.ZERO;
        orders.setDeliveryFee(deliveryFee);
        log.info("店铺配送费设置：deliveryFee={}", deliveryFee);
        // 设置默认包装状态和数量（如果前端未传递）
        if (orders.getPackageStatus() == null) {
            orders.setPackageStatus(1); // 默认按药量提供
        }
        if (orders.getPackageNumber() == 0) {
            orders.setPackageNumber(1); // 默认 1 个包装
        }

        // 先插入订单，获取订单 ID
        orderMapper.insert(orders);

        // 处理优惠券逻辑（必须在订单插入后，因为需要订单 ID）
        if (ordersSubmitDTO.getCouponId() != null) {
            // 查询用户优惠券
            UserCoupon userCoupon = userCouponMapper.getById(ordersSubmitDTO.getCouponId());
            if (userCoupon == null) {
                throw new RuntimeException("优惠券不存在");
            }
            
            // 验证优惠券所有权
            if (!userCoupon.getUserId().equals(userId)) {
                throw new RuntimeException("优惠券信息异常");
            }
            
            // 验证优惠券状态
            if (!Integer.valueOf(0).equals(userCoupon.getStatus())) {
                throw new RuntimeException("优惠券已使用或已过期");
            }
            
            // 设置优惠券信息到订单
            orders.setCouponId(userCoupon.getCouponId());
            orders.setCouponDiscount(ordersSubmitDTO.getCouponDiscount());
            // 更新订单表中的优惠券信息
            orderMapper.update(orders);
            
            // 锁定优惠券（状态改为 3-已锁定）
            userCoupon.setStatus(3);
            userCoupon.setOrderId(orders.getId());
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.update(userCoupon);
        }

        // 批量插入订单明细
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装 VO 返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        
        // 注意：不在这里发送 WebSocket 通知，因为用户尚未支付
        // WebSocket 通知将在支付成功回调中发送
        
       return orderSubmitVO;
    }

    /**
     * 生成支付参数（伪支付）
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 生成伪支付参数
        OrderPaymentVO orderPaymentVO = OrderPaymentVO.builder()
                .nonceStr(UUID.randomUUID().toString())
                .paySign("fake_pay_sign")
                .timeStamp(String.valueOf(System.currentTimeMillis() / 1000))
                .signType("MD5")
                .packageStr("prepay_id=fake_prepay_id")
                .build();
        
        // 注意：不在这里调用 paySuccess，应该等前端支付完成后再调用
        // paySuccess 应该由前端在支付成功后主动调用
        
        return orderPaymentVO;
    }

    /**
     * 处理支付结果（伪支付）
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        log.info("========== 支付成功回调开始，订单号：{} ==========", outTradeNo);
        
        // 根据订单号查询订单
        Orders orders = orderMapper.getByNumber(outTradeNo);
        if (orders == null) {
            log.error("订单不存在：{}", outTradeNo);
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        log.info("查询到订单信息：ID={}, 订单号={}, 当前状态={}, 支付状态={}", 
                orders.getId(), orders.getNumber(), orders.getStatus(), orders.getPayStatus());
        
        // 更新订单状态为已支付
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(orders);
        
        log.info("订单状态已更新：新状态={}, 新支付状态={}", orders.getStatus(), orders.getPayStatus());
        
        // 如果订单使用了优惠券，将优惠券状态从锁定改为已使用
        if (orders.getCouponId() != null) {
            log.info("订单使用了优惠券，准备更新优惠券状态：couponId={}", orders.getCouponId());
            
            // 查询用户优惠券
            UserCoupon userCoupon = getUserCouponByOrderId(orders.getId());
            if (userCoupon != null && userCoupon.getStatus() == 3) {
                // 更新优惠券状态为已使用
                userCoupon.setStatus(1);
                userCoupon.setUseTime(LocalDateTime.now());
                userCoupon.setOrderId(orders.getId());
                userCoupon.setUpdateTime(LocalDateTime.now());
                userCouponMapper.update(userCoupon);
                
                log.info("优惠券状态已更新为已使用：userCouponId={}, orderId={}", 
                        userCoupon.getId(), orders.getId());
                
                // 记录优惠券使用记录
                recordCouponUsage(orders, userCoupon);
            }
        }
        
        // 支付成功后，通过 WebSocket 向管理员发送新订单通知
        WebSocketMessageVO message = WebSocketMessageVO.builder()
                .type(1) // 1:新订单（已支付）
                .content("新订单提醒：订单号 " + orders.getNumber() + "，金额 " + orders.getAmount() + " 元")
                .orderId(orders.getId())
                .orderNumber(orders.getNumber())
                .build();
        
        log.info("发送 WebSocket 通知：{}", JSON.toJSONString(message));
        webSocketServer.sendToAllClient(JSON.toJSONString(message));
        
        log.info("========== 支付成功回调结束 ==========");
    }
    
    /**
     * 根据订单 ID 查询用户优惠券
     * @param orderId 订单 ID
     * @return 用户优惠券
     */
    private UserCoupon getUserCouponByOrderId(Long orderId) {
        // 查询该订单关联的优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setOrderId(orderId);
        List<UserCoupon> list = userCouponMapper.listByOrderId(orderId);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 记录优惠券使用记录
     * @param orders 订单
     * @param userCoupon 用户优惠券
     */
    private void recordCouponUsage(Orders orders, UserCoupon userCoupon) {
        try {
            // 查询优惠券模板信息
            Coupon coupon = couponMapper.getById(userCoupon.getCouponId());
            if (coupon == null) {
                log.warn("优惠券模板不存在：{}", userCoupon.getCouponId());
                return;
            }
            
            // 创建使用记录
            com.medhub.entity.CouponUsageRecord usageRecord = com.medhub.entity.CouponUsageRecord.builder()
                    .couponId(coupon.getId())
                    .userCouponId(userCoupon.getId())
                    .userId(orders.getUserId())
                    .orderId(orders.getId())
                    .orderAmount(orders.getAmount().add(orders.getCouponDiscount() != null ? orders.getCouponDiscount() : BigDecimal.ZERO))
                    .discountAmount(orders.getCouponDiscount() != null ? orders.getCouponDiscount() : BigDecimal.ZERO)
                    .actualAmount(orders.getAmount())
                    .useTime(LocalDateTime.now())
                    .createTime(LocalDateTime.now())
                    .build();
            
            // 插入使用记录（假设有对应的 mapper）
            // couponUsageRecordMapper.insert(usageRecord);
            log.info("优惠券使用记录：couponId={}, userCouponId={}, orderId={}, discountAmount={}", 
                    coupon.getId(), userCoupon.getId(), orders.getId(), orders.getCouponDiscount());
        } catch (Exception e) {
            log.error("记录优惠券使用记录失败：{}", e.getMessage());
        }
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        List<Orders> ordersList = orderMapper.conditionSearch(ordersPageQueryDTO);
        Integer count = orderMapper.countByCondition(ordersPageQueryDTO);
        return new PageResult(count, ordersList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult historyOrders(Integer page, Integer pageSize, Integer status) {
        Long userId = BaseContext.getCurrentId();
        List<Orders> ordersList = orderMapper.historyOrders(page, pageSize, status, userId);
        Integer total = orderMapper.countHistoryOrders(status, userId);
        
        // 将Orders列表转换为OrderVO列表
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            // 查询订单详情
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }
        
        return new PageResult(total, orderVOList);
    }

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void userCancelOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        
        // 如果订单使用了优惠券且处于锁定状态，解锁优惠券
        if (orders.getCouponId() != null) {
            log.info("订单取消，准备解锁优惠券：orderId={}, couponId={}", id, orders.getCouponId());
            
            UserCoupon userCoupon = getUserCouponByOrderId(id);
            if (userCoupon != null) {
                log.info("查询到用户优惠券：userCouponId={}, 当前状态={}", 
                        userCoupon.getId(), userCoupon.getStatus());
                
                // 只有状态为 3（已锁定）的优惠券才能解锁
                if (userCoupon.getStatus() == 3) {
                    // 解锁优惠券（状态改回 0-未使用）
                    userCoupon.setStatus(0);
                    userCoupon.setOrderId(null);
                    userCoupon.setUpdateTime(LocalDateTime.now());
                    userCouponMapper.update(userCoupon);
                    
                    log.info("优惠券已解锁：userCouponId={}, 新状态=0", userCoupon.getId());
                } else {
                    log.warn("优惠券状态不是锁定状态，无需解锁：userCouponId={}, 当前状态={}", 
                            userCoupon.getId(), userCoupon.getStatus());
                }
            } else {
                log.warn("未查询到订单关联的用户优惠券：orderId={}", id);
            }
        }
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }
        
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        if (!orders.getUserId().equals(userId)) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        
        // 通过 WebSocket 向后台服务端发送催单提醒
        WebSocketMessageVO message = WebSocketMessageVO.builder()
                .type(2) // 2:催单
                .content("订单 " + orders.getNumber() + " 催单提醒")
                .orderId(orders.getId())
                .orderNumber(orders.getNumber())
                .build();
        webSocketServer.sendToAllClient(JSON.toJSONString(message));
    }
}
