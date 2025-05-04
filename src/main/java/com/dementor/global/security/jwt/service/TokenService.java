package com.dementor.global.security.jwt.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.dementor.global.security.CustomUserDetails;
import com.dementor.global.security.CustomUserDetailsService;
import com.dementor.global.security.jwt.JwtTokenProvider;
import com.dementor.global.security.jwt.RefreshToken_Role;
import com.dementor.global.security.jwt.dto.TokenDto;
import com.dementor.global.security.jwt.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CustomUserDetailsService customUserDetailsService;

	// Member 토큰 생성
	public TokenDto createMemberTokens(Authentication authentication, Long memberId, String nickname) {
		String userEmail = authentication.getName();
		log.info("멤버 토큰 생성 시작 - 이메일: {}, 멤버ID: {}, 닉네임: {}", userEmail, memberId, nickname);

		// 액세스 토큰 생성
		String accessToken = tokenProvider.createMemberToken(authentication, memberId, nickname);
		log.info("액세스 토큰 생성 완료: {}", accessToken);

		// 리프레시 토큰 생성
		String refreshToken = tokenProvider.createRefreshToken(userEmail, RefreshToken_Role.ROLE_MEMBER);
		log.info("리프레시 토큰 생성 완료: {}", refreshToken);

		// Redis에 리프레시 토큰 저장
		refreshTokenRepository.save(
			userEmail,
			refreshToken,
			tokenProvider.getRefreshTokenValidityInMilliseconds()
		);
		log.info("리프레시 토큰 Redis 저장 완료 - 이메일: {}", userEmail);

		return new TokenDto(accessToken, refreshToken);
	}

	// Admin 토큰 생성
	public TokenDto createAdminTokens(Long adminId, String username) {
		log.info("관리자 토큰 생성 시작 - 관리자ID: {}, 사용자명: {}", adminId, username);

		// 액세스 토큰 생성
		String accessToken = tokenProvider.createAdminToken(adminId);
		log.info("관리자 액세스 토큰 생성 완료: {}", accessToken);

		// 리프레시 토큰 생성
		String refreshToken = tokenProvider.createRefreshToken(username, RefreshToken_Role.ROLE_ADMIN);
		log.info("관리자 리프레시 토큰 생성 완료: {}", refreshToken);

		// Redis에 리프레시 토큰 저장
		refreshTokenRepository.save(
			username,
			refreshToken,
			tokenProvider.getRefreshTokenValidityInMilliseconds()
		);
		log.info("관리자 리프레시 토큰 Redis 저장 완료 - 사용자명: {}", username);

		return new TokenDto(accessToken, refreshToken);
	}

	// 리프레시 토큰으로 액세스 토큰 갱신 - 공통 로직
	public TokenDto refreshAccessToken(String refreshToken) {
		log.info("액세스 토큰 갱신 시작 - 리프레시 토큰: {}", refreshToken);

		// 토큰 유효성 검사
		if (!refreshTokenRepository.validateRefreshToken(refreshToken)) {
			log.error("유효하지 않은 리프레시 토큰");
			throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
		}

		// 토큰에서 사용자 식별자 추출
		String userIdentifier = tokenProvider.getUserIdentifierFromRefreshToken(refreshToken);
		log.info("리프레시 토큰에서 사용자 식별자 추출: {}", userIdentifier);

		// Redis에서 사용자의 리프레시 토큰 조회
		String savedToken = refreshTokenRepository.findByUserIdentifier(userIdentifier)
			.orElseThrow(() -> {
				return new RuntimeException("저장된 리프레시 토큰이 없습니다");
			});

		// 저장된 토큰과 받은 토큰이 일치하는지 확인
		if (!savedToken.equals(refreshToken)) {
			throw new RuntimeException("토큰이 일치하지 않습니다");
		}

		Claims claims = Jwts
			.parserBuilder()
			.setSigningKey(tokenProvider.getKey())
			.build()
			.parseClaimsJws(refreshToken)
			.getBody();

		RefreshToken_Role role = RefreshToken_Role.fromRole((String)claims.get("sub"));
		CustomUserDetails userDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(userIdentifier);

		String newAccessToken;

		if (role.equals(RefreshToken_Role.ROLE_ADMIN)) {
			Long adminId = userDetails.getId();
			newAccessToken = tokenProvider.createAdminToken(adminId);
		} else {
			Long memberId = userDetails.getId();
			String nickname = userDetails.getNickname();
			Authentication newAuth = new UsernamePasswordAuthenticationToken(
				userDetails,  // principal
				null,         // credentials (토큰 갱신 시에는 필요 없음)
				userDetails.getAuthorities()  // authorities
			);
			newAccessToken = tokenProvider.createMemberToken(newAuth, memberId, nickname);
		}

		// 리프레시 토큰은 재사용
		return new TokenDto(newAccessToken, refreshToken);
	}

	public void logout(String userEmail) {
		log.info("로그아웃 처리 시작 - 이메일: {}", userEmail);
		refreshTokenRepository.deleteByUserIdentifier(userEmail);
		log.info("로그아웃 처리 완료 - Redis에서 토큰 삭제됨");
	}

}
