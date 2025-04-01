package com.dementor.domain.mentor.dto.request;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentor.entity.Mentor;
import jakarta.validation.constraints.*;

import java.util.List;

public class MentorApplicationRequest {
    public record MentorApplicationRequestDto(
            @NotNull(message = "사용자 ID는 필수 입력 항목입니다.")
            Long memberId,

            @NotBlank(message = "이름은 필수 입력 항목입니다.")
            String name,

            @NotNull(message = "직무 ID는 필수 입력 항목입니다.")
            Long jobId,

            @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
            @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자만 입력 가능합니다.")
            String phone,

            @NotBlank(message = "이메일은 필수 입력 항목입니다.")
            @Email(message = "유효한 이메일 형식이 아닙니다.")
            String email,

            @NotNull(message = "경력은 필수 입력 항목입니다.")
            @Positive(message = "경력은 0보다 큰 정수여야 합니다.")
            Integer career,

            String currentCompany,

            @NotBlank(message = "소개글은 필수 입력 항목입니다.")
            String introduction,

            String bestFor,

            List<Long> attachmentId
    ) {
            public Mentor toEntity(Member member, Job job) {
                    return Mentor.builder()
                            .member(member)
                            .job(job)
                            .name(name)
                            .phone(phone)
                            .career(career)
                            .currentCompany(currentCompany)
                            .introduction(introduction)
                            .bestFor(bestFor)
                            .approvalStatus(Mentor.ApprovalStatus.PENDING)
                            .build();
            }
    }
}
