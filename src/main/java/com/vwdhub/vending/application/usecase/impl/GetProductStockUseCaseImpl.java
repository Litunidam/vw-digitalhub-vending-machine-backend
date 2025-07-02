package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.GetProductStockUseCase;
import com.vwdhub.vending.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.UUID;

@Service
public class GetProductStockUseCaseImpl implements GetProductStockUseCase {

    private final ProductRepository productRepository;

    public GetProductStockUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Integer getProduct(UUID productId) {
        return (productRepository.findById(productId)
                .orElseThrow(
                        () -> new NotFoundException(productId.toString())))
                .getStock();
    }
}
