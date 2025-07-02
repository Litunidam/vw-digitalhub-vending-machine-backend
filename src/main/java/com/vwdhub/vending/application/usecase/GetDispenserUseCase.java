package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.domain.model.Dispenser;

import java.util.UUID;

public interface GetDispenserUseCase {
    Dispenser get(UUID id);
}
