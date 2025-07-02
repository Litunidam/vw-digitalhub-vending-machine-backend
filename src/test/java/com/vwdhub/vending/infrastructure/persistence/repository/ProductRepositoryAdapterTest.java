package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import com.vwdhub.vending.infrastructure.persistence.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductSpringDataRepository springRepo;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    private UUID productId;
    private ProductEntity entity;
    private Product domain;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        entity = ProductEntity.builder()
                .id(productId)
                .name("TestProduct")
                .price(new BigDecimal("1.23"))
                .expiration(LocalDate.now().plusDays(10))
                .stock(5)
                .build();

        domain = Product.builder()
                .id(productId)
                .name("TestProduct")
                .price(new BigDecimal("1.23"))
                .expiration(LocalDate.now().plusDays(10))
                .stock(5)
                .build();
    }

    @Test
    void findById_present() {
        when(springRepo.findById(productId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Product> result = adapter.findById(productId);

        assertThat(result).isPresent().get().isEqualTo(domain);
        verify(springRepo).findById(productId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_empty() {
        when(springRepo.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = adapter.findById(productId);

        assertThat(result).isEmpty();
        verify(springRepo).findById(productId);
        verify(mapper, never()).toDomain(any());
    }
}
