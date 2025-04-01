package com.dementor.apply.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

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
import com.dementor.domain.apply.exception.ApplyErrorCode;
import com.dementor.domain.apply.exception.ApplyException;
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
	private Member testMember;

	@BeforeEach
	void setUp() {

		testMember = Member.builder()
			.email("test@test.com")
			.password("password")
			.nickname("test")
			.name("test")
			.userRole(UserRole.MENTEE)
			.build();
		testMember = memberRepository.save(testMember);


		MentoringClass mentoring = new MentoringClass();
		mentoring.setTitle("테스트 멘토링");
		mentoring.setContent("테스트 내용");
		mentoring.setPrice(10000);
		mentoring.setStack("Java");


		this.mentoringClass = mentoringClassRepository.save(mentoring);
		this.mentoringClassId = this.mentoringClass.getId();
	}

	@Test
	@DisplayName("멘토링 신청 성공")
	void createApplySuccess() {

		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClassId(mentoringClassId);
		request.setInquiry("테스트 문의입니다");
		request.setSchedule(LocalDateTime.now().plusDays(1));


		ApplyResponse.GetApplyId result = applyService.createApply(request, testMember.getId());


		assertNotNull(result);
		assertNotNull(result.getApplymentId());


		Apply savedApply = applyRepository.findById(result.getApplymentId()).orElse(null);
		assertNotNull(savedApply);
		assertEquals("테스트 문의입니다", savedApply.getInquiry());
		assertEquals(ApplyStatus.PENDING, savedApply.getApplyStatus());
		assertEquals(mentoringClassId, savedApply.getMentoringClass().getId());
	}

	@Test
	@DisplayName("존재하지 않는 멘토링 클래스로 신청 시 예외 발생")
	void createApplyFail1() {

		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClassId(99999L);
		request.setInquiry("테스트 문의입니다");
		request.setSchedule(LocalDateTime.now().plusDays(1));


		assertThrows(IllegalArgumentException.class, () -> {
			applyService.createApply(request, testMember.getId());
		});;
	}

	@Test
	@DisplayName("멘토링 일정을 입력하지 않고 신청 시 예외 발생")
	void createApplyFail2() {

		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClassId(mentoringClassId);
		request.setInquiry("테스트 문의입니다");
		request.setSchedule(null);

		ApplyException exception = assertThrows(ApplyException.class, () -> {
			applyService.createApply(request, testMember.getId());
		});

		assertEquals(ApplyErrorCode.SCHEDULE_REQUIRED, exception.getErrorCode());
		assertEquals("멘토링 일정을 선택해야 합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("멘토링 신청 취소 성공")
	void deleteApplySuccess() {

		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClassId(mentoringClassId);
		request.setInquiry("취소 테스트용 문의입니다");
		request.setSchedule(LocalDateTime.now().plusDays(1));

		ApplyResponse.GetApplyId result = applyService.createApply(request, testMember.getId());
		Long applyId = result.getApplymentId();

		applyService.deleteApply(applyId, testMember.getId());


		Apply deletedApply = applyRepository.findById(applyId).orElse(null);
		assertNull(deletedApply, "멘토링 신청 취소가 되지 않았습니다.");
	}

	@Test
	@DisplayName("권한 없는 사용자가 멘토링 신청 취소 시 예외 발생")
	void deleteApplyFail() {

		ApplyRequest.ApplyCreateRequest request = new ApplyRequest.ApplyCreateRequest();
		request.setClassId(mentoringClassId);
		request.setInquiry("취소 테스트용 문의입니다");
		request.setSchedule(LocalDateTime.now().plusDays(1));

		ApplyResponse.GetApplyId result = applyService.createApply(request, testMember.getId());
		Long applyId = result.getApplymentId();

		Member anotherMember = Member.builder()
			.email("another@test.com")
			.password("password")
			.nickname("another")
			.name("another")
			.userRole(UserRole.MENTEE)
			.build();
		anotherMember = memberRepository.save(anotherMember);

		Member AnotherMember = anotherMember;
		ApplyException exception = assertThrows(ApplyException.class, () -> {
			applyService.deleteApply(applyId, AnotherMember.getId());
		});

		assertEquals(ApplyErrorCode.NOT_YOUR_APPLY, exception.getErrorCode());
	}

}

