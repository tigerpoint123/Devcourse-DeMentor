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

            Mentor.ModificationStatus modificationStatus,

            List<Long> attachmentId
    ) {
        public void updateMentor(Mentor mentor) {
            mentor.update(
                    currentCompany,
                    career,
                    phone,
                    introduction,
                    bestFor
            );

            // 수정 상태를 PENDING으로 변경
            mentor.updateModificationStatus(Mentor.ModificationStatus.PENDING);

            // stack 업데이트 로직
            if (this.stack() != null && !this.stack().isEmpty()) {
                // 멘토링 클래스가 있는 경우, 모든 클래스에 동일한 스택 정보 적용
                // 또는 첫 번째 클래스만 업데이트하거나, 특정 로직에 따라 처리
                if (mentor.getMentorings() != null && !mentor.getMentorings().isEmpty()) {
                    mentor.getMentorings().forEach(mc -> mc.setStack(this.stack()));
                }
            }
        }
    }
}
