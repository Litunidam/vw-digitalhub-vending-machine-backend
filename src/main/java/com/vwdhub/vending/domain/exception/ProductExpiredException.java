package com.vwdhub.vending.domain.exception;

public class ProductExpiredException extends RuntimeException {
    public ProductExpiredException(String message) {
        super(message);
    }
}
