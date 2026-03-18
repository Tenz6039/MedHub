package com.medhub.controller.user;

import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.dto.UserCouponQueryDTO;
import com.medhub.result.PageResult;
import com.medhub.result.Result;
import com.medhub.service.UserCouponService;
import com.medhub.vo.CouponApplyResultVO;
import com.medhub.vo.CouponValidateResultVO;
import com.medhub.vo.OrderAmountCalculateVO;
import com.medhub.vo.UserCouponVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户优惠券 Controller
 */
@RestController("userCouponController")
@RequestMapping("/user/coupon")
@Slf4j
@Api(tags = "用户优惠券相关接口")
public class UserCouponController {

    @Autowired
    private UserCouponService userCouponService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 可领取优惠券列表
     * @param couponPageQueryDTO 分页查询条件
     * @return 分页结果
     */
    @GetMapping("/available")
    @ApiOperation("可领取优惠券列表")
    public Result<PageResult> availableList(CouponPageQueryDTO couponPageQueryDTO) {
        String redisKey = "coupon_available_list_" + couponPageQueryDTO.getPage() + "_" + couponPageQueryDTO.getPageSize();
        
        PageResult pageResult = null;
        try {
            pageResult = (PageResult) redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            redisTemplate.delete(redisKey);
            log.warn("Redis 缓存反序列化失败，已删除缓存：{}", redisKey);
            pageResult = null;
        }
        
        if (pageResult != null && pageResult.getRecords() != null && !pageResult.getRecords().isEmpty()) {
            log.info("从 Redis 缓存获取可领取优惠券列表：{}", redisKey);
            return Result.success(pageResult);
        }
        
        log.info("从数据库查询可领取优惠券列表：{}", couponPageQueryDTO);
        pageResult = userCouponService.availableList(couponPageQueryDTO);
    
        if (pageResult != null && pageResult.getRecords() != null && !pageResult.getRecords().isEmpty()) {
            try {
                redisTemplate.opsForValue().set(redisKey, pageResult);
                log.info("可领取优惠券列表缓存到 Redis 成功：{}", redisKey);
            } catch (Exception e) {
                log.error("Redis 缓存失败：{}", e.getMessage());
            }
        }
        
        return Result.success(pageResult);
    }

    /**
     * 领取优惠券
     * @param couponId 优惠券 ID
     * @return 用户优惠券 ID
     */
    @PostMapping("/obtain/{couponId}")
    @ApiOperation("领取优惠券")
    public Result<Long> obtain(@PathVariable Long couponId) {
        log.info("领取优惠券：couponId={}", couponId);
        Long userCouponId = userCouponService.obtain(couponId);
        return Result.success(userCouponId);
    }

    /**
     * 我的优惠券列表
     * @param userCouponQueryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/my")
    @ApiOperation("我的优惠券列表")
    public Result<PageResult> myCoupons(UserCouponQueryDTO userCouponQueryDTO) {
        log.info("我的优惠券列表：{}", userCouponQueryDTO);
        PageResult pageResult = userCouponService.myCoupons(userCouponQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 下单可用优惠券列表
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    @GetMapping("/available-for-order")
    @ApiOperation("下单可用优惠券列表")
    public Result<List<UserCouponVO>> getAvailableForOrder(@RequestParam BigDecimal orderAmount) {
        log.info("下单可用优惠券列表：orderAmount={}", orderAmount);
        List<UserCouponVO> voList = userCouponService.getAvailableForOrder(orderAmount);
        return Result.success(voList);
    }

    /**
     * 验证优惠券有效性
     * @param userCouponId 用户优惠券 ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    @GetMapping("/validate")
    @ApiOperation("验证优惠券有效性")
    public Result<CouponValidateResultVO> validateCoupon(
        @RequestParam Long userCouponId,
        @RequestParam BigDecimal orderAmount
    ) {
        log.info("验证优惠券有效性：userCouponId={}, orderAmount={}", userCouponId, orderAmount);
        CouponValidateResultVO result = userCouponService.validateCoupon(userCouponId, orderAmount);
        return Result.success(result);
    }

    /**
     * 订单金额计算
     * @param orderAmount 订单商品总金额
     * @param userCouponId 用户优惠券 ID
     * @return 订单金额计算结果
     */
    @PostMapping("/calculate")
    @ApiOperation("订单金额计算")
    public Result<OrderAmountCalculateVO> calculateOrderAmount(
        @RequestBody Map<String, Object> params
    ) {
        log.info("订单金额计算，接收参数：{}", params);
        
        Object orderAmountObj = params.get("orderAmount");
        Object userCouponIdObj = params.get("userCouponId");
        
        if (orderAmountObj == null || userCouponIdObj == null) {
            log.error("参数缺失：orderAmount={}, userCouponId={}", orderAmountObj, userCouponIdObj);
            return Result.error("参数缺失");
        }
        
        BigDecimal orderAmount = new BigDecimal(orderAmountObj.toString());
        Long userCouponId = Long.parseLong(userCouponIdObj.toString());
        
        log.info("订单金额计算：orderAmount={}, userCouponId={}", orderAmount, userCouponId);
        OrderAmountCalculateVO result = userCouponService.calculateOrderAmount(orderAmount, userCouponId);
        return Result.success(result);
    }

    /**
     * 应用优惠券到订单
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     * @param orderAmount 订单金额
     * @return 应用结果
     */
    @PostMapping("/apply")
    @ApiOperation("应用优惠券到订单")
    public Result<CouponApplyResultVO> applyCoupon(
        @RequestParam Long userCouponId,
        @RequestParam Long orderId,
        @RequestParam BigDecimal orderAmount
    ) {
        log.info("应用优惠券到订单：userCouponId={}, orderId={}, orderAmount={}", userCouponId, orderId, orderAmount);
        CouponApplyResultVO result = userCouponService.applyCoupon(userCouponId, orderId, orderAmount);
        return Result.success(result);
    }

    /**
     * 取消优惠券应用（解锁优惠券）
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     */
    @PostMapping("/cancel")
    @ApiOperation("取消优惠券应用")
    public Result<Void> cancelCouponApply(
        @RequestParam Long userCouponId,
        @RequestParam Long orderId
    ) {
        log.info("取消优惠券应用：userCouponId={}, orderId={}", userCouponId, orderId);
        userCouponService.cancelCouponApply(userCouponId, orderId);
        return Result.success();
    }
}
