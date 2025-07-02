package com.vwdhub.vending.infrastructure.adapter.web;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.*;
import com.vwdhub.vending.infrastructure.adapter.web.dto.response.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DispenserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handle(DispenserNotFoundException ex) {
        return ErrorDto.builder()
                .code(Constants.DISPENSER_NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handle(ProductNotFoundException ex) {
        return ErrorDto.builder()
                .code(Constants.PRODUCT_NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ChangeException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ErrorDto handle(ChangeException ex) {
        return ErrorDto.builder()
                .code(Constants.NOT_ENOUGH_MONEY_TO_CHANGE)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ErrorDto handle(InsufficientStockException ex) {
        return ErrorDto.builder()
                .code(Constants.PRODUCT_STOCK_ZERO)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(OutOfOrderException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ErrorDto handle(OutOfOrderException ex) {
        return ErrorDto.builder()
                .code(Constants.OUT_OF_ORDER)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ProductExpiredException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ErrorDto handle(ProductExpiredException ex) {
        return ErrorDto.builder()
                .code(Constants.PRODUCT_EXPIRED)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handle(MethodArgumentTypeMismatchException ex) {

        return ErrorDto.builder()
                .code(Constants.BAD_UUID)
                .message("Invalid UUID " + ex.getValue())
                .build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handle(InvalidFormatException ex) {

        return ErrorDto.builder()
                .code(Constants.BAD_UUID)
                .message("Invalid UUID " + ex.getValue())
                .build();
    }

    @ExceptionHandler(UuidNotNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handle(UuidNotNullException ex) {

        return ErrorDto.builder()
                .code(Constants.UUID_NOT_NULL)
                .message(ex.getMessage())
                .build();
    }
}
