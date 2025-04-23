package com.dementor.global.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Events {
    private final ApplicationEventPublisher eventPublisher;

    public void raise(Object event) {
        eventPublisher.publishEvent(event);
    }
}
