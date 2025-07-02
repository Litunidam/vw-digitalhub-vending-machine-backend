package com.vwdhub.vending.application.usecase;

import java.util.UUID;

public interface GetProductStockUseCase {
    Integer getProduct(UUID productId);
}
