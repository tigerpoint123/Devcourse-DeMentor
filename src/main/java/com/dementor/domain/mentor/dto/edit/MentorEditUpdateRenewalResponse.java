package com.dementor.domain.mentor.dto.edit;

import com.dementor.domain.mentor.entity.MentorEditProposal;
import com.dementor.domain.mentor.entity.MentorEditProposalStatus;

public record MentorEditUpdateRenewalResponse(
	Long id,
	Long memberId,
	MentorEditProposalStatus status,
	String modifiedAt,
	String jobName
) {
	public static MentorEditUpdateRenewalResponse from(
			MentorEditProposal mentorEditProposal
	) {
		return new MentorEditUpdateRenewalResponse(
			mentorEditProposal.getId(),
			mentorEditProposal.getMember().getId(),
			mentorEditProposal.getStatus(),
			mentorEditProposal.getModifiedAt().toString(),
			mentorEditProposal.getJob().getName()
		);
	}
}
