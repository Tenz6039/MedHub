package com.medhub.exception;

/**
 * 店铺未找到异常
 */
public class ShopNotFoundException extends BaseException {

    public ShopNotFoundException() {
    }

    public ShopNotFoundException(String msg) {
        super(msg);
    }

}
