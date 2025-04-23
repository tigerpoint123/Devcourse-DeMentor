package com.dementor.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@Slf4j
public class StompConnectionEventListener {

    @EventListener(SessionConnectEvent.class)
    public void handleStompConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("=== STOMP CONNECT ===");
        log.info("Session ID: {}", accessor.getSessionId());
        log.info("Headers: {}", accessor.getMessageHeaders());
        log.info("Command: {}", accessor.getCommand());
        log.info("User: {}", accessor.getUser());
    }

    @EventListener(SessionConnectedEvent.class)
    public void handleStompConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("=== STOMP CONNECTED ===");
        log.info("Session ID: {}", accessor.getSessionId());
    }
}