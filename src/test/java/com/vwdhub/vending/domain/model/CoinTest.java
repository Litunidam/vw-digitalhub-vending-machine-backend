package com.vwdhub.vending.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CoinTest {

    static Stream<Arguments> coinValues() {
        return Stream.of(
                Arguments.of(new BigDecimal("0.05"), Coin.CENT_05),
                Arguments.of(new BigDecimal("0.10"), Coin.CENT_10),
                Arguments.of(new BigDecimal("0.20"), Coin.CENT_20),
                Arguments.of(new BigDecimal("0.50"), Coin.CENT_50),
                Arguments.of(new BigDecimal("1.00"), Coin.EUR_1),
                Arguments.of(new BigDecimal("2.00"), Coin.EUR_2)
        );
    }

    @ParameterizedTest
    @MethodSource("coinValues")
    void returnsExpectedCoin(BigDecimal value, Coin expected) {
        Optional<Coin> result = Coin.fromValue(value);
        assertThat(result)
                .as("fromValue(%s) should return %s", value, expected)
                .contains(expected);
    }

    @Test
    void returnsEmpty() {
        Optional<Coin> result = Coin.fromValue(new BigDecimal("0.15"));
        assertThat(result)
                .as("0.15 is not a supported coin value")
                .isEmpty();
    }

    @Test
    void differentScaleReturnsEmpty() {
        Optional<Coin> result = Coin.fromValue(new BigDecimal("0.050"));
        assertThat(result)
                .as("0.050 (scale 3) should not match the 0.05 coin (scale 2)")
                .isEmpty();
    }

    @Test
    void returnsConfiguredBigDecimal() {
        assertThat(Coin.EUR_1.getValue())
                .as("EUR_1 should have value 1.00")
                .isEqualByComparingTo(new BigDecimal("1.00"));
        assertThat(Coin.CENT_20.getValue())
                .as("CENT_20 should have value 0.20")
                .isEqualByComparingTo(new BigDecimal("0.20"));
    }
}
