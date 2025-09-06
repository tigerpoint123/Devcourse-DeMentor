package com.dementor.domain.screenShare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dementor.domain.apply.entity.Apply;
import com.dementor.domain.apply.entity.ApplyStatus;
import com.dementor.domain.apply.repository.ApplyRepository;
import com.dementor.domain.apply.dto.response.ApplyIdResponse;
import com.dementor.domain.screenShare.dto.ScreenShareTokenResponse;
import com.dementor.global.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Transactional(readOnly = true)
public class ScreenShareService {

    private final ApplyRepository applyRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final long screenShareTokenValidityMs;

    public ScreenShareService(
        ApplyRepository applyRepository,
        JwtTokenProvider jwtTokenProvider,
        @Value("${screen-share.token.expiration-ms:600000}") long screenShareTokenValidityMs
    ) {
        this.applyRepository = applyRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.screenShareTokenValidityMs = screenShareTokenValidityMs;
    }

    public ScreenShareTokenResponse createShareToken(Long applyId, Long requesterMemberId) {
        Apply apply = applyRepository.findById(applyId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        Long menteeId = apply.getMember().getId();
        Long mentorId = apply.getMentoringClass().getMentor().getId();

        if (!requesterMemberId.equals(menteeId) && !requesterMemberId.equals(mentorId)) {
            throw new SecurityException("화면공유 권한이 없습니다.");
        }

        if (apply.getApplyStatus() != ApplyStatus.APPROVED) {
            throw new SecurityException("승인된 신청에서만 화면공유가 가능합니다.");
        }

        long now = System.currentTimeMillis();
        String token = Jwts.builder()
            .setSubject("screen-share")
            .claim("applyId", applyId)
            .claim("memberId", requesterMemberId)
            .setIssuedAt(new java.util.Date(now))
            .setExpiration(new java.util.Date(now + screenShareTokenValidityMs))
            .signWith(jwtTokenProvider.getKey(), SignatureAlgorithm.HS512)
            .compact();

        List<ScreenShareTokenResponse.IceServer> iceServers = List.of(
            new ScreenShareTokenResponse.IceServer("stun:stun.l.google.com:19302", null, null)
        );

        return new ScreenShareTokenResponse(token, iceServers, applyId);
    }

    public boolean isParticipant(Long applyId, Long memberId) {
        Optional<Apply> applyOpt = applyRepository.findById(applyId);
        if (applyOpt.isEmpty()) return false;
        Apply apply = applyOpt.get();
        Long menteeId = apply.getMember().getId();
        Long mentorId = apply.getMentoringClass().getMentor().getId();
        return memberId.equals(menteeId) || memberId.equals(mentorId);
    }

    // applyId 기반으로 신청자(멘티)와 멘토의 ID를 반환
    public ApplyIdResponse getApplyParticipants(Long applyId, Long requesterMemberId) {
        Apply apply = applyRepository.findById(applyId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        Long menteeId = apply.getMember().getId();
        Long mentorId = apply.getMentoringClass().getMentor().getId();

        if (!requesterMemberId.equals(menteeId) && !requesterMemberId.equals(mentorId)) {
            throw new SecurityException("조회 권한이 없습니다.");
        }

        return ApplyIdResponse.builder()
            .applyId(apply.getId())
            .mentorId(mentorId)
            .menteeId(menteeId)
            .chatRoomId(null)
            .build();
    }
}


