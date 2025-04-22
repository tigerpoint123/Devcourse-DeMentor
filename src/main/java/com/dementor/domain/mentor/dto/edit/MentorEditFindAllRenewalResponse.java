package com.dementor.domain.mentor.dto.edit;

import com.dementor.domain.mentor.entity.MentorEditProposal;
import com.dementor.domain.mentor.entity.MentorEditProposalStatus;

public record MentorEditFindAllRenewalResponse(
	Long id,
	Long memberId,
	String memberName,
	MentorEditProposalStatus status,
	String createdAt,
	Integer career,
	String currentCompany,
	String introduction,
	String jobName
) {
	public static MentorEditFindAllRenewalResponse from(
			MentorEditProposal mentorEditProposal
	) {
		return new MentorEditFindAllRenewalResponse(
			mentorEditProposal.getId(),
			mentorEditProposal.getMember().getId(),
			mentorEditProposal.getMember().getName(),
			mentorEditProposal.getStatus(),
			mentorEditProposal.getCreatedAt().toString(),
			mentorEditProposal.getCareer(),
			mentorEditProposal.getCurrentCompany(),
			mentorEditProposal.getIntroduction(),
			mentorEditProposal.getJob().getName()
		);
	}
}
