package com.vwdhub.vending.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.PRODUCT_EXPIRED;
import static com.vwdhub.vending.common.Constants.PRODUCT_STOCK_ZERO;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private final UUID id;
    private final String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDate expiration;

    public boolean isInStock() {
        return stock > 0;
    }

    public void reduceStock() {
        if (!isInStock()) {
            throw new IllegalStateException(PRODUCT_STOCK_ZERO);
        }
        stock--;
    }

    public void increaseStock() {
        stock++;
    }

    public void checkValid() {
        if (!isInStock()) {
            throw new IllegalStateException(PRODUCT_STOCK_ZERO);
        }
        if (expiration.isBefore(LocalDate.now())) {
            throw new IllegalStateException(PRODUCT_EXPIRED);
        }
    }
}
