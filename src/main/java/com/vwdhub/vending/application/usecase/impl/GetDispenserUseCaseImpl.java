package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.GetDispenserUseCase;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.DISPENSER_NOT_FOUND;

@Service
public class GetDispenserUseCaseImpl implements GetDispenserUseCase {

    private final DispenserRepository dispenserRepository;

    public GetDispenserUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public Dispenser get(UUID id) {
        return dispenserRepository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException(DISPENSER_NOT_FOUND)
                );
    }
}
