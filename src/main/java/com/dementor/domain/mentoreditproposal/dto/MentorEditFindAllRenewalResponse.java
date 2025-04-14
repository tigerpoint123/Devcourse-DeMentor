package com.dementor.domain.mentoreditproposal.dto;

import com.dementor.domain.mentorapplyproposal.dto.response.AttachmentInfo;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposal;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposalStatus;
import com.dementor.domain.postattachment.entity.PostAttachment;

import java.util.List;

public record MentorEditFindAllRenewalResponse(
	Long id,
	Long memberId,
	String memberName,
	MentorEditProposalStatus status,
	String createdAt,
	Integer career,
	String currentCompany,
	String introduction,
	List<AttachmentInfo> attachments
) {
	public static MentorEditFindAllRenewalResponse from(
			MentorEditProposal mentorEditProposal,
			List<PostAttachment> attachments
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
			attachments.stream()
					.map(AttachmentInfo::from)
					.toList()
		);
	}
}
