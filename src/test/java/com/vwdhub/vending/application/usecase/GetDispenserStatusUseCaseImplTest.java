package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.GetDispenserStatusUseCaseImpl;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDispenserStatusUseCaseImplTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @InjectMocks
    private GetDispenserStatusUseCaseImpl useCase;

    private UUID dispenserId;

    @BeforeEach
    void setUp() {
        dispenserId = UUID.randomUUID();
    }

    @Test
    void returnStatus() {

        when(dispenserRepository.findStatusById(dispenserId))
                .thenReturn(Optional.of(DispenserStatus.CHECKING));

        DispenserStatus status = useCase.getDispenserStatus(dispenserId);

        assertThat(status).isEqualTo(DispenserStatus.CHECKING);
        verify(dispenserRepository).findStatusById(dispenserId);
        verifyNoMoreInteractions(dispenserRepository);
    }

    @Test
    void getDispenserStatus_whenNotFound_throwsNoSuchElement() {

        when(dispenserRepository.findStatusById(dispenserId))
                .thenReturn(Optional.empty());

        NoSuchElementException ex = catchThrowableOfType(
                () -> useCase.getDispenserStatus(dispenserId),
                NoSuchElementException.class
        );
        assertThat(ex).hasMessage(Constants.DISPENSER_NOT_FOUND);

        verify(dispenserRepository).findStatusById(dispenserId);
        verifyNoMoreInteractions(dispenserRepository);
    }
}
