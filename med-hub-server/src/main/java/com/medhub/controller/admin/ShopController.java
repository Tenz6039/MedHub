package com.medhub.controller.admin;

import com.medhub.dto.ShopDTO;
import com.medhub.result.Result;
import com.medhub.service.ShopService;
import com.medhub.vo.ShopVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    public static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private ShopService shopService;

    /**
     * 设置店铺状态
     * @param status 店铺状态
     * @return Result
     */
    @ApiOperation("设置店铺状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status == 1 ? "营业" : "关闭");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return Result
     */
    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        if (status == null) {
            status = 0;
        }
        log.info("获取店铺状态：{}", status == 1 ? "营业" : "关闭");
        return Result.success(status);
    }
    
    /**
     * 获取店铺详细信息
     * @return Result
     */
    @ApiOperation("获取店铺详细信息")
    @GetMapping("/info")
    public Result<ShopVO> getShopInfo() {
        log.info("管理员获取店铺详细信息");
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
        log.info("管理员根据 ID 获取店铺信息：id={}", id);
        ShopVO shopVO = shopService.getShopById(id);
        return Result.success(shopVO);
    }
    
    /**
     * 获取所有店铺信息
     * @return Result
     */
    @ApiOperation("获取所有店铺信息")
    @GetMapping("/list")
    public Result<List<ShopVO>> list() {
        log.info("获取所有店铺信息");
        List<ShopVO> shopList = shopService.list();
        return Result.success(shopList);
    }
    
    /**
     * 更新店铺信息
     * @param shopDTO 店铺信息 DTO
     * @return Result
     */
    @ApiOperation("更新店铺信息")
    @PutMapping("/update")
    public Result update(@RequestBody ShopDTO shopDTO) {
        log.info("更新店铺信息：shopDTO={}", shopDTO);
        shopService.updateShop(shopDTO);
        return Result.success();
    }
    
    /**
     * 更新店铺营业状态
     * @param status 状态 0:休息 1:营业
     * @return Result
     */
    @ApiOperation("更新店铺营业状态")
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Integer status) {
        log.info("更新店铺营业状态：status={}", status);
        shopService.updateStatus(status);
        return Result.success();
    }
}
