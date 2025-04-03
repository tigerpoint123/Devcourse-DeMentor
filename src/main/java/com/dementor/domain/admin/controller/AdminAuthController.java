package com.dementor.domain.admin.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dementor.domain.admin.dto.request.AdminLoginRequest;
import com.dementor.domain.admin.dto.response.AdminLoginResponse;
import com.dementor.domain.admin.service.AdminService;
import com.dementor.global.security.cookie.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 로그인, 로그아웃", description = "관리자 로그인, 로그아웃")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

	private final AdminService adminService;
	private final AuthenticationManager authenticationManager;
	private final CookieUtil cookieUtil;

	@Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다.")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AdminLoginRequest loginRequest) {
		try {
			// 인증 시도
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginRequest.getUsername(),
					loginRequest.getPassword()
				)
			);

			// Security Context에 인증 정보 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// JWT 토큰 생성
			String jwt = adminService.loginAdmin(loginRequest);

			// 쿠키 생성
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createJwtCookie(jwt).toString());

			return ResponseEntity.ok()
				.headers(headers)
				.body(AdminLoginResponse.builder()
					.message("로그인 성공")
					.token(jwt)
					.build());

		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest()
				.body(AdminLoginResponse.builder()
					.message("로그인 실패: " + e.getMessage())
					.token(null)
					.build());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		SecurityContextHolder.clearContext();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteJwtCookie().toString());

		return ResponseEntity.ok()
			.headers(headers)
			.body(new AdminLoginResponse("로그아웃 성공", null));
	}
}
