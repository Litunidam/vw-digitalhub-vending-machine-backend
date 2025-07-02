package com.vwdhub.vending.infrastructure.event;

import com.vwdhub.vending.domain.event.LCDNotificationEvent;
import com.vwdhub.vending.domain.event.RepositionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import static com.vwdhub.vending.common.Constants.LCD_TOPIC;
import static com.vwdhub.vending.common.Constants.REPOSITION_TOPIC;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ListenableFuture<SendResult<String, String>> future;

    @InjectMocks
    private KafkaEventProducer producer;

    @Captor
    private ArgumentCaptor<SuccessCallback<SendResult<String, String>>> successCaptor;

    @Captor
    private ArgumentCaptor<FailureCallback> failureCaptor;

    @BeforeEach
    void setUp() {
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);
    }

    @Test
    void publishLcdOk() {

        LCDNotificationEvent event = LCDNotificationEvent.builder().state("OK_LCD").build();

        producer.publish(event);

        verify(kafkaTemplate).send(LCD_TOPIC, "OK_LCD");
        verify(future).addCallback(successCaptor.capture(), failureCaptor.capture());

        assertThatNoException().isThrownBy(() ->
                successCaptor.getValue().onSuccess(mock(SendResult.class))
        );
    }

    @Test
    void publishLcdKafkaException() {

        LCDNotificationEvent event = LCDNotificationEvent.builder().state("FAIL_LCD").build();
        producer.publish(event);

        verify(future).addCallback(successCaptor.capture(), failureCaptor.capture());
        Exception cause = new Exception("kafka down");

        assertThatThrownBy(() ->
                failureCaptor.getValue().onFailure(cause)
        ).isInstanceOf(KafkaException.class)
                .hasMessage("kafka down");
    }


    @Test
    void publishRepositionOk() {

        RepositionEvent event = RepositionEvent.builder().state("OK_REPO").build();

        producer.publish(event);

        verify(kafkaTemplate).send(REPOSITION_TOPIC, "OK_REPO");
        verify(future).addCallback(successCaptor.capture(), failureCaptor.capture());

        assertThatNoException().isThrownBy(() ->
                successCaptor.getValue().onSuccess(mock(SendResult.class))
        );
    }

    @Test
    void publishRepositionKafkaException() {
        RepositionEvent event = RepositionEvent.builder().state("FAIL_REPO").build();
        producer.publish(event);

        verify(future).addCallback(successCaptor.capture(), failureCaptor.capture());
        Exception cause = new Exception("topic unreachable");

        assertThatThrownBy(() ->
                failureCaptor.getValue().onFailure(cause)
        ).isInstanceOf(KafkaException.class)
                .hasMessage("topic unreachable");
    }
}
