package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;

import java.util.UUID;

public interface PurchaseProductUseCase {
    ChangeAndProduct purchase(UUID dispenserId, UUID productId, Money insertedAmount, boolean confirmed);
}
