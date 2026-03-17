package com.medhub.service;

import com.medhub.dto.ShopDTO;
import com.medhub.entity.Shop;
import com.medhub.vo.ShopVO;
import java.util.List;

public interface ShopService {

    /**
     * 获取店铺信息
     * @return 店铺信息
     */
    ShopVO getShopInfo();

    /**
     * 根据 ID 获取店铺信息
     * @param id 店铺 ID
     * @return 店铺信息
     */
    ShopVO getShopById(Long id);

    /**
     * 更新店铺信息
     * @param shopDTO 店铺信息 DTO
     */
    void updateShop(ShopDTO shopDTO);

    /**
     * 更新店铺营业状态
     * @param status 状态 0:休息 1:营业
     */
    void updateStatus(Integer status);

    /**
     * 获取所有店铺信息
     * @return 店铺信息列表
     */
    List<ShopVO> list();
}
