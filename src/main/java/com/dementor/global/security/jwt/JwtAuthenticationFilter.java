package com.dementor.global.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final String cookieName;

	//doFilter의 역할은 토큰의 실제 인증정보를 현재 실행중인 securityContext 에 저장하는 역할
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 리프레시 엔드포인트는 토큰 검증 건너뛰기
		if (request.getRequestURI().equals("/api/admin/refresh")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = resolveToken(request);

		// 토큰 유효성 검사 및 인증 설정
		if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) {
			Authentication auth = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(request, response);
	}


	private String resolveToken(HttpServletRequest request) {

		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7).trim(); // 공백 제거
			return token;
		}

		if (request.getCookies() == null) {
			return null;
		}

		Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
			.filter(cookie -> cookieName.equals(cookie.getName()))
			.findFirst();

		return jwtCookie.map(Cookie::getValue).orElse(null);
	}


}
