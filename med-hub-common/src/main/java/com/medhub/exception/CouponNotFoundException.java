package com.medhub.exception;

/**
 * 优惠券不存在异常
 */
public class CouponNotFoundException extends BaseException {

    public CouponNotFoundException() {
    }

    public CouponNotFoundException(String msg) {
        super(msg);
    }
}
