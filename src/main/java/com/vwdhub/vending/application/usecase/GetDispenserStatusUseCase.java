package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.domain.model.DispenserStatus;

import java.util.UUID;

public interface GetDispenserStatusUseCase {
    DispenserStatus getDispenserStatus(UUID dispenserId);
}
