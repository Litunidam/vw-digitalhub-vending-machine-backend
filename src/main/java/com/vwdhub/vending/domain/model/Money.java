package com.vwdhub.vending.domain.model;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Money {
    private final Map<Coin, Integer> coins;

    public Money(Map<Coin, Integer> coins) {
        this.coins = coins.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getKey().getValue(), entry1.getKey().getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    public double totalAmount() {
        return coins.entrySet()
                .stream()
                .mapToDouble(coin -> coin.getKey().getValue() * coin.getValue())
                .sum();
    }

    public Money change(double amount) {
        double remaining = amount;
        Map<Coin, Integer> change = new LinkedHashMap<>();

        for (Map.Entry<Coin, Integer> entry : coins.entrySet()) {
            Coin coin = entry.getKey();
            int availableCoinAmount = entry.getValue();

            int coinsNeeded = (int) Math.floor(remaining / coin.getValue());

            if (coinsNeeded > 0 && availableCoinAmount > 0) {
                int countLeft = Math.min(coinsNeeded, availableCoinAmount);
                change.put(coin, countLeft);
                remaining = remaining - (countLeft * coin.getValue());
            }
        }

        if (remaining > 0) {
            throw new IllegalArgumentException("We ran out of change for " + amount);
        }
        return new Money(change);
    }
}
