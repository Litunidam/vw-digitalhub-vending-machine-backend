package com.vwdhub.vending.infrastructure.event;

import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.vwdhub.vending.common.Constants.LCD_TOPIC;
import static com.vwdhub.vending.common.Constants.REPOSITION_TOPIC;

@Slf4j
@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafka;

    public KafkaEventProducer(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    public CompletableFuture<SendResult<String, String>> publish(LCDNotificationEvent event) {
        return kafka.send(LCD_TOPIC, event.getState())
                .handle((result, exception) -> {
                    if (exception != null) {
                        throw new KafkaException("Error publishing LCD event: " + exception.getMessage(), exception);
                    } else {
                        log.info("LCD notification event published: {}", event.getState());
                    }
                    return result;
                });
    }

    public CompletableFuture<SendResult<String, String>> publish(RepositionEvent event) {
        return kafka.send(REPOSITION_TOPIC, event.getState())
                .handle((result, exception) -> {
                    if (exception != null) {
                        throw new KafkaException("Error publishing Reposition event: " + exception.getMessage(), exception);
                    } else {
                        log.info("Reposition notification event published: {}", event.getState());
                    }
                    return result;
                });
    }
}
