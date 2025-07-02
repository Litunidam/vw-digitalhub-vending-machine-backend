package com.vwdhub.vending.domain.model;

import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Dispenser {

    private final UUID id;

    private Map<UUID, Product> products;

    private Money dispenserMoney;

    private DispenserStatus status = DispenserStatus.AVAILABLE;

    private Money insertedMoney;

    public Dispenser(UUID id, Map<UUID, Product> products, Money dispenserMoney) {
        this.id = id;
        this.products = products;
        this.dispenserMoney = dispenserMoney;
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public Product findProduct(UUID productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new NoSuchElementException(PRODUCT_NOT_FOUND);
        }
        return product;
    }

    public void insertMoney(Money money) {
        if (status != DispenserStatus.AVAILABLE) {
            throw new IllegalStateException(OUT_OF_ORDER);
        }
        if (insertedMoney == null) {
            insertedMoney = money;
        }
        status = DispenserStatus.CHECKING;
    }

    public ChangeAndProduct purchase(UUID productId) {
        isAvailable();
        Product product = findProduct(productId);
        product.checkValid();
        BigDecimal price = (product.getPrice()).setScale(2, RoundingMode.DOWN);
        BigDecimal total = insertedMoney.totalAmount().setScale(2, RoundingMode.DOWN);

        if (total.compareTo(price) < 0) {
            throw new IllegalArgumentException(NOT_ENOUGH_MONEY.concat(total.toString()));
        }
        BigDecimal changeAmount = total.subtract(price);
        if (!dispenserMoney.canProvideChange(changeAmount)) {
            throw new IllegalArgumentException(NOT_ENOUGH_MONEY_TO_CHANGE.concat(changeAmount.toString()));
        }
        dispenserMoney = dispenserMoney.add(insertedMoney);

        Money change = dispenserMoney.change(changeAmount);
        product.reduceStock();
        status = DispenserStatus.DISPENSING;

        return ChangeAndProduct.builder().change(change).product(product).dispenserMoney(dispenserMoney).build();
    }

    private void isAvailable() {
        if (status != DispenserStatus.CHECKING) {
            throw new IllegalStateException(OUT_OF_ORDER);
        }
    }


}
