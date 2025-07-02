package com.vwdhub.vending.domain.exception;

public class OutOfOrderException extends RuntimeException {
    public OutOfOrderException(String message) {
        super(message);
    }
}
