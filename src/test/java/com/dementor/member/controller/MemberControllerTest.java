package com.dementor.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;


	@Test
	@DisplayName("이메일 중복확인 - 사용 가능한 이메일")
	public void testIsEmailSuccess() throws Exception {
		// given
		String email = "test@example.com";
		when(memberService.isEmail(email)).thenReturn(true);

		// when & then
		mvc.perform(get("/api/member/isEmail")
				.param("email", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("Email exists"))
			.andExpect(jsonPath("$.data").value(true));
	}

	// @Test
	// @DisplayName("이메일 중복확인 - 이미 존재하는 이메일")
	// public void testIsEmailDuplicate() throws Exception {
	//
	// 	// given
	// 	String email = "mentee@test.com";
	// 	when(memberService.isEmail(email))
	// 		.thenThrow(new MemberException(MemberErrorCode.DUPLICATE_EMAIL));
	//
	// 	// when & then
	// 	mvc.perform(get("/api/member/isEmail")
	// 			.param("email", email))
	// 		.andExpect(status().isConflict())
	// 		.andExpect(jsonPath("$.isSuccess").value(false))
	// 		.andExpect(jsonPath("$.code").value("409"))
	// 		.andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다"));
	// }
}

