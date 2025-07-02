package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.ProductRepository;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import com.vwdhub.vending.infrastructure.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductSpringDataRepository productRepository;
    private final ProductMapper productMapper;

    public ProductRepositoryAdapter(ProductSpringDataRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        return productMapper.toDomain(productRepository.save(productMapper.toEntity(product)));
    }

}
