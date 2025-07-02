package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.AddDispenserUseCaseImpl;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.Product;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddDispenserUseCaseImplTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @InjectMocks
    private AddDispenserUseCaseImpl useCase;

    private UUID dispenserId;
    private Product product1;
    private Product product2;
    private Money initialMoney;

    @BeforeEach
    void setUp() {
        dispenserId = UUID.randomUUID();
        product1 = Product.builder()
                .id(UUID.randomUUID())
                .name("Coke")
                .price(BigDecimal.valueOf(1.50))
                .stock(10)
                .expiration(LocalDate.now().plusDays(10))
                .build();
        product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Pepsi")
                .price(BigDecimal.valueOf(1.20))
                .stock(8)
                .expiration(LocalDate.now().plusDays(5))
                .build();
        initialMoney = Money.builder()
                .coins(Map.of())
                .build();

        when(dispenserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void addProductList() {
        List<Product> products = List.of(product1, product2);
        DispenserStatus status = DispenserStatus.AVAILABLE;

        Dispenser result = useCase.add(dispenserId, products, initialMoney, status);

        ArgumentCaptor<Dispenser> captor = ArgumentCaptor.forClass(Dispenser.class);
        verify(dispenserRepository).save(captor.capture());
        Dispenser saved = captor.getValue();

        Map<UUID, Product> expected = Map.of(
                product1.getId(), product1,
                product2.getId(), product2
        );

        assertThat(saved.getProducts())
                .containsExactlyInAnyOrderEntriesOf(expected);

        assertThat(result).isSameAs(saved);
    }


    @Test
    void addEmptyProductList() {
        List<Product> products = Collections.emptyList();
        DispenserStatus status = DispenserStatus.OUT_OF_ORDER;

        Dispenser result = useCase.add(dispenserId, products, initialMoney, status);

        ArgumentCaptor<Dispenser> captor = ArgumentCaptor.forClass(Dispenser.class);
        verify(dispenserRepository).save(captor.capture());
        Dispenser saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(dispenserId);
        assertThat(saved.getProducts()).isEmpty();
        assertThat(saved.getDispenserMoney()).isEqualTo(initialMoney);
        assertThat(saved.getStatus()).isEqualTo(status);
        assertThat(result).isSameAs(saved);
    }
}
