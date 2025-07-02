package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.AddDispenserUseCase;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddDispenserUseCaseImpl implements AddDispenserUseCase {

    private final DispenserRepository dispenserRepository;

    public AddDispenserUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public Dispenser add(UUID id, List<Product> products, Money initialCoins, DispenserStatus status) {
        return dispenserRepository.save(Dispenser.builder().id(id).products(mapProducts(products)).dispenserMoney(initialCoins).status(status).build());
    }

    private Map<UUID, Product> mapProducts(List<Product> products) {
        return products.stream().collect(Collectors.toMap(Product::getId, this::mapId));
    }

    private Product mapId(Product product) {
        if(product.getId() == null) {
            product = Product.builder()
                    .id(UUID.randomUUID())
                    .name(product.getName())
                    .stock(product.getStock())
                    .price(product.getPrice())
                    .expiration(product.getExpiration())
                    .build();
            return product;
        }
        return product;
    }
}
