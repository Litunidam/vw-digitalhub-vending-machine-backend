package com.vwdhub.vending.domain.exception;

public class DispenserNotFoundException extends RuntimeException {
    public DispenserNotFoundException(String message) {
        super(message);
    }
}
