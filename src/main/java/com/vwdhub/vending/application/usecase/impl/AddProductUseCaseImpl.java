package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.AddProductUseCase;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import com.vwdhub.vending.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.DISPENSER_NOT_FOUND;

@Service
public class AddProductUseCaseImpl implements AddProductUseCase {

    private final DispenserRepository dispenserRepository;

    public AddProductUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public Product add(UUID dispenserId, String name, BigDecimal price, LocalDate expiration, int stock) {
        Dispenser dispenser = dispenserRepository.findById(dispenserId).orElseThrow(
                () -> new IllegalArgumentException(DISPENSER_NOT_FOUND)
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
