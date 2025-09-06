package com.dementor.domain.screenShare.websocket;

import com.dementor.domain.screenShare.service.ScreenShareService;
import com.dementor.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScreenShareChannelInterceptor implements ChannelInterceptor {

    private final ScreenShareService screenShareService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String SCREEN_TOPIC_PREFIX = "/topic/screen/";
    private static final String SCREEN_APP_PREFIX = "/app/screen/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        // 화면공유 목적지에 한해 SUBSCRIBE/SEND 시 토큰 검증
        if (command == StompCommand.SUBSCRIBE || command == StompCommand.SEND) {
            String destination = accessor.getDestination();
            if (destination == null) return message;
            boolean isScreenDest = destination.startsWith(SCREEN_TOPIC_PREFIX) || destination.startsWith(SCREEN_APP_PREFIX);
            if (!isScreenDest) return message;

            List<String> tokenHeaders = accessor.getNativeHeader("share-token");
            if (tokenHeaders == null || tokenHeaders.isEmpty()) {
                throw new IllegalArgumentException("share-token 헤더가 필요합니다.");
            }
            String token = tokenHeaders.get(0);

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtTokenProvider.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            Long applyId = claims.get("applyId", Long.class);
            Long memberId = claims.get("memberId", Long.class);

            if (applyId == null || memberId == null) {
                throw new IllegalArgumentException("유효하지 않은 화면공유 토큰입니다.");
            }

            if (!screenShareService.isParticipant(applyId, memberId)) {
                throw new IllegalArgumentException("화면공유 참가 권한이 없습니다.");
            }

            // 목적지 경로의 applyId와 토큰의 applyId 일치 검사
            Long destApplyId = extractApplyId(destination);
            if (destApplyId != null && !destApplyId.equals(applyId)) {
                throw new IllegalArgumentException("목적지 방과 토큰의 신청 ID가 일치하지 않습니다.");
            }
        }

        return message;
    }

    private Long extractApplyId(String destination) {
        try {
            if (destination.startsWith(SCREEN_TOPIC_PREFIX)) {
                String id = destination.substring(SCREEN_TOPIC_PREFIX.length());
                return Long.parseLong(id);
            }
            if (destination.startsWith(SCREEN_APP_PREFIX)) {
                String rest = destination.substring(SCREEN_APP_PREFIX.length());
                int slashIdx = rest.indexOf('/');
                String id = slashIdx >= 0 ? rest.substring(0, slashIdx) : rest;
                return Long.parseLong(id);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}


