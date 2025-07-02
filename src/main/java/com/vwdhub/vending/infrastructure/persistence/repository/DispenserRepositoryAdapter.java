package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import com.vwdhub.vending.infrastructure.persistence.entity.DispenserEntity;
import com.vwdhub.vending.infrastructure.persistence.mapper.DispenserMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Repository
public class DispenserRepositoryAdapter implements DispenserRepository {

    private final DispenserSpringDataRepository dispenserRepository;

    private final DispenserMapper dispenserMapper;

    public DispenserRepositoryAdapter(DispenserSpringDataRepository dispenserRepository, DispenserMapper dispenserMapper) {
        this.dispenserRepository = dispenserRepository;
        this.dispenserMapper = dispenserMapper;
    }

    @Override
    public Optional<Dispenser> findById(UUID id) {
        return dispenserRepository.findById(id).map(dispenserMapper::toDomain);
    }

    @Override
    public Dispenser save(Dispenser dispenser) {
        DispenserEntity dispenserEntity = dispenserMapper.toEntity(dispenser);

        if (dispenser.getProducts() != null) {
            dispenserEntity.getProducts().forEach(productEntity -> productEntity.setDispenser(dispenserEntity));
        }

        if (dispenserEntity.getDispenserMoney() != null) {
            dispenserEntity.getDispenserMoney().forEach(moneyEntity -> moneyEntity.setDispenser(dispenserEntity));
        }

        return dispenserMapper.toDomain(dispenserRepository.save(dispenserEntity));
    }

    @Override
    public Optional<DispenserStatus> findStatusById(UUID id) {
        return dispenserRepository.findById(id).map(dispenserMapper::toDomain).map(Dispenser::getStatus);
    }
}
