package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.AddProductUseCaseImpl;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.exception.DispenserNotFoundException;
import com.vwdhub.vending.domain.model.Dispenser;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductUseCaseImplTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @InjectMocks
    private AddProductUseCaseImpl useCase;

    private UUID dispenserId;

    @BeforeEach
    void setUp() {
        dispenserId = UUID.randomUUID();
    }

    @Test
    void whenDispenserNotFoundThrowsDispenserNotFoundException() {

        when(dispenserRepository.findById(dispenserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.add(dispenserId, "Coke", new BigDecimal("1.50"), LocalDate.now().plusDays(5), 10)
        )
                .isInstanceOf(DispenserNotFoundException.class)
                .hasMessage(Constants.DISPENSER_NOT_FOUND);

        verify(dispenserRepository).findById(dispenserId);
        verifyNoMoreInteractions(dispenserRepository);
    }

    @Test
    void dispenserRetrievedAndProductAddedAndSaved() {
        Dispenser existing = Dispenser.builder()
                .id(dispenserId)
                .dispenserMoney(null)
                .build();
        existing.setProducts(new HashMap<>());

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.of(existing));
        when(dispenserRepository.save(any(Dispenser.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        String name = "Fanta";
        BigDecimal price = new BigDecimal("2.25");
        LocalDate expiration = LocalDate.of(2025, 12, 31);
        int stock = 7;
        Product created = useCase.add(dispenserId, name, price, expiration, stock);

        verify(dispenserRepository).findById(dispenserId);
        ArgumentCaptor<Dispenser> captor = ArgumentCaptor.forClass(Dispenser.class);
        verify(dispenserRepository).save(captor.capture());
        Dispenser saved = captor.getValue();

        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getPrice()).isEqualByComparingTo(price);
        assertThat(created.getExpiration()).isEqualTo(expiration);
        assertThat(created.getStock()).isEqualTo(stock);
        assertThat(created.getId()).isNotNull();

        Map<UUID, Product> productsMap = saved.getProducts();
        assertThat(productsMap)
                .hasSize(1)
                .containsKey(created.getId());

        Product inMap = productsMap.get(created.getId());
        assertThat(inMap)
                .usingRecursiveComparison()
                .isEqualTo(created);
    }

    @Test
    void throwsWhenDispenserNotFound() {
        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.empty());

        var ex = org.junit.jupiter.api.Assertions.assertThrows(
                DispenserNotFoundException.class,
                () -> useCase.add(dispenserId, "X", BigDecimal.ONE, LocalDate.now(), 1)
        );
        assertThat(ex.getMessage()).isEqualTo(Constants.DISPENSER_NOT_FOUND);
    }
}