package com.dementor.domain.admin.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dementor.domain.admin.dto.request.AdminLoginRequest;
import com.dementor.domain.admin.entity.Admin;
import com.dementor.domain.admin.excption.AdminErrorCode;
import com.dementor.domain.admin.excption.AdminException;
import com.dementor.domain.admin.repository.AdminRepository;
import com.dementor.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public String loginAdmin(AdminLoginRequest request) {
		Admin admin = adminRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			throw new AdminException(AdminErrorCode.INVALID_PASSWORD);
		}

		return jwtTokenProvider.createAdminToken(admin.getId());
	}
}
