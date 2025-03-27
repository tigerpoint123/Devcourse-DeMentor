package com.dementor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@Profile("test") // 테스트 프로필에서만 적용
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/class").permitAll() // 전체 조회는 인증 없이 접근 가능
//                .requestMatchers("/api/class/**").hasRole("MENTOR") // 멘토링 수업 관련 API는 MENTOR 역할 필요
//                .anyRequest().permitAll() // 그 외 모든 요청 허용
            )
            .httpBasic(Customizer.withDefaults()); // 기본 인증 사용

        return http.build();
    }
} 