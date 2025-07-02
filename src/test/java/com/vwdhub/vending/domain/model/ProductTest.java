package com.vwdhub.vending.domain.model;

import com.vwdhub.vending.common.Constants;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductTest {

    private static final UUID SOME_ID = UUID.randomUUID();
    private static final String SOME_NAME = "TestProduct";
    private static final BigDecimal SOME_PRICE = new BigDecimal("9.99");

    @Test
    void isInStockReturnsTrueWhenStockPositive() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(5)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        assertThat(product.isInStock()).isTrue();
    }

    @Test
    void isInStockReturnsFalseWhenStockZero() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(0)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        assertThat(product.isInStock()).isFalse();
    }

    @Test
    void reduceStockDecrementsWhenInStock() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(3)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        product.reduceStock();

        assertThat(product.getStock()).isEqualTo(2);
    }

    @Test
    void reduceStockThrowsWhenStockZero() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(0)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        assertThatThrownBy(product::reduceStock)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(Constants.PRODUCT_STOCK_ZERO);
    }

    @Test
    void increaseStockIncrements() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(2)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        product.increaseStock();

        assertThat(product.getStock()).isEqualTo(3);
    }

    @Test
    void passesWhenStockPositiveAndNotExpired() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(1)
                .expiration(LocalDate.now())  // equal to today is OK
                .build();

        // no exception
        product.checkValid();
    }

    @Test
    void throwsWhenStockZero() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(0)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        assertThatThrownBy(product::checkValid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(Constants.PRODUCT_STOCK_ZERO);
    }

    @Test
    void throwsWhenExpiredEvenIfInStock() {
        Product product = Product.builder()
                .id(SOME_ID)
                .name(SOME_NAME)
                .price(SOME_PRICE)
                .stock(1)
                .expiration(LocalDate.now().minusDays(1))
                .build();

        assertThatThrownBy(product::checkValid)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(Constants.PRODUCT_EXPIRED);
    }
}
