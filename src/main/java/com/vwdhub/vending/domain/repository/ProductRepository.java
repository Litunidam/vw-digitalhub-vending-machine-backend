package com.vwdhub.vending.domain.repository;

import com.vwdhub.vending.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Optional<Product> findById(UUID id);

    Product save(Product product);

}
