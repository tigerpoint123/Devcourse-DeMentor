package com.dementor.domain.member.service;

import org.springframework.stereotype.Service;

import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public boolean isEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
		});
		return true;
	}

	public boolean isNickname(String nickname) {
		memberRepository.findByNickname(nickname).ifPresent(member -> {
			throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
		});
		return true;
	}
}
