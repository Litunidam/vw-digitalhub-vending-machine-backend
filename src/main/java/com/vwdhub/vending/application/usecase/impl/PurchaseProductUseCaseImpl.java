package com.vwdhub.vending.application.usecase.impl;

import com.vwdhub.vending.application.usecase.PurchaseProductUseCase;
import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import com.vwdhub.vending.domain.model.Dispenser;
import com.vwdhub.vending.domain.model.DispenserStatus;
import com.vwdhub.vending.domain.model.Money;
import com.vwdhub.vending.domain.model.valueobject.ChangeAndProduct;
import com.vwdhub.vending.domain.repository.DispenserRepository;
import com.vwdhub.vending.infrastructure.event.KafkaEventProducer;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.vwdhub.vending.common.Constants.*;

@Service
public class PurchaseProductUseCaseImpl implements PurchaseProductUseCase {

    private final DispenserRepository dispenserRepository;
    private final KafkaEventProducer eventProducer;

    public PurchaseProductUseCaseImpl(DispenserRepository dispenserRepository, KafkaEventProducer eventProducer) {
        this.dispenserRepository = dispenserRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public ChangeAndProduct purchase(UUID dispenserId, UUID productId, Money insertedAmount, boolean confirmed) {

        long startTime = System.currentTimeMillis();

        if (!confirmed) {
            throw new NoSuchElementException(PURCHASE_NOT_CONFIRMED);
        }
        eventProducer.publish(LCDNotificationEvent.builder().state(CONFIRMED).build());

        Dispenser dispenser = findDispenser(dispenserId);

        dispenser.insertMoney(insertedAmount);
        eventProducer.publish(LCDNotificationEvent.builder().state(VALIDATE_MONEY).build());

        ChangeAndProduct result = purchaseProduct(productId, insertedAmount, dispenser);

        if ((System.currentTimeMillis() - startTime) >= 5000) {
            eventProducer.publish(LCDNotificationEvent.builder().state(OUT_OF_TIME).build());
            throw new NoSuchElementException(OUT_OF_TIME);
        }
        eventProducer.publish(LCDNotificationEvent.builder().state(DISPENSING_PRODUCT).build());
        dispenser.setStatus(DispenserStatus.AVAILABLE);
        dispenserRepository.save(dispenser);

        if (result.getProduct().getStock() == 0) {
            eventProducer.publish(RepositionEvent.builder().state(PRODUCT_STOCK_ZERO.concat(result.getProduct().getId().toString())).build());
        }
        return result;
    }

    private static ChangeAndProduct purchaseProduct(UUID productId, Money insertedAmount, Dispenser dispenser) {
        ChangeAndProduct result = dispenser.purchase(productId);
        dispenser.getDispenserMoney().add(insertedAmount);
        dispenser.setDispenserMoney(dispenser.getDispenserMoney().subtract(result.getChange()));
        return result;
    }

    private Dispenser findDispenser(UUID dispenserId) {
        return dispenserRepository.findById(dispenserId)
                .orElseThrow(() -> new NoSuchElementException(DISPENSER_NOT_FOUND));
    }
}
