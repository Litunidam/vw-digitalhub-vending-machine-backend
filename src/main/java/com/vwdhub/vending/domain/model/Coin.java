package com.vwdhub.vending.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Coin {
    CENT_5(0.05),
    CENT_10(0.10),
    CENT_20(0.20),
    CENT_50(0.50),
    EUR_1(1.00),
    EUR_2(2.00);

    private final double value;

    public static Optional<Coin> fromValue(double value) {
        return Arrays.stream(values())
                .filter(coin -> coin.getValue() == value)
                .findFirst();
    }
}
