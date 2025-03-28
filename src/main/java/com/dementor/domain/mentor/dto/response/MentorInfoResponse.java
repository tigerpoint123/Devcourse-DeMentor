package com.dementor.domain.mentor.dto.response;

import com.dementor.domain.mentor.entity.Mentor;

public record MentorInfoResponse (
        Long Id,
        String name,
        String job,
        Integer career,
        String phone,
        String currentCompany,
        String stack,
        String introduction,
        String bestFor,
        Boolean isApproved,
        Integer totalClasses,
        Integer pendingRequests,
        Integer completedSessions
) {
    public static MentorInfoResponse from(Mentor mentor, Integer totalClasses,
                                          Integer pendingRequests, Integer completedSessions) {
        return new MentorInfoResponse(
                mentor.getId(),
                mentor.getName(),
                mentor.getJob().getName(),
                mentor.getCareer(),
                mentor.getPhone(),
                mentor.getCurrentCompany(),
                mentor.getStack(),
                mentor.getIntroduction(),
                mentor.getBestFor(),
                mentor.getIsApproved(),
                totalClasses,
                pendingRequests,
                completedSessions
        );
    }
}
