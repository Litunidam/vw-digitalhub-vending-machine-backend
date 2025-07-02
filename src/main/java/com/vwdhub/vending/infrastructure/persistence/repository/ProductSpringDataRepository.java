package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSpringDataRepository extends JpaRepository<ProductEntity, UUID> {
}
