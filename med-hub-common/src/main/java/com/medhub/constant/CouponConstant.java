package com.medhub.constant;

/**
 * 优惠券相关常量
 */
public class CouponConstant {

    private CouponConstant() {
        throw new IllegalStateException("Constant class");
    }

    // ==================== 优惠券类型常量 ====================

    /**
     * 满减券
     */
    public static final Integer COUPON_TYPE_DISCOUNT = 1;

    /**
     * 打折券
     */
    public static final Integer COUPON_TYPE_PERCENT_OFF = 2;

    // ==================== 优惠券状态常量 ====================

    /**
     * 禁用
     */
    public static final Integer COUPON_STATUS_DISABLED = 0;

    /**
     * 启用
     */
    public static final Integer COUPON_STATUS_ENABLED = 1;

    // ==================== 用户优惠券状态常量 ====================

    /**
     * 未使用
     */
    public static final Integer USER_COUPON_STATUS_UNUSED = 0;

    /**
     * 已使用
     */
    public static final Integer USER_COUPON_STATUS_USED = 1;

    /**
     * 已过期
     */
    public static final Integer USER_COUPON_STATUS_EXPIRED = 2;

    /**
     * 已锁定（下单中）
     */
    public static final Integer USER_COUPON_STATUS_LOCKED = 3;

    // ==================== 发行数量常量 ====================

    /**
     * 不限量标识
     */
    public static final Integer UNLIMITED_COUNT = -1;

    // ==================== 默认值常量 ====================

    /**
     * 默认单用户限领数量
     */
    public static final Integer DEFAULT_USER_LIMIT = 1;

    /**
     * 默认初始已领数量
     */
    public static final Integer DEFAULT_ISSUED_COUNT = 0;
}
