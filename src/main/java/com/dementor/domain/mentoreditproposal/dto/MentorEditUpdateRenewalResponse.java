package com.dementor.domain.mentoreditproposal.dto;

import com.dementor.domain.mentorapplyproposal.dto.response.AttachmentInfo;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposal;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposalStatus;
import com.dementor.domain.postattachment.entity.PostAttachment;

import java.util.List;

public record MentorEditUpdateRenewalResponse(
	Long id,
	Long memberId,
	MentorEditProposalStatus status,
	String modifiedAt,
	String jobName,
	List<AttachmentInfo> attachments
) {
	public static MentorEditUpdateRenewalResponse from(
			MentorEditProposal mentorEditProposal,
			List<PostAttachment> attachments
	) {
		return new MentorEditUpdateRenewalResponse(
			mentorEditProposal.getId(),
			mentorEditProposal.getMember().getId(),
			mentorEditProposal.getStatus(),
			mentorEditProposal.getModifiedAt().toString(),
			mentorEditProposal.getJob().getName(),
			attachments.stream()
					.map(AttachmentInfo::from)
					.toList()
		);
	}
}
