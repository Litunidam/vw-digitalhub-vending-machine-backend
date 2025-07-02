package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.GetDispenserStatusUseCase;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.DispenserNotFoundException;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetDispenserStatusUseCaseImpl implements GetDispenserStatusUseCase {
    private final DispenserRepository dispenserRepository;

    public GetDispenserStatusUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public DispenserStatus getDispenserStatus(UUID dispenserId) {
        return dispenserRepository.findStatusById(dispenserId).orElseThrow(
                () -> new DispenserNotFoundException(Constants.DISPENSER_NOT_FOUND)
        );
    }
}
