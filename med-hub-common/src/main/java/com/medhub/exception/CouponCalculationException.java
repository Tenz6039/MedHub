package com.medhub.exception;

/**
 * 优惠券支付计算异常
 * 当支付金额计算过程中出现错误时抛出
 */
public class CouponCalculationException extends BaseException {

    public CouponCalculationException() {
    }

    public CouponCalculationException(String msg) {
        super(msg);
    }
}
