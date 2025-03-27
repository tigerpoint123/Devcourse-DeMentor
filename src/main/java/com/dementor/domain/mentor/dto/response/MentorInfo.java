package com.dementor.domain.mentor.dto.response;

public class MentorInfo {

    //멘토 정보
    public record MentorInfoResponse(
            Long mentorId,
            String name,
            String job,
            Integer career,
            String phone,
            String stack,
            Boolean isApproved,
            Integer totalClasses
    ) {}
}
