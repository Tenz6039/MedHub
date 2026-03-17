package com.medhub.mapper;

import com.medhub.dto.CouponPageQueryDTO;
import com.medhub.dto.UserCouponQueryDTO;
import com.medhub.entity.Coupon;
import com.medhub.entity.UserCoupon;
import com.medhub.vo.CouponTop10VO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户优惠券 Mapper
 */
@Mapper
public interface UserCouponMapper {

    /**
     * 新增用户优惠券
     * @param userCoupon 用户优惠券实体
     * @return 用户优惠券 ID
     */
    Long insert(UserCoupon userCoupon);

    /**
     * 根据 ID 查询用户优惠券
     * @param id 用户优惠券 ID
     * @return 用户优惠券实体
     */
    @Select("select * from user_coupon where id = #{id}")
    UserCoupon getById(Long id);

    /**
     * 分页查询用户优惠券列表
     * @param userId 用户 ID
     * @param userCouponQueryDTO 查询条件
     * @return 用户优惠券列表
     */
    List<UserCoupon> pageQuery(@Param("userId") Long userId, UserCouponQueryDTO userCouponQueryDTO);

    /**
     * 查询用户优惠券详情（关联优惠券表）
     * @param userCouponId 用户优惠券 ID
     * @return 用户优惠券实体（包含优惠券信息）
     */
    UserCoupon getDetailById(@Param("userCouponId") Long userCouponId);

    /**
     * 查询用户是否已领取某优惠券
     * @param userId 用户 ID
     * @param couponId 优惠券 ID
     * @return 已领取的数量
     */
    @Select("select count(*) from user_coupon where user_id = #{userId} and coupon_id = #{couponId}")
    Integer countUserObtained(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 查询用户未使用的优惠券（用于下单页）
     * @param userId 用户 ID
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    List<UserCoupon> getAvailableForOrder(@Param("userId") Long userId, @Param("orderAmount") Object orderAmount);

    /**
     * 更新用户优惠券状态
     * @param userCoupon 用户优惠券实体
     */
    void update(UserCoupon userCoupon);

    /**
     * 锁定用户优惠券（下单时使用）
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     */
    @Select("update user_coupon set status = 3, order_id = #{orderId} where id = #{userCouponId}")
    void lock(@Param("userCouponId") Long userCouponId, @Param("orderId") Long orderId);

    /**
     * 解锁用户优惠券（取消订单时使用）
     * @param userCouponId 用户优惠券 ID
     */
    @Select("update user_coupon set status = 0, order_id = null where id = #{userCouponId} and status = 3")
    void unlock(Long userCouponId);

    /**
     * 使用用户优惠券（支付成功时使用）
     * @param userCouponId 用户优惠券 ID
     * @param orderId 订单 ID
     */
    @Select("update user_coupon set status = 1, use_time = now(), order_id = #{orderId} where id = #{userCouponId}")
    void use(@Param("userCouponId") Long userCouponId, @Param("orderId") Long orderId);
    
    /**
     * 根据订单 ID 查询用户优惠券列表
     * @param orderId 订单 ID
     * @return 用户优惠券列表
     */
    @Select("select * from user_coupon where order_id = #{orderId}")
    List<UserCoupon> listByOrderId(@Param("orderId") Long orderId);
}
