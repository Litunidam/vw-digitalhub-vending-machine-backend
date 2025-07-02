package com.vwdhub.vending.infrastructure.persistence.repository;

import com.vwdhub.vending.domain.model.*;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import com.vwdhub.vending.infrastructure.persistence.entity.*;
import com.vwdhub.vending.infrastructure.persistence.mapper.DispenserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispenserRepositoryAdapterTest {

    @Mock
    private DispenserSpringDataRepository springRepo;

    @Mock
    private DispenserMapper mapper;

    @InjectMocks
    private DispenserRepositoryAdapter adapter;

    private UUID dispenserId;
    private DispenserEntity entity;
    private Dispenser domain;

    @BeforeEach
    void setUp() {
        dispenserId = UUID.randomUUID();

        entity = DispenserEntity.builder()
                .id(dispenserId)
                .status(DispenserStatusEntity.AVAILABLE)
                .products(new ArrayList<>())
                .dispenserMoney(new ArrayList<>())
                .build();

        domain = new Dispenser(dispenserId, Map.of(), Money.builder().coins(Map.of()).build());
        domain.setStatus(DispenserStatus.AVAILABLE);
    }

    @Test
    void findById() {
        when(springRepo.findById(dispenserId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Dispenser> result = adapter.findById(dispenserId);

        assertThat(result).isPresent().contains(domain);
        verify(springRepo).findById(dispenserId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findByIdEmpty() {
        when(springRepo.findById(dispenserId)).thenReturn(Optional.empty());

        Optional<Dispenser> result = adapter.findById(dispenserId);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void findStatusById() {
        when(springRepo.findById(dispenserId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        domain.setStatus(DispenserStatus.OUT_OF_ORDER);

        Optional<DispenserStatus> status = adapter.findStatusById(dispenserId);

        assertThat(status).contains(DispenserStatus.OUT_OF_ORDER);
    }

    @Test
    void findStatusByIdEmpty() {
        when(springRepo.findById(dispenserId)).thenReturn(Optional.empty());

        Optional<DispenserStatus> status = adapter.findStatusById(dispenserId);

        assertThat(status).isEmpty();
    }

    @Test
    void saveTest() {

        UUID prodId = UUID.randomUUID();
        Product product = Product.builder()
                .id(prodId)
                .name("Test")
                .price(new BigDecimal("1.00"))
                .expiration(LocalDate.now().plusDays(1))
                .stock(3)
                .build();

        Money initialMoney = Money.builder()
                .coins(Map.of(Coin.EUR_1, 2))
                .build();

        Dispenser toSave = new Dispenser(dispenserId,
                Map.of(prodId, product),
                initialMoney);
        toSave.setStatus(DispenserStatus.AVAILABLE);

        ProductEntity productEntity = ProductEntity.builder().id(prodId).build();
        CoinEntity coinEntity = CoinEntity.builder().coin(Coin.EUR_1.name()).count(2).build();

        DispenserEntity dispenserEntity = DispenserEntity.builder()
                .id(dispenserId)
                .status(DispenserStatusEntity.AVAILABLE)
                .products(new ArrayList<>(List.of(productEntity)))
                .dispenserMoney(new ArrayList<>(List.of(coinEntity)))
                .build();

        DispenserEntity dispenserEntityExpected = DispenserEntity.builder()
                .id(dispenserId)
                .status(DispenserStatusEntity.AVAILABLE)
                .products(new ArrayList<>(List.of(productEntity)))
                .dispenserMoney(new ArrayList<>(List.of(coinEntity)))
                .build();

        Dispenser mappedBack = new Dispenser(dispenserId, Map.of(), Money.builder().coins(Map.of()).build());
        mappedBack.setStatus(DispenserStatus.AVAILABLE);

        when(mapper.toEntity(toSave)).thenReturn(dispenserEntity);
        when(springRepo.save(dispenserEntity)).thenReturn(dispenserEntityExpected);
        when(mapper.toDomain(dispenserEntityExpected)).thenReturn(mappedBack);

        Dispenser result = adapter.save(toSave);

        assertThat(result).isSameAs(mappedBack);
        verify(mapper).toEntity(toSave);
        verify(springRepo).save(dispenserEntity);
        verify(mapper).toDomain(dispenserEntityExpected);

        for (ProductEntity productEntity1 : dispenserEntity.getProducts()) {
            assertThat(productEntity1.getDispenser()).isSameAs(dispenserEntity);
        }
        for (CoinEntity coin : dispenserEntity.getDispenserMoney()) {
            assertThat(coin.getDispenser()).isSameAs(dispenserEntity);
        }
    }
}
