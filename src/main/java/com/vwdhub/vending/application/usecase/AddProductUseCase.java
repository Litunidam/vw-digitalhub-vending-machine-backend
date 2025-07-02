package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.domain.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface AddProductUseCase {
    Product add(UUID dispenserId, String name, BigDecimal price, LocalDate expiration, int stock);
}
