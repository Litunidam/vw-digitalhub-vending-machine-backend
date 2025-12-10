package com.vwdhub.vending.infrastructure.event;

import com.vwdhub.vending.common.Constants;
import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaEventProducer producer;

    @Test
    void publishLcdOk() {

        LCDNotificationEvent event = LCDNotificationEvent.builder().state("OK_LCD").build();
        SendResult<String, String> mockResult = mock(SendResult.class);

        when(kafkaTemplate.send(eq(Constants.LCD_TOPIC), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockResult));

        assertThatNoException().isThrownBy(() ->
                producer.publish(event)
        );

        verify(kafkaTemplate).send(Constants.LCD_TOPIC, "OK_LCD");
    }

    @Test
    void publishLcdKafkaException() {

        LCDNotificationEvent event = LCDNotificationEvent.builder().state("FAIL_LCD").build();

        CompletableFuture<SendResult<String, String>> failedFuture = CompletableFuture.failedFuture(new RuntimeException("kafka down"));

        when(kafkaTemplate.send(eq(Constants.LCD_TOPIC), anyString()))
                .thenReturn(failedFuture);

        assertThatThrownBy(() -> producer.publish(event).join())
                .isInstanceOf(java.util.concurrent.CompletionException.class)
                .hasCauseInstanceOf(KafkaException.class)
                .hasMessageContaining("kafka down");

        verify(kafkaTemplate).send(Constants.LCD_TOPIC, "FAIL_LCD");
    }


    @Test
    void publishRepositionOk() {

        RepositionEvent event = RepositionEvent.builder().state("OK_REPO").build();
        SendResult<String, String> mockResult = mock(SendResult.class);

        when(kafkaTemplate.send(eq(Constants.REPOSITION_TOPIC), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockResult));

        assertThatNoException().isThrownBy(() ->
                producer.publish(event)
        );

        verify(kafkaTemplate).send(Constants.REPOSITION_TOPIC, "OK_REPO");
    }

    @Test
    void publishRepositionKafkaException() {

        RepositionEvent event = RepositionEvent.builder().state("FAIL_REPO").build();

        CompletableFuture<SendResult<String, String>> failedFuture = CompletableFuture.failedFuture(new RuntimeException("topic unreachable"));

        when(kafkaTemplate.send(eq(Constants.REPOSITION_TOPIC), anyString()))
                .thenReturn(failedFuture);

        assertThatThrownBy(() -> producer.publish(event).join())
                .isInstanceOf(java.util.concurrent.CompletionException.class)
                .hasCauseInstanceOf(KafkaException.class)
                .hasMessageContaining("topic unreachable");

        verify(kafkaTemplate).send(Constants.REPOSITION_TOPIC, "FAIL_REPO");
    }
}