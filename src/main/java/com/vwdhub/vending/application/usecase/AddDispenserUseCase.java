package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface AddDispenserUseCase {
    Dispenser add(UUID id, List<Product> products, Money initialCoins, DispenserStatus status);
}
