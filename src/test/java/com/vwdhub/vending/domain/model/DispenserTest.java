package com.vwdhub.vending.domain.model;

import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.vwdhub.vending.common.Constants.*;
import static org.assertj.core.api.Assertions.*;

class DispenserTest {

    private Money initialDispenserMoney;
    private Dispenser dispenser;
    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        UUID dispenserId = UUID.randomUUID();
        productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .name("Test")
                .price(new BigDecimal("0.20"))
                .stock(5)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        Map<UUID, Product> products = new HashMap<>();
        products.put(productId, product);

        initialDispenserMoney = new Money(Map.of(
                Coin.CENT_50, 10,
                Coin.CENT_10, 10
        ));

        dispenser = new Dispenser(dispenserId, products, initialDispenserMoney);
    }

    @Test
    void addProductInserts() {
        UUID newId = UUID.randomUUID();
        Product productAdd = Product.builder()
                .id(newId)
                .name("X")
                .price(BigDecimal.ONE)
                .stock(1)
                .expiration(LocalDate.now().plusDays(1))
                .build();

        dispenser.addProduct(productAdd);
        assertThat(dispenser.getProducts())
                .containsEntry(newId, productAdd);
    }

    @Test
    void findProductReturnsIt() {
        assertThat(dispenser.findProduct(productId))
                .isSameAs(product);
    }

    @Test
    void findProductThrows() {
        assertThatThrownBy(() -> dispenser.findProduct(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(PRODUCT_NOT_FOUND);
    }

    @Test
    void insertMoneyInserts() {
        Money money = new Money(Map.of(Coin.EUR_1, 1));
        dispenser.insertMoney(money);

        assertThat(dispenser.getInsertedMoney()).isEqualTo(money);
        assertThat(dispenser.getStatus()).isEqualTo(DispenserStatus.CHECKING);
    }

    @Test
    void insertMoneyThrows() {
        dispenser.setStatus(DispenserStatus.OUT_OF_ORDER);
        assertThatThrownBy(() -> dispenser.insertMoney(new Money(Map.of())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(OUT_OF_ORDER);
    }

    @Test
    void purchaseWithoutInsertThrowsOutOfOrder() {
        assertThatThrownBy(() -> dispenser.purchase(productId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(OUT_OF_ORDER);
    }

    @Test
    void purchaseInsufficientMoneyThrows() {
        dispenser.insertMoney(new Money(Map.of(Coin.CENT_10, 1))); // 0.10 < price 0.20
        assertThatThrownBy(() -> dispenser.purchase(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(NOT_ENOUGH_MONEY);
    }

    @Test
    void purchaseCantChangeThrows() {
        dispenser.setDispenserMoney(new Money(Map.of()));
        dispenser.insertMoney(new Money(Map.of(Coin.EUR_1, 1))); // total 1.00, price 0.20, change 0.80
        assertThatThrownBy(() -> dispenser.purchase(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(NOT_ENOUGH_MONEY_TO_CHANGE);
    }

    @Test
    void purchaseExpiredProductThrows() {
        Product expired = Product.builder()
                .id(UUID.randomUUID())
                .name("old")
                .price(new BigDecimal("0.10"))
                .stock(1)
                .expiration(LocalDate.now().minusDays(1))
                .build();
        dispenser.getProducts().put(expired.getId(), expired);

        dispenser.insertMoney(new Money(Map.of(Coin.EUR_1, 1)));
        assertThatThrownBy(() -> dispenser.purchase(expired.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PRODUCT_EXPIRED);
    }

    @Test
    void purchaseOutOfStockThrows() {
        Product zeroStock = Product.builder()
                .id(UUID.randomUUID())
                .name("gone")
                .price(new BigDecimal("0.10"))
                .stock(0)
                .expiration(LocalDate.now().plusDays(1))
                .build();
        dispenser.getProducts().put(zeroStock.getId(), zeroStock);

        dispenser.insertMoney(new Money(Map.of(Coin.EUR_1, 1)));
        assertThatThrownBy(() -> dispenser.purchase(zeroStock.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(PRODUCT_STOCK_ZERO);
    }

    @Test
    void purchaseSuccessful() {
        dispenser.insertMoney(new Money(Map.of(Coin.EUR_1, 1)));
        ChangeAndProduct change = dispenser.purchase(productId);

        LinkedHashMap<Coin, Integer> expectedChange = new LinkedHashMap<>();
        expectedChange.put(Coin.CENT_50, 1);
        expectedChange.put(Coin.CENT_10, 3);

        assertThat(change.getChange().getCoins())
                .containsExactlyEntriesOf(expectedChange);
    }

}
