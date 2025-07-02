package com.vwdhub.vending.infrastructure.event;

import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.vwdhub.vending.common.Constants.LCD_TOPIC;
import static com.vwdhub.vending.common.Constants.REPOSITION_TOPIC;

@Slf4j
@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafka;

    public KafkaEventProducer(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    public void publish(LCDNotificationEvent event) {
        kafka.send(LCD_TOPIC, event.getState()).addCallback(
                result -> {
                    assert result != null;
                    log.info("LCD notification event published: {}", event.getState());
                },
                exception -> {
                    throw new KafkaException(exception.getMessage());
                }
        );
    }

    public void publish(RepositionEvent event) {
        kafka.send(REPOSITION_TOPIC, event.getState()).addCallback(
                result -> {
                    assert result != null;
                    log.info("Reposition notification event published: {}", event.getState());
                },
                exception -> {
                    throw new KafkaException(exception.getMessage());
                }
        );
    }
}
