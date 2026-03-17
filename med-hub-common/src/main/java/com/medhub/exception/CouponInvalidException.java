package com.medhub.exception;

/**
 * 优惠券无效异常
 * 当优惠券无效时抛出（过期、已使用、不符合使用条件等）
 */
public class CouponInvalidException extends BaseException {

    public CouponInvalidException() {
    }

    public CouponInvalidException(String msg) {
        super(msg);
    }
}
