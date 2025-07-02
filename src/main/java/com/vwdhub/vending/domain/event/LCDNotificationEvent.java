package com.vwdhub.vending.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LCDNotificationEvent {
    private final String state;
}

