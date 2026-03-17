package com.medhub.service.impl;

import com.medhub.context.BaseContext;
import com.medhub.dto.ShopDTO;
import com.medhub.entity.Shop;
import com.medhub.exception.ShopNotFoundException;
import com.medhub.mapper.ShopMapper;
import com.medhub.service.ShopService;
import com.medhub.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 获取店铺信息
     * @return 店铺信息
     */
    @Override
    public ShopVO getShopInfo() {
        log.info("获取店铺信息");
        Shop shop = shopMapper.getShopInfo();
        if (shop == null) {
            log.warn("店铺信息不存在");
            throw new ShopNotFoundException("店铺信息不存在");
        }
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return shopVO;
    }

    /**
     * 根据 ID 获取店铺信息
     * @param id 店铺 ID
     * @return 店铺信息
     */
    @Override
    public ShopVO getShopById(Long id) {
        log.info("根据 ID 获取店铺信息：id={}", id);
        Shop shop = shopMapper.getById(id);
        if (shop == null) {
            log.warn("店铺信息不存在：id={}", id);
            throw new ShopNotFoundException("店铺信息不存在");
        }
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return shopVO;
    }

    /**
     * 更新店铺信息
     * @param shopDTO 店铺信息 DTO
     */
    @Override
    @Transactional
    public void updateShop(ShopDTO shopDTO) {
        log.info("更新店铺信息：shopDTO={}", shopDTO);
        
        // 检查店铺是否存在
        Shop existShop = shopMapper.getById(shopDTO.getId());
        if (existShop == null) {
            log.warn("店铺不存在：id={}", shopDTO.getId());
            throw new ShopNotFoundException("店铺不存在");
        }

        // 创建更新对象
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopDTO, shop);
        shop.setUpdateTime(LocalDateTime.now());
        shop.setUpdateUser(BaseContext.getCurrentId());

        // 更新数据库
        shopMapper.update(shop);
        
        log.info("店铺信息更新成功：id={}", shopDTO.getId());
    }

    /**
     * 更新店铺营业状态
     * @param status 状态 0:休息 1:营业
     */
    @Override
    @Transactional
    public void updateStatus(Integer status) {
        log.info("更新店铺营业状态：status={}", status);
        
        // 获取默认店铺（ID=1）
        Shop shop = shopMapper.getById(1L);
        if (shop == null) {
            log.warn("店铺不存在");
            throw new ShopNotFoundException("店铺不存在");
        }

        // 更新状态
        shopMapper.updateStatus(shop.getId(), status);
        
        log.info("店铺营业状态更新成功：id={}, status={}", shop.getId(), status);
    }

    /**
     * 获取所有店铺信息
     * @return 店铺信息列表
     */
    @Override
    public List<ShopVO> list() {
        log.info("获取所有店铺信息");
        List<Shop> shopList = shopMapper.list();
        return shopList.stream()
                .map(shop -> {
                    ShopVO shopVO = new ShopVO();
                    BeanUtils.copyProperties(shop, shopVO);
                    return shopVO;
                })
                .collect(Collectors.toList());
    }
}
