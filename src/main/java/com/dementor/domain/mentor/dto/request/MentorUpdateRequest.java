package com.dementor.domain.mentor.dto.request;

import com.dementor.domain.mentor.entity.Mentor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class MentorUpdateRequest {
    public record MentorUpdateRequestDto(
            @Positive(message = "경력은 0보다 큰 정수여야 합니다.")
            Integer career,

            @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자만 입력 가능합니다.")
            String phone,

            String currentCompany,

            String stack,

            String introduction,

            String bestFor,

            List<Long> attachmentId
    ) {
        public void applyToMentor(Mentor mentor) {
            mentor.update(
                    currentCompany,
                    career,
                    phone,
                    introduction,
                    bestFor,
                    stack
            );
        }
    }
}
