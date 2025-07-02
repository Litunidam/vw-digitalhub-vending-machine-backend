package com.vwdhub.vending.application.usecase;

import com.vwdhub.vending.application.usecase.impl.PurchaseProductUseCaseImpl;
import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import com.vwdhub.vending.domain.exception.DispenserNotFoundException;
import com.vwdhub.vending.domain.model.*;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import com.vwdhub.vending.infrastructure.event.KafkaEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseProductUseCaseImplTest {

    @Mock
    private DispenserRepository dispenserRepository;

    @Mock
    private KafkaEventProducer eventProducer;

    @InjectMocks
    private PurchaseProductUseCaseImpl useCase;

    private final UUID dispenserId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    private Dispenser dispenser;
    private Money initialMoney;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(productId)
                .name("Test")
                .price(new BigDecimal("1.00"))
                .expiration(LocalDate.now().plusDays(1))
                .stock(1)
                .build();

        initialMoney = new Money(Map.of(Coin.EUR_1, 5));

        Dispenser real = new Dispenser(dispenserId, Map.of(productId, product), initialMoney);
        dispenser = spy(real);
    }

    @Test
    void purchaseNotConfirmedThrowsAndNotPublishAnything() {
        assertThatThrownBy(() ->
                useCase.purchase(dispenserId, productId, initialMoney, false)
        )
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(Constants.PURCHASE_NOT_CONFIRMED);

        verifyNoInteractions(dispenserRepository, eventProducer);
    }

    @Test
    void purchaseDispenserNotFoundThrowsAfterConfirmPublish() {

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.purchase(dispenserId, productId, initialMoney, true)
        )
                .isInstanceOf(DispenserNotFoundException.class)
                .hasMessage(Constants.DISPENSER_NOT_FOUND);

        verify(eventProducer).publish((LCDNotificationEvent) argThat(evt ->
                evt instanceof LCDNotificationEvent &&
                        ((LCDNotificationEvent) evt).getState().equals(Constants.CONFIRMED)
        ));
        verify(dispenserRepository).findById(dispenserId);
        verifyNoMoreInteractions(eventProducer, dispenserRepository);
    }

    @Test
    void purchaseSuccessfulNoReposition() {

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.of(dispenser));

        ChangeAndProduct cap = ChangeAndProduct.builder()
                .change(new Money(Map.of()))
                .product(product)
                .dispenserMoney(initialMoney)
                .build();
        doReturn(cap).when(dispenser).purchase(productId);

        ChangeAndProduct result = useCase.purchase(dispenserId, productId, initialMoney, true);

        assertThat(result).isSameAs(cap);

        InOrder inOrder = inOrder(eventProducer, dispenser, dispenserRepository);

        inOrder.verify(eventProducer).publish((LCDNotificationEvent) argThat(evt ->
                evt instanceof LCDNotificationEvent &&
                        ((LCDNotificationEvent) evt).getState().equals(Constants.CONFIRMED)
        ));
        inOrder.verify(dispenser).insertMoney(initialMoney);
        inOrder.verify(eventProducer).publish((LCDNotificationEvent) argThat(evt ->
                evt instanceof LCDNotificationEvent &&
                        ((LCDNotificationEvent) evt).getState().equals(Constants.VALIDATE_MONEY)
        ));
        inOrder.verify(dispenser).purchase(productId);
        inOrder.verify(eventProducer).publish((LCDNotificationEvent) argThat(evt ->
                evt instanceof LCDNotificationEvent &&
                        ((LCDNotificationEvent) evt).getState().equals(Constants.DISPENSING_PRODUCT)
        ));
        inOrder.verify(dispenser).setStatus(DispenserStatus.AVAILABLE);
        inOrder.verify(dispenserRepository).save(dispenser);

        verify(eventProducer, never()).publish(isA(RepositionEvent.class));
    }

    @Test
    void purchaseSuccessfulWithReposition() {

        when(dispenserRepository.findById(dispenserId))
                .thenReturn(Optional.of(dispenser));

        Product soldOut = Product.builder()
                .id(productId)
                .name("X")
                .price(new BigDecimal("1.00"))
                .expiration(LocalDate.now().plusDays(1))
                .stock(0)
                .build();
        ChangeAndProduct capZero = ChangeAndProduct.builder()
                .change(new Money(Map.of()))
                .product(soldOut)
                .dispenserMoney(initialMoney)
                .build();
        doReturn(capZero).when(dispenser).purchase(productId);

        ChangeAndProduct result = useCase.purchase(dispenserId, productId, initialMoney, true);
        assertThat(result).isSameAs(capZero);

        ArgumentCaptor<RepositionEvent> captor = ArgumentCaptor.forClass(RepositionEvent.class);
        verify(eventProducer).publish(captor.capture());

        RepositionEvent evt = captor.getValue();
        assertThat(evt.getState())
                .isEqualTo(Constants.PRODUCT_STOCK_ZERO + productId);
    }
}
