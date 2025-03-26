package com.dementor.domain.mentor.dto.request;

import com.dementor.domain.mentor.entity.MentorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MentorRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        private Long jobId;
        private Long userId;
        private String name;
        private Integer career;
        private String phone;
        private String introduction;

        // 파일 첨부 정보
        private AttachmentInfo attachment;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AttachmentInfo {
            private String filename;
            private String storeFilePath;
            private Long fileSize;
        }

        // MentorEntity 변환 메서드
        public MentorEntity toEntity() {
            return MentorEntity.builder()
                    .jobId(jobId)
                    .userId(userId)
                    .name(name)
                    .career(career)
                    .phone(phone)
                    .introduction(introduction)
                    .isApproved(false)
                    .isModified(false)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {
        private Long mentorId;
        private Long jobId;
        private String name;
        private Integer career;
        private String phone;
        private String introduction;

        // 기존 첨부파일 ID 목록 (삭제를 위한)
        private Long removeAttachmentId;
        // 새로운 첨부파일 정보
        private Create.AttachmentInfo newAttachment;

        // MentorEntity 업데이트 메서드
        public MentorEntity updateEntity(MentorEntity entity) {
            return entity.updateInfo(jobId, name, career, phone, introduction);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Approve {
        private Long mentorId;
        private Boolean approve;
    }
}
