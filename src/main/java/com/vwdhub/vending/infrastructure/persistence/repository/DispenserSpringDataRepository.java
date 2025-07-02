package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.infrastructure.persistence.entity.DispenserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DispenserSpringDataRepository extends JpaRepository<DispenserEntity, UUID> {
}
