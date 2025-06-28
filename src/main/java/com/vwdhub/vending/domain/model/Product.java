package com.vwdhub.vending.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class Product {

    private final String name;
    private final double price;
    private LocalDate expiration;
    private Integer stock;

    public boolean isInStock() {
        return stock > 0;
    }

    public void reduceStock() {
        if(!isInStock()) {
            throw new IllegalStateException("Out of stock");
        }
        stock--;
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDate.now());
    }
}
