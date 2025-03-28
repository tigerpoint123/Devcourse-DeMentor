package com.dementor.apply.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dementor.domain.apply.dto.request.ApplyRequest;
import com.dementor.domain.apply.dto.response.ApplyResponse;
import com.dementor.domain.apply.entity.Apply;
import com.dementor.domain.apply.entity.ApplyStatus;
import com.dementor.domain.apply.repository.ApplyRepository;
import com.dementor.domain.apply.service.ApplyService;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ApplyServiceTest {

	@Autowired
	private ApplyService applyService;

	@Autowired
	private MentoringClassRepository mentoringClassRepository;

	@Autowired
	private ApplyRepository applyRepository;

	@Autowired
	private MemberRepository memberRepository;

	private MentoringClass mentoringClass;
	private Long mentoringClassId;

	@BeforeEach
	void setUp() {
		// 멤버 생성 및 저장
		Member member = Member.builder()
			.email("test@test.com")
			.password("password")
			.nickname("tester")
			.userRole(UserRole.MENTEE)
			.build();
		memberRepository.save(member);

		// MentoringClass 생성 (생성자 사용)
		MentoringClass mentoring = new MentoringClass();
		mentoring.setTitle("테스트 멘토링");
		mentoring.setContent("테스트 내용");
		mentoring.setPrice(10000);
		mentoring.setStack("Java");
		// 필요한 다른 필드 설정

		// 저장
		this.mentoringClass = mentoringClassRepository.save(mentoring);
	}

	@Test
	@DisplayName("멘토링 신청 성공")
	void createApplySuccess() {
		// Given
		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClass_id(mentoringClassId);
		request.setInquiry("테스트 문의입니다");

		// When
		ApplyResponse.GetApplyId result = applyService.createApply(request, 1L);

		// Then
		assertNotNull(result);
		assertNotNull(result.getApplyment_id());

		// 저장된 정보 확인
		Apply savedApply = applyRepository.findById(result.getApplyment_id()).orElse(null);
		assertNotNull(savedApply);
		assertEquals("테스트 문의입니다", savedApply.getInquiry());
		assertEquals(ApplyStatus.PENDING, savedApply.getApplyStatus());
		assertEquals(mentoringClassId, savedApply.getMentoringClass().getId());
	}

	@Test
	@DisplayName("존재하지 않는 멘토링 클래스로 신청 시 예외 발생")
	void createApplyFailWithInvalidClassId() {
		// Given
		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClass_id(99999L); // 존재하지 않는 ID
		request.setInquiry("테스트 문의입니다");

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> {
			applyService.createApply(request, 1L);
		});
	}

	@Test
	@DisplayName("문의 내용 없이 신청 시 실패")
	void createApplyFailWithoutInquiry() {
		// Given
		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClass_id(mentoringClassId);
		request.setInquiry(null); // 문의 내용 누락

		// When & Then
		assertThrows(Exception.class, () -> {
			applyService.createApply(request, 1L);
		});
	}

	@AfterEach
	void tearDown() {
		// 테스트 후 정리 (필요한 경우)
		applyRepository.deleteAll();
		mentoringClassRepository.delete(mentoringClass);
	}
}
