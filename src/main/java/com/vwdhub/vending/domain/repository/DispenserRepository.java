package com.vwdhub.vending.domain.repository;

import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;

import java.util.Optional;
import java.util.UUID;

public interface DispenserRepository {

    Optional<Dispenser> findById(UUID id);

    Dispenser save(Dispenser dispenser);

    Optional<DispenserStatus> findStatusById(UUID id);
}
