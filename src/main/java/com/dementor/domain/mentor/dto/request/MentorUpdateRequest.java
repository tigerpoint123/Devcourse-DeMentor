package com.dementor.domain.mentor.dto.request;

import com.dementor.domain.mentor.entity.Mentor;
import jakarta.validation.constraints.Email;
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

            Long jobId,

            @Email(message = "유효한 이메일 형식이 아닙니다.")
            String email,

            String introduction,

            String bestFor,

            List<Long> attachmentId
    ) {
        //멘토 엔티티 업데이트 (실제 업데이트는 수정 요청 승인 후 적용)
        public void updateMentor(Mentor mentor) {
            // 수정 상태를 PENDING으로 변경
            mentor.updateModificationStatus(Mentor.ModificationStatus.PENDING);
            // 이메일은 멤버의 필드이므로 멘토 수정 승인 시 별도로 처리해야 합니다.
        }

        //변경된 필드가 있는지 확인
        public boolean hasChanges(Mentor mentor) {
            return (career != null && !career.equals(mentor.getCareer())) ||
                    (phone != null && !phone.equals(mentor.getPhone())) ||
                    (currentCompany != null && !currentCompany.equals(mentor.getCurrentCompany())) ||
                    (jobId != null && !jobId.equals(mentor.getJob().getId())) ||
                    (email != null && !email.equals(mentor.getMember().getEmail())) ||
                    (introduction != null && !introduction.equals(mentor.getIntroduction())) ||
                    (bestFor != null && !bestFor.equals(mentor.getBestFor()));
        }
    }
}
