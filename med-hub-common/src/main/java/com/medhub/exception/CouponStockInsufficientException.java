package com.medhub.exception;

/**
 * 优惠券库存不足异常
 */
public class CouponStockInsufficientException extends BaseException {

    public CouponStockInsufficientException() {
    }

    public CouponStockInsufficientException(String msg) {
        super(msg);
    }
}
