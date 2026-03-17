package com.medhub.controller.user;

import com.medhub.result.Result;
import com.medhub.service.ShopService;
import com.medhub.vo.ShopVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private ShopService shopService;
    
    /**
     * 获取店铺状态
     * @return Result
     */
    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        // 处理 status 为 null 的情况
        if (status == null) {
            status = 0; // 默认关闭状态
        }
        log.info("获取店铺状态：{}", status == 1 ? "营业" : "关闭");
        return Result.success(status);
    }

    /**
     * 获取药房信息
     * @return Result
     */
    @ApiOperation("获取药房信息")
    @GetMapping("/getMerchantInfo")
    public Result getMerchantInfo() {
        log.info("获取药房信息");
        Map<String, Object> info = new HashMap<>();
        info.put("phone", "400-123-4567");
        return Result.success(info);
    }
    
    /**
     * 获取店铺详细信息
     * @return Result
     */
    @ApiOperation("获取店铺详细信息")
    @GetMapping("/info")
    public Result<ShopVO> getShopInfo() {
        log.info("获取店铺详细信息");
        ShopVO shopVO = shopService.getShopInfo();
        return Result.success(shopVO);
    }
    
    /**
     * 根据 ID 获取店铺信息
     * @param id 店铺 ID
     * @return Result
     */
    @ApiOperation("根据 ID 获取店铺信息")
    @GetMapping("/info/{id}")
    public Result<ShopVO> getShopById(@PathVariable Long id) {
        log.info("根据 ID 获取店铺信息：id={}", id);
        ShopVO shopVO = shopService.getShopById(id);
        return Result.success(shopVO);
    }
}
