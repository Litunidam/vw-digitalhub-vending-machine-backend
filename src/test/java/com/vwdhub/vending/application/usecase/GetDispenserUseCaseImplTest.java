package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.GetDispenserUseCaseImpl;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDispenserUseCaseImplTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @InjectMocks
    private GetDispenserUseCaseImpl useCase;

    private UUID dispenserId;
    private Dispenser dispenser;

    @BeforeEach
    void setUp() {
        dispenserId = UUID.randomUUID();
        dispenser = Dispenser.builder()
                .id(dispenserId)
                .products(Map.of())
                .dispenserMoney(null)
                .build();
    }

    @Test
    void whenExistsReturnDispenser() {

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.of(dispenser));

        Dispenser result = useCase.get(dispenserId);

        assertThat(result).isSameAs(dispenser);
        verify(dispenserRepository).findById(dispenserId);
        verifyNoMoreInteractions(dispenserRepository);
    }

    @Test
    void whenNotExistsThrowsNoSuchElementException() {

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.empty());

        NoSuchElementException ex = catchThrowableOfType(
                () -> useCase.get(dispenserId),
                NoSuchElementException.class
        );
        assertThat(ex).hasMessage(Constants.DISPENSER_NOT_FOUND);

        verify(dispenserRepository).findById(dispenserId);
        verifyNoMoreInteractions(dispenserRepository);
    }
}
