package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.AddProductUseCase;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.DispenserNotFoundException;
import com.vwdhub.vending.domain.exception.UuidNotNullException;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

@Service
public class AddProductUseCaseImpl implements AddProductUseCase {

    private final DispenserRepository dispenserRepository;

    public AddProductUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public Product add(UUID dispenserId, String name, BigDecimal price, LocalDate expiration, int stock) {
        if (dispenserId == null) {
            throw new UuidNotNullException(Constants.UUID_NOT_NULL);
        }
        Dispenser dispenser = dispenserRepository.findById(dispenserId).orElseThrow(
                () -> new DispenserNotFoundException(Constants.DISPENSER_NOT_FOUND)
        );
        if (dispenser.getProducts() == null) {
            dispenser.setProducts(new HashMap<>());
        }
        UUID productId = UUID.randomUUID();
        Product newProduct = Product.builder()
                .id(productId)
                .name(name)
                .price(price)
                .expiration(expiration)
                .stock(stock)
                .build();
        dispenser.getProducts().put(newProduct.getId(), newProduct);
        Dispenser saved = dispenserRepository.save(dispenser);
        return saved.getProducts().get(productId);
    }
}
