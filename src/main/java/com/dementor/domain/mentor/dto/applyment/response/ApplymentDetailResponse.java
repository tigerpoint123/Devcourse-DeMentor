package com.dementor.domain.mentor.dto.applyment.response;

import com.dementor.domain.mentor.entity.MentorApplyProposal;
import com.dementor.domain.postattachment.entity.PostAttachment;

import java.util.List;

public record ApplymentDetailResponse(
	ApplymentInfo applymentInfo
) {
	public static ApplymentDetailResponse from(
		MentorApplyProposal applyment,
		List<PostAttachment> attachments
	) {
		return new ApplymentDetailResponse(
			ApplymentInfo.from(applyment, attachments)
		);
	}
}
