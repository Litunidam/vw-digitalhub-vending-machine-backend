package com.vwdhub.vending.domain.model;

import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.ChangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private Money money;

    @BeforeEach
    void setUp() {
        Map<Coin, Integer> unsortedMap = new LinkedHashMap<>();
        unsortedMap.put(Coin.CENT_10, 2);
        unsortedMap.put(Coin.EUR_1, 1);
        unsortedMap.put(Coin.CENT_05, 3);
        money = new Money(unsortedMap);
    }

    @Test
    void sortsCoinsDescendingByValue() {
        List<Coin> keysOrder = new ArrayList<>(money.getCoins().keySet());
        assertThat(keysOrder)
                .containsExactly(Coin.EUR_1, Coin.CENT_10, Coin.CENT_05);
    }

    @Test
    void sumCorrectly() {
        // 1 * 1.00 + 2 * 0.10 + 3 * 0.05 = 1.00 + 0.20 + 0.15 = 1.35
        assertThat(money.totalAmount())
                .isEqualByComparingTo(new BigDecimal("1.35"));
    }

    @Test
    void combinesTwoMoney() {
        Money other = new Money(Map.of(
                Coin.CENT_10, 1,
                Coin.CENT_20, 2
        ));
        Money sum = money.add(other);
        // Original money: {EUR_1=1, CENT_10=2, CENT_05=3}
        // Other: {CENT_20=2, CENT_10=1}
        // Sum: {EUR_1=1, CENT_10=3, CENT_05=3, CENT_20=2}
        LinkedHashMap<Coin, Integer> expected = new LinkedHashMap<>();
        expected.put(Coin.EUR_1, 1);
        expected.put(Coin.CENT_20, 2);
        expected.put(Coin.CENT_10, 3);
        expected.put(Coin.CENT_05, 3);
        assertThat(sum.getCoins())
                .containsExactlyEntriesOf(expected);
    }

    @Test
    void subtractReturnsOk() {
        Money toSubtract = new Money(Map.of(
                Coin.CENT_10, 1,
                Coin.CENT_05, 2
        ));
        Money result = money.subtract(toSubtract);

        // EUR_2 (0), EUR_1 (1), CENT_50 (0), CENT_20 (0), CENT_10 (1), CENT_05 (1)
        LinkedHashMap<Coin, Integer> expected = new LinkedHashMap<>();
        expected.put(Coin.EUR_2, 0);
        expected.put(Coin.EUR_1, 1);
        expected.put(Coin.CENT_50, 0);
        expected.put(Coin.CENT_20, 0);
        expected.put(Coin.CENT_10, 1);
        expected.put(Coin.CENT_05, 1);

        assertThat(result.getCoins())
                .containsExactlyEntriesOf(expected);
    }


    @Test
    void subtractNotSufficientCoinsThrowsChangeException() {
        Money tooMuch = new Money(Map.of(
                Coin.CENT_10, 3
        ));
        assertThatThrownBy(() -> money.subtract(tooMuch))
                .isInstanceOf(ChangeException.class)
                .hasMessage(Constants.NOT_ENOUGH_MONEY_TO_CHANGE);
    }

    @Test
    void returnsCorrectChange() {
        // money: {EUR_1=1, CENT_10=2, CENT_05=3}
        // request 1.20 => should use {EUR_1=1, no cents}
        Money changeFor120 = money.change(new BigDecimal("1.00"));
        assertThat(changeFor120.getCoins())
                .containsExactly(Map.entry(Coin.EUR_1, 1));
        // request 0.35 => use {CENT_10=2, CENT_05=3? no, only need 0.35 => 0.10*2 + 0.05*3}
        Money changeFor035 = money.change(new BigDecimal("0.35"));
        LinkedHashMap<Coin, Integer> expected = new LinkedHashMap<>();
        expected.put(Coin.CENT_10, 2);
        expected.put(Coin.CENT_05, 3);
        assertThat(changeFor035.getCoins())
                .containsExactlyEntriesOf(expected);
    }

    @Test
    void insufficientTotalAmountThrowsChangeException() {
        BigDecimal request = new BigDecimal("2.00");
        // total available is 1.35
        assertThatThrownBy(() -> money.change(request))
                .isInstanceOf(ChangeException.class)
                .hasMessage(Constants.NOT_ENOUGH_MONEY_TO_CHANGE.concat(request.toString()));
    }

    @Test
    void canProvideChangeTest() {
        assertThat(money.canProvideChange(new BigDecimal("0.15"))).isTrue();   // 3×0.05
        assertThat(money.canProvideChange(new BigDecimal("2.00"))).isFalse();
    }

    @Test
    void totalAmountZero() {
        Money empty = new Money(Collections.emptyMap());
        assertThat(empty.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(empty.canProvideChange(BigDecimal.ZERO)).isTrue();
        assertThatThrownBy(() -> empty.change(new BigDecimal("0.05")))
                .isInstanceOf(ChangeException.class);
    }
}
