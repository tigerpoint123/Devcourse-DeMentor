package com.dementor.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dementor.domain.member.service.MemberService;
import com.dementor.global.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ApiResponse<?> createMember() {
		return ApiResponse.of(true, HttpStatus.CREATED , "Create member");
	}

	@GetMapping("/isEmail")
	public ApiResponse<?> isEmail(@RequestParam("email") String email) {
		boolean isEmail = memberService.isEmail(email);
		return ApiResponse.of(true, HttpStatus.OK, "Email exists", isEmail);
	}

}
