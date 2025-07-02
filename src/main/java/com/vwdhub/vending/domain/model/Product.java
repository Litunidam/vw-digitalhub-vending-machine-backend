package com.vwdhub.vending.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.InsufficientStockException;
import com.vwdhub.vending.domain.exception.ProductExpiredException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

    @JsonIgnore
    public boolean isInStock() {
        return stock > 0;
    }

    public void reduceStock() {
        if (!isInStock()) {
            throw new InsufficientStockException(Constants.PRODUCT_STOCK_ZERO);
        }
        stock--;
    }

    public void increaseStock() {
        stock++;
    }

    public void checkValid() {
        if (!isInStock()) {
            throw new InsufficientStockException(Constants.PRODUCT_STOCK_ZERO);
        }
        if (expiration.isBefore(LocalDate.now())) {
            throw new ProductExpiredException(Constants.PRODUCT_EXPIRED);
        }
    }
}
