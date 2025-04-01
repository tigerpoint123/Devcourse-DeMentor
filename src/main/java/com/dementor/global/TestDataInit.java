package com.dementor.global;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Profile("local") // 로컬 환경에서만 실행되도록
public class TestDataInit implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;
    private final MentorRepository mentorRepository;

    public TestDataInit(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JobRepository jobRepository, MentorRepository mentorRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jobRepository = jobRepository;
        this.mentorRepository = mentorRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 기존 데이터가 없을 때만 생성
        if (memberRepository.count() == 0) {
            Job job = Job.builder()
                    .name("백엔드 개발자")
                    .build();
            job = jobRepository.save(job);

            Member testMentor = Member.builder()
                    .email("mentor@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .nickname("테스트멘토")
                    .name("TEST_NAME")
                    .userRole(UserRole.MENTOR)
                    .build();

            Member testMentee = Member.builder()
                    .email("mentee@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .nickname("테스트멘티")
                    .name("TEST_NAME")
                    .userRole(UserRole.MENTEE)
                    .build();

            // 테스트 멘토의 멘토 정보 생성
            Mentor mentorInfo = Mentor.builder()
                    .member(testMentor)
                    .name(testMentor.getName())
                    .currentCompany("테스트회사")
                    .career(5)
                    .phone("010-1234-5678")
                    .introduction("테스트 멘토 소개입니다. 경력 5년차 개발자입니다.")
                    .bestFor("코딩 테스트, 알고리즘, 백엔드 개발")
                    .approvalStatus(Mentor.ApprovalStatus.APPROVED) // 승인 상태로 설정
                    .job(job)
                    .build();

			memberRepository.save(testMentor);
			memberRepository.save(testMentee);
			mentorRepository.save(mentorInfo);
		}
	}
}