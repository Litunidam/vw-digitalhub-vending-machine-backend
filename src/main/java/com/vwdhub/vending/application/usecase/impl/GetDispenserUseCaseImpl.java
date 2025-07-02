package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.GetDispenserUseCase;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.DispenserNotFoundException;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class GetDispenserUseCaseImpl implements GetDispenserUseCase {

    private final DispenserRepository dispenserRepository;

    public GetDispenserUseCaseImpl(DispenserRepository dispenserRepository) {
        this.dispenserRepository = dispenserRepository;
    }

    @Override
    public Dispenser get(UUID id) {
        return dispenserRepository.findById(id)
                .orElseThrow(() -> new DispenserNotFoundException(Constants.DISPENSER_NOT_FOUND));
    }
}
