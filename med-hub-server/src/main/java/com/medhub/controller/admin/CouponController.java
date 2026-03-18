package com.medhub.controller.admin;

import com.medhub.dto.CouponCreateDTO;
import com.medhub.dto.CouponEditDTO;
import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.result.PageResult;
import com.medhub.result.Result;
import com.medhub.service.CouponService;
import com.medhub.vo.CouponVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 优惠券管理
 */
@RestController
@RequestMapping("/admin/coupon")
@Api(tags = "优惠券管理接口")
@Slf4j
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 删除优惠券缓存
     * @param pattern 缓存 key 模式
     */
    private void deleteCouponCache(String pattern) {
        Set redisKeys = redisTemplate.keys(pattern);
        if (redisKeys != null && !redisKeys.isEmpty()) {
            redisTemplate.delete(redisKeys);
            log.info("删除优惠券缓存：pattern={}, 删除数量={}", pattern, redisKeys.size());
        }
    }

    /**
     * 创建优惠券
     * @param couponCreateDTO 优惠券创建信息
     * @return 创建的优惠券 ID
     */
    @ApiOperation("创建优惠券")
    @PostMapping
    public Result<Long> createCoupon(@RequestBody CouponCreateDTO couponCreateDTO) {
        log.info("创建优惠券：{}", couponCreateDTO);
        Long couponId = couponService.createCoupon(couponCreateDTO);
        deleteCouponCache("coupon_available_list_*");
        return Result.success(couponId);
    }

    /**
     * 修改优惠券
     * @param couponEditDTO 优惠券修改信息
     * @return 操作结果
     */
    @ApiOperation("修改优惠券")
    @PutMapping
    public Result updateCoupon(@RequestBody CouponEditDTO couponEditDTO) {
        log.info("修改优惠券：{}", couponEditDTO);
        couponService.updateCoupon(couponEditDTO);
        deleteCouponCache("coupon_available_list_*");
        return Result.success();
    }

    /**
     * 删除优惠券
     * @param id 优惠券 ID
     * @return 操作结果
     */
    @ApiOperation("删除优惠券")
    @DeleteMapping("/{id}")
    public Result deleteCoupon(@PathVariable Long id) {
        log.info("删除优惠券：{}", id);
        couponService.deleteCoupon(id);
        deleteCouponCache("coupon_available_list_*");
        return Result.success();
    }

    /**
     * 优惠券分页查询
     * @param couponPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @ApiOperation("优惠券分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageCoupon(CouponPageQueryDTO couponPageQueryDTO) {
        log.info("优惠券分页查询：{}", couponPageQueryDTO);
        PageResult pageResult = couponService.pageCoupon(couponPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询优惠券详情
     * @param id 优惠券 ID
     * @return 优惠券详情
     */
    @ApiOperation("查询优惠券详情")
    @GetMapping("/{id}")
    public Result<CouponVO> getById(@PathVariable Long id) {
        log.info("查询优惠券详情：{}", id);
        CouponVO couponVO = couponService.getById(id);
        return Result.success(couponVO);
    }

    /**
     * 修改优惠券状态（启用/禁用）
     * @param status 状态：0-禁用，1-启用
     * @param id 优惠券 ID
     * @return 操作结果
     */
    @ApiOperation("修改优惠券状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改优惠券状态：id={}, status={}", id, status);
        couponService.updateStatus(id, status);
        deleteCouponCache("coupon_available_list_*");
        return Result.success();
    }
}
