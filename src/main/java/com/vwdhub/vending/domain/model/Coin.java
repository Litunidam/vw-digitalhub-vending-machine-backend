package com.vwdhub.vending.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Coin {
    CENT_05(new BigDecimal("0.05")),
    CENT_10(new BigDecimal("0.10")),
    CENT_20(new BigDecimal("0.20")),
    CENT_50(new BigDecimal("0.50")),
    EUR_1(new BigDecimal("1.00")),
    EUR_2(new BigDecimal("2.00"));

    private final BigDecimal value;

    public static Optional<Coin> fromValue(BigDecimal value) {
        return Arrays.stream(values())
                .filter(coin -> coin.getValue().equals(value))
                .findFirst();
    }
}
