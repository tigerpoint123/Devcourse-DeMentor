package com.dementor.email.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.dementor.RedisTestContainerConfig;

import jakarta.mail.MessagingException;

@SpringBootTest
public class EmailServiceTest extends RedisTestContainerConfig {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@MockitoBean
	private EmailService emailService;

	@MockitoBean
	private JavaMailSender mailSender;

	@Test
	@DisplayName("인증 코드 생성 및 Redis 저장 테스트")
	void sendVerificationEmailTest() throws MessagingException {

	}
}
