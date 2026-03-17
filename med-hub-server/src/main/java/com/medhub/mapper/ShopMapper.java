package com.medhub.mapper;

import com.medhub.entity.Shop;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ShopMapper {

    /**
     * 根据 ID 查询店铺信息
     * @param id 店铺 ID
     * @return 店铺信息
     */
    @Select("SELECT * FROM shop WHERE id = #{id}")
    Shop getById(Long id);

    /**
     * 查询店铺信息（默认查询第一条）
     * @return 店铺信息
     */
    @Select("SELECT * FROM shop LIMIT 1")
    Shop getShopInfo();

    /**
     * 插入店铺信息
     * @param shop 店铺信息
     */
    @Insert("INSERT INTO shop (name, address, phone, delivery_fee, min_delivery_amount, open_time, close_time, status, notice, description, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{address}, #{phone}, #{deliveryFee}, #{minDeliveryAmount}, #{openTime}, #{closeTime}, #{status}, #{notice}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Shop shop);

    /**
     * 更新店铺信息
     * @param shop 店铺信息
     */
    @Update("<script>" +
            "UPDATE shop " +
            "<set>" +
            "  <if test='name != null'>name = #{name},</if>" +
            "  <if test='address != null'>address = #{address},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='deliveryFee != null'>delivery_fee = #{deliveryFee},</if>" +
            "  <if test='minDeliveryAmount != null'>min_delivery_amount = #{minDeliveryAmount},</if>" +
            "  <if test='openTime != null'>open_time = #{openTime},</if>" +
            "  <if test='closeTime != null'>close_time = #{closeTime},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "  <if test='notice != null'>notice = #{notice},</if>" +
            "  <if test='description != null'>description = #{description},</if>" +
            "  <if test='updateTime != null'>update_time = #{updateTime},</if>" +
            "  <if test='updateUser != null'>update_user = #{updateUser},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    void update(Shop shop);

    /**
     * 更新店铺状态
     * @param id 店铺 ID
     * @param status 状态
     */
    @Update("UPDATE shop SET status = #{status} WHERE id = #{id}")
    void updateStatus(Long id, Integer status);

    /**
     * 查询所有店铺信息
     * @return 店铺信息列表
     */
    @Select("SELECT * FROM shop")
    List<Shop> list();
}
