package com.vwdhub.vending.domain.exception;

public class UuidNotNullException extends RuntimeException {
    public UuidNotNullException(String message) {
        super(message);
    }
}
