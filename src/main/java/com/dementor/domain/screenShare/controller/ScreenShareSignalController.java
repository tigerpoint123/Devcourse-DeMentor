package com.dementor.domain.screenShare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ScreenShareSignalController {

    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트 -> 서버: SDP/ICE 신호 수신, 서버 -> 구독자 브로드캐스트
    @MessageMapping("/screen/{applyId}/signal")
    public void relaySignal(@DestinationVariable Long applyId, String payload) {
        // 동일 방(topic)에 브로드캐스트
        messagingTemplate.convertAndSend("/topic/screen/" + applyId, payload);
    }
}


