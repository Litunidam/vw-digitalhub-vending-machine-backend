package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.GetDispenserStatusUseCase;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.DISPENSER_NOT_FOUND;

@Service
public class GetDispenserStatusUseCaseImpl implements GetDispenserStatusUseCase {
    private final DispenserRepository dispenserRepository;

    public GetDispenserStatusUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public DispenserStatus getDispenserStatus(UUID dispenserId) {
        return dispenserRepository.findStatusById(dispenserId).orElseThrow(
                () -> new NoSuchElementException(DISPENSER_NOT_FOUND)
        );
    }
}
