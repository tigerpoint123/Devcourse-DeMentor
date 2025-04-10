package com.dementor.domain.mentoreditproposal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dementor.domain.mentor.dto.response.MentorChangeResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.mentoreditproposal.dto.MentorEditFindAllRenewalResponse;
import com.dementor.domain.mentoreditproposal.dto.MentorEditUpdateRenewalResponse;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposal;
import com.dementor.domain.mentoreditproposal.entity.MentorEditProposalStatus;
import com.dementor.domain.mentoreditproposal.repository.AdminModificationRepository;
import com.dementor.domain.mentoreditproposal.repository.MentorEditProposalRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminModificationService {
	private AdminModificationRepository adminModificationRepository;
	private final MentorEditProposalRepository mentorEditProposalRepository;
	private final MentorRepository mentorRepository;

	public Page<MentorEditFindAllRenewalResponse> findAllModificationRequest(Pageable pageable) {
		Page<MentorEditProposal> mentorEditProposalPage;

		mentorEditProposalPage = adminModificationRepository.findAll(pageable);

		return mentorEditProposalPage.map(MentorEditFindAllRenewalResponse::from);
	}

	public MentorEditUpdateRenewalResponse approveMentorUpdate(Long memberId) {
		MentorEditProposal mentorEditProposal = mentorEditProposalRepository.findOneRequestByMemberId(memberId);

		Mentor mentor = mentorRepository.findByMemberId(memberId)
			.orElseThrow(() -> new EntityNotFoundException("Mentor not found"));

		mentor.update(
			mentorEditProposal.getCurrentCompany(),
			mentorEditProposal.getCareer(),
			// mentorEditProposal.getPhone,
			// mentorEditProposal.getEmail,
			mentorEditProposal.getIntroduction()
			);

		mentor = Mentor.builder().build();
		mentorEditProposal.updateStatus(MentorEditProposalStatus.APPROVED);
		mentorRepository.save(mentor);
		
		return null;
	}

}
