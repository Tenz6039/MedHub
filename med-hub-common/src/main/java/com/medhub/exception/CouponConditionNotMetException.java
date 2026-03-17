package com.medhub.exception;

/**
 * 优惠券使用条件不满足异常
 * 当订单金额不满足优惠券使用门槛时抛出
 */
public class CouponConditionNotMetException extends BaseException {

    public CouponConditionNotMetException() {
    }

    public CouponConditionNotMetException(String msg) {
        super(msg);
    }
}
