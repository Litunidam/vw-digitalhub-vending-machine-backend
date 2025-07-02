package com.vwdhub.vending.infrastructure.persistence.mapper;

import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toDomain_mapsAllFields() {
        UUID id = UUID.randomUUID();
        String name = "TestProduct";
        BigDecimal price = new BigDecimal("2.50");
        LocalDate exp = LocalDate.of(2025, 12, 31);
        int stock = 42;

        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setPrice(price);
        entity.setExpiration(exp);
        entity.setStock(stock);

        Product domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo(name);
        assertThat(domain.getPrice()).isEqualByComparingTo(price);
        assertThat(domain.getExpiration()).isEqualTo(exp);
        assertThat(domain.getStock()).isEqualTo(stock);
    }

    @Test
    void mapsAllFieldsEntity() {
        UUID id = UUID.randomUUID();
        String name = "AnotherProduct";
        BigDecimal price = new BigDecimal("7.75");
        LocalDate exp = LocalDate.of(2024, 6, 30);
        int stock = 10;

        Product domain = Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .expiration(exp)
                .stock(stock)
                .build();

        ProductEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getPrice()).isEqualByComparingTo(price);
        assertThat(entity.getExpiration()).isEqualTo(exp);
        assertThat(entity.getStock()).isEqualTo(stock);
    }

    @Test
    void domainReturnsNull() {
        assertThat(mapper.toDomainList(null)).isNull();
    }

    @Test
    void domainReturnsEmptyList() {
        List<Product> result = mapper.toDomainList(Collections.emptyList());
        assertThat(result).isNotNull()
                .isEmpty();
    }

    @Test
    void domainMapsEachElement() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        ProductEntity e1 = new ProductEntity();
        e1.setId(id1);
        e1.setName("P1");
        e1.setPrice(new BigDecimal("1.00"));
        e1.setExpiration(LocalDate.now());
        e1.setStock(5);

        ProductEntity e2 = new ProductEntity();
        e2.setId(id2);
        e2.setName("P2");
        e2.setPrice(new BigDecimal("3.50"));
        e2.setExpiration(LocalDate.now().plusDays(10));
        e2.setStock(0);

        List<ProductEntity> entities = List.of(e1, e2);
        List<Product> domains = mapper.toDomainList(entities);

        assertThat(domains).hasSize(2);
        assertThat(domains.get(0).getId()).isEqualTo(id1);
        assertThat(domains.get(0).getName()).isEqualTo("P1");
        assertThat(domains.get(0).getStock()).isEqualTo(5);

        assertThat(domains.get(1).getId()).isEqualTo(id2);
        assertThat(domains.get(1).getName()).isEqualTo("P2");
        assertThat(domains.get(1).getStock()).isEqualTo(0);
    }
}
