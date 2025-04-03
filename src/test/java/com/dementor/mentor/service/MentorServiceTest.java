package com.dementor.mentor.service;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.dto.request.MentorApplicationRequest;
import com.dementor.domain.mentor.dto.request.MentorUpdateRequest;
import com.dementor.domain.mentor.dto.response.MentorInfoResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.mentor.service.MentorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MentorServiceTest {

    @Autowired
    private MentorService mentorService;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    private Job testJob;
    private Member testMember;
    private Member testMentorMember;
    private Mentor testMentor;

    @BeforeEach
    void setUp() {
        // 테스트용 일반 회원 생성
        testMember = Member.builder()
                .nickname("testMember")
                .password("password")
                .name("테스트회원")
                .email("test@example.com")
                .userRole(UserRole.MENTEE)
                .build();
        testMember = memberRepository.save(testMember);

        // 테스트용 멘토 회원 생성
        testMentorMember = Member.builder()
                .nickname("testMentor")
                .password("password")
                .name("테스트멘토")
                .email("mentor@example.com")
                .userRole(UserRole.MENTOR)
                .build();
        testMentorMember = memberRepository.save(testMentorMember);

        // 테스트용 직무 생성
        testJob = Job.builder()
                .name("백엔드 개발자")
                .build();
        testJob = jobRepository.save(testJob);

        // 테스트용 멘토 정보 생성 (수정/조회 테스트용)
        testMentor = Mentor.builder()
                .member(testMentorMember)
                .job(testJob)
                .name(testMentorMember.getName())
                .currentCompany("테스트 회사")
                .career(5)
                .phone("01012345678")
                .introduction("테스트 자기소개")
                .bestFor("테스트 특기")
                .approvalStatus(Mentor.ApprovalStatus.APPROVED)
                .modificationStatus(Mentor.ModificationStatus.NONE)
                .build();
        testMentor = mentorRepository.save(testMentor);
    }

    @Test
    @DisplayName("멘토 지원 성공")
    void applyMentorSuccess() {
        // Given
        MentorApplicationRequest.MentorApplicationRequestDto requestDto =
                new MentorApplicationRequest.MentorApplicationRequestDto(
                        testMember.getId(),
                        testMember.getName(),
                        testJob.getId(),
                        "01012345678",
                        "test@example.com",
                        5,
                        "테스트 회사",
                        "테스트 자기소개",
                        "테스트 특기",
                        null
                );

        // When
        mentorService.applyMentor(requestDto);

        // Then
        Mentor savedMentor = mentorRepository.findById(testMember.getId()).orElse(null);
        assertNotNull(savedMentor, "멘토 지원 정보가 저장되지 않았습니다.");
        assertEquals("테스트 자기소개", savedMentor.getIntroduction());
        assertEquals("테스트 특기", savedMentor.getBestFor());
        assertEquals(Mentor.ApprovalStatus.PENDING, savedMentor.getApprovalStatus());
    }

    @Test
    @DisplayName("없는 회원으로 멘토 지원 시 예외 발생")
    void applyMentorFailMemberNotFound() {
        // Given
        Long nonExistingMemberId = 9999L;
        MentorApplicationRequest.MentorApplicationRequestDto requestDto =
                new MentorApplicationRequest.MentorApplicationRequestDto(
                        nonExistingMemberId,
                        "존재하지 않는 회원",
                        testJob.getId(),
                        "01012345678",
                        "nonexistent@example.com",
                        5,
                        "테스트 회사",
                        "테스트 자기소개",
                        "테스트 특기",
                        null
                );

        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorService.applyMentor(requestDto);
        });

        assertTrue(exception.getMessage().contains("회원을 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("이미 멘토인 회원이 멘토 지원 시 예외 발생")
    void applyMentorFailAlreadyMentor() {
        // Given
        MentorApplicationRequest.MentorApplicationRequestDto requestDto =
                new MentorApplicationRequest.MentorApplicationRequestDto(
                        testMentorMember.getId(),
                        testMentorMember.getName(),
                        testJob.getId(),
                        "01012345678",
                        "mentor@example.com",
                        5,
                        "테스트 회사",
                        "테스트 자기소개",
                        "테스트 특기",
                        null
                );

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mentorService.applyMentor(requestDto);
        });

        assertTrue(exception.getMessage().contains("이미 멘토로 등록된 사용자입니다"));
    }

    @Test
    @DisplayName("멘토 정보 수정 성공")
    void updateMentorSuccess() {
        // Given
        MentorUpdateRequest.MentorUpdateRequestDto requestDto =
                new MentorUpdateRequest.MentorUpdateRequestDto(
                        8,          // career
                        "01098765432",    // phone
                        "업데이트 회사",    // currentCompany
                        1L,               // jobId (예시 값)
                        "update@email.com", // email
                        "업데이트된 자기소개", // introduction
                        "업데이트된 특기",   // bestFor
                        null              // attachmentId
                );

        // When
        mentorService.updateMentor(testMentor.getId(), requestDto);

        // Then
        Mentor updatedMentor = mentorRepository.findById(testMentor.getId()).orElse(null);
        assertNotNull(updatedMentor, "멘토 정보가 조회되지 않습니다.");
        assertEquals(Mentor.ModificationStatus.PENDING, updatedMentor.getModificationStatus());
        assertEquals("업데이트된 자기소개", updatedMentor.getIntroduction());
        assertEquals("업데이트된 특기", updatedMentor.getBestFor());
        assertEquals(8, updatedMentor.getCareer());
    }

    @Test
    @DisplayName("존재하지 않는 멘토 정보 수정 시 예외 발생")
    void updateMentorFailMentorNotFound() {
        // Given
        final Long nonExistingMentorId = 9999L;
        MentorUpdateRequest.MentorUpdateRequestDto requestDto =
                new MentorUpdateRequest.MentorUpdateRequestDto(
                        8,          // career
                        "01098765432",    // phone
                        "업데이트 회사",    // currentCompany
                        1L,               // jobId (예시 값)
                        "update@email.com", // email
                        "업데이트된 자기소개", // introduction
                        "업데이트된 특기",   // bestFor
                        null              // attachmentId
                );

        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorService.updateMentor(nonExistingMentorId, requestDto);
        });

        assertTrue(exception.getMessage().contains("멘토를 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("승인되지 않은 멘토 정보 수정 시 예외 발생")
    void updateMentorFailNotApproved() {
        // Given
        Member unapprovedMember = Member.builder()
                .nickname("unapprovedMember")
                .password("password")
                .name("미승인회원")
                .email("unapproved@example.com")
                .userRole(UserRole.MENTEE)
                .build();
        unapprovedMember = memberRepository.save(unapprovedMember);

        Mentor unapprovedMentor = Mentor.builder()
                .member(unapprovedMember)
                .job(testJob)
                .name(unapprovedMember.getName())
                .currentCompany("테스트 회사")
                .career(5)
                .phone("01012345678")
                .introduction("테스트 자기소개")
                .bestFor("테스트 특기")
                .approvalStatus(Mentor.ApprovalStatus.PENDING)
                .modificationStatus(Mentor.ModificationStatus.NONE)
                .build();
        unapprovedMentor = mentorRepository.save(unapprovedMentor);

        final Long unapprovedMentorId = unapprovedMentor.getId();

        MentorUpdateRequest.MentorUpdateRequestDto requestDto =
                new MentorUpdateRequest.MentorUpdateRequestDto(
                        8,          // career
                        "01098765432",    // phone
                        "업데이트 회사",    // currentCompany
                        1L,               // jobId (예시 값)
                        "update@email.com", // email
                        "업데이트된 자기소개", // introduction
                        "업데이트된 특기",   // bestFor
                        null              // attachmentId
                );

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mentorService.updateMentor(unapprovedMentorId, requestDto);
        });

        assertTrue(exception.getMessage().contains("승인되지 않은 멘토는 정보를 수정할 수 없습니다"));
    }

    @Test
    @DisplayName("이미 수정 요청 중인 멘토 정보 수정 시 예외 발생")
    void updateMentorFailAlreadyPending() {
        // Given
        Member pendingMember = Member.builder()
                .nickname("pendingMember")
                .password("password")
                .name("수정중회원")
                .email("pending@example.com")
                .userRole(UserRole.MENTOR)
                .build();
        pendingMember = memberRepository.save(pendingMember);

        Mentor pendingMentor = Mentor.builder()
                .member(pendingMember)
                .job(testJob)
                .name(pendingMember.getName())
                .currentCompany("테스트 회사")
                .career(5)
                .phone("01012345678")
                .introduction("테스트 자기소개")
                .bestFor("테스트 특기")
                .approvalStatus(Mentor.ApprovalStatus.APPROVED)
                .modificationStatus(Mentor.ModificationStatus.PENDING)
                .build();
        pendingMentor = mentorRepository.save(pendingMentor);

        final Long pendingMentorId = pendingMentor.getId();

        MentorUpdateRequest.MentorUpdateRequestDto requestDto =
                new MentorUpdateRequest.MentorUpdateRequestDto(
                        8,          // career
                        "01098765432",    // phone
                        "업데이트 회사",    // currentCompany
                        1L,               // jobId (예시 값)
                        "update@email.com", // email
                        "업데이트된 자기소개", // introduction
                        "업데이트된 특기",   // bestFor
                        null              // attachmentId
                );

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mentorService.updateMentor(pendingMentorId, requestDto);
        });

        assertTrue(exception.getMessage().contains("이미 정보 수정 요청 중입니다"));
    }

    @Test
    @DisplayName("멘토 정보 조회 성공")
    void getMentorInfoSuccess() {
        // Given
        Long mentorId = testMentor.getId();

        // When
        MentorInfoResponse response = mentorService.getMentorInfo(mentorId);

        // Then
        assertNotNull(response, "멘토 정보가 조회되지 않습니다");
        assertEquals(testMentor.getId(), response.Id());
        assertEquals("테스트멘토", response.name());
        assertEquals("백엔드 개발자", response.job());
        assertEquals(5, response.career());
        assertEquals("01012345678", response.phone());
        assertEquals("테스트 회사", response.currentCompany());
        assertEquals("테스트 자기소개", response.introduction());
        assertEquals("테스트 특기", response.bestFor());
        assertEquals(Mentor.ApprovalStatus.APPROVED, response.approvalStatus());
    }

    @Test
    @DisplayName("존재하지 않는 멘토 정보 조회 시 예외 발생")
    void getMentorInfoFailMentorNotFound() {
        // Given
        final Long nonExistingMentorId = 9999L;

        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            mentorService.getMentorInfo(nonExistingMentorId);
        });

        assertTrue(exception.getMessage().contains("해당 멘토를 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("승인되지 않은 멘토 정보 조회 시 예외 발생")
    void getMentorInfoFailNotApproved() {
        // Given
        Member unapprovedMember = Member.builder()
                .nickname("unapprovedMember2")
                .password("password")
                .name("미승인회원2")
                .email("unapproved2@example.com")
                .userRole(UserRole.MENTEE)
                .build();
        unapprovedMember = memberRepository.save(unapprovedMember);

        Mentor unapprovedMentor = Mentor.builder()
                .member(unapprovedMember)
                .job(testJob)
                .name(unapprovedMember.getName())
                .currentCompany("테스트 회사")
                .career(5)
                .phone("01012345678")
                .introduction("테스트 자기소개")
                .bestFor("테스트 특기")
                .approvalStatus(Mentor.ApprovalStatus.PENDING)
                .modificationStatus(Mentor.ModificationStatus.NONE)
                .build();
        unapprovedMentor = mentorRepository.save(unapprovedMentor);

        final Long unapprovedMentorId = unapprovedMentor.getId();

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mentorService.getMentorInfo(unapprovedMentorId);
        });

        assertTrue(exception.getMessage().contains("승인되지 않은 멘토 정보는 조회할 수 없습니다"));
    }
}