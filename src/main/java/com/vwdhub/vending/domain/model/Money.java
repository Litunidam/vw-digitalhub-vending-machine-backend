package com.vwdhub.vending.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.ChangeException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
public class Money {
    private Map<Coin, Integer> coins;

    @JsonCreator
    public Money(@JsonProperty("coins") Map<Coin, Integer> coins) {
        this.coins = coins.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getKey().getValue().compareTo(entry1.getKey().getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    public BigDecimal totalAmount() {
        return coins.entrySet()
                .stream()
                .map(coin -> coin.getKey().getValue().multiply(BigDecimal.valueOf(coin.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Money add(Money money) {
        Map<Coin, Integer> newCoins = new LinkedHashMap<>(coins);
        money.getCoins().forEach((coin, count) -> newCoins.merge(coin, count, Integer::sum));
        return new Money(newCoins);
    }

    public Money subtract(Money money) {
        Map<Coin, Integer> result = new EnumMap<>(Coin.class);
        for (Coin coin : Coin.values()) {
            int count = coins.getOrDefault(coin, 0) - money.coins.getOrDefault(coin, 0);
            if (count < 0) {
                throw new ChangeException(Constants.NOT_ENOUGH_MONEY_TO_CHANGE);
            }
            result.put(coin, count);
        }
        return new Money(result);
    }

    public Money change(BigDecimal amount) {
        BigDecimal remaining = amount;
        Map<Coin, Integer> change = new LinkedHashMap<>();

        for (Map.Entry<Coin, Integer> entry : coins.entrySet()) {
            Coin coin = entry.getKey();
            int availableCoinAmount = entry.getValue();
            int coinsNeeded = remaining.divide(coin.getValue()).setScale(0, RoundingMode.DOWN).intValue();
            if (coinsNeeded > 0 && availableCoinAmount > 0) {
                int countLeft = Math.min(coinsNeeded, availableCoinAmount);
                change.put(coin, countLeft);
                remaining = remaining.subtract(coin.getValue().multiply(BigDecimal.valueOf(countLeft)));
            }
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new ChangeException(Constants.NOT_ENOUGH_MONEY_TO_CHANGE.concat(amount.toString()));
        }
        return new Money(change);
    }

    public boolean canProvideChange(BigDecimal amount) {
        try {
            change(amount);
            return true;
        } catch (ChangeException e) {
            return false;
        }
    }
}
