package com.dementor.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private MemberRepository memberRepository;

	@MockitoBean
	private MemberService memberService;


	@BeforeEach
	void setUp() {
		memberRepository.save(
			Member.builder()
				.email("mentee@test.com")
				.nickname("testMentee")
				.password(passwordEncoder.encode("1234")) // 실제 테스트에서는 암호화된 값 사용 추천
				.userRole(UserRole.MENTEE) // ENUM 타입
				.build()
		);
	}

	@Test
	@DisplayName("이메일 중복확인 - 사용 가능한 이메일")
	public void testIsEmailSuccess() throws Exception {
		// given
		String email = "test@example.com";
		when(memberService.isEmail(email)).thenReturn(true);

		// when & then
		mvc.perform(get("/api/signup/isEmail")
				.param("email", email))
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("Email exists"))
			.andExpect(jsonPath("$.data").value(true));
	}

	@Test
	@DisplayName("이메일 중복확인 - 이미 존재하는 이메일")
	public void testIsEmailDuplicate() throws Exception {
		// given
		String email = "mentee@test.com";
		when(memberService.isEmail(email))
			.thenThrow(new MemberException(MemberErrorCode.DUPLICATE_EMAIL));

		// when & then
		mvc.perform(get("/api/signup/isEmail")
				.param("email", email))
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("409"))
			.andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다"));
	}

	@Test
	@DisplayName("닉네임 중복확인 - 사용 가능한 닉네임")
	public void testIsNicknameSuccess() throws Exception {
		// given
		String nickname = "hong";
		when(memberService.isNickname(nickname)).thenReturn(true);

		// when & then
		mvc.perform(get("/api/signup/isNickname")
				.param("nickname", nickname))
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("200"))
			.andExpect(jsonPath("$.message").value("Nickname exists"))
			.andExpect(jsonPath("$.data").value(true));
	}

	@Test
	@DisplayName("이메일 중복확인 - 이미 존재하는 이메일")
	public void testIsNicknameDuplicate() throws Exception {
		// given
		String nickname = "testMentee";
		when(memberService.isNickname(nickname))
			.thenThrow(new MemberException(MemberErrorCode.DUPLICATE_NICKNAME));

		// when & then
		mvc.perform(get("/api/signup/isNickname")
				.param("nickname", nickname))
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("409"))
			.andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다"));
	}

}

