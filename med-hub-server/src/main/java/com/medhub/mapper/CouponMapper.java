package com.medhub.mapper;

import com.medhub.annotation.AutoFill;
import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.entity.Coupon;
import com.medhub.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    /**
     * 新增优惠券
     * @param coupon 优惠券实体
     * @return 优惠券 ID
     */
    @AutoFill(OperationType.INSERT)
    Long insert(Coupon coupon);

    /**
     * 更新优惠券
     * @param coupon 优惠券实体
     */
    @AutoFill(OperationType.UPDATE)
    void update(Coupon coupon);

    /**
     * 根据 ID 删除优惠券（逻辑删除）
     * @param id 优惠券 ID
     */
    void deleteById(@Param("id") Long id);

    /**
     * 根据 ID 查询优惠券
     * @param id 优惠券 ID
     * @return 优惠券实体
     */
    Coupon getById(@Param("id") Long id);

    /**
     * 分页查询优惠券
     * @param couponPageQueryDTO 分页查询 DTO
     * @return 优惠券列表
     */
    List<Coupon> pageQuery(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 查询优惠券总数
     * @param couponPageQueryDTO 分页查询 DTO
     * @return 总数
     */
    Long count(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 更新已领取数量
     * @param id 优惠券 ID
     * @param issuedCount 已领取数量
     */
    void updateIssuedCount(@Param("id") Long id, @Param("issuedCount") Integer issuedCount);

    /**
     * 查询优惠券统计信息
     * @return 统计数据
     */
    Coupon getStatistics();

    /**
     * 分页查询可领取的优惠券
     * @param couponPageQueryDTO 分页查询 DTO
     * @return 优惠券列表
     */
    List<Coupon> pageQueryAvailable(CouponPageQueryDTO couponPageQueryDTO);

    /**
     * 更新已领取数量（自增）
     * @param id 优惠券 ID
     */
    void updateIssuedCount(@Param("id") Long id);
}
