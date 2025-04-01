package com.dementor.domain.apply.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dementor.domain.apply.dto.request.ApplyRequest;
import com.dementor.domain.apply.dto.response.ApplyResponse;
import com.dementor.domain.apply.service.ApplyService;
import com.dementor.global.ApiResponse;
import com.dementor.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "멘토링 신청", description = "멘토링 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

	private final ApplyService applyService;


	@Operation(summary = "멘토링 신청", description = "멘토링을 신청합니다")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<?> createApply(
		@RequestBody ApplyRequest.ApplyCreateRequest req,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		ApplyResponse.GetApplyId response = applyService.createApply(req, userDetails.getId());

		return ApiResponse.of(true, HttpStatus.CREATED, "멘토링 신청이 완료되었습니다", response);
	}


	@Operation(summary = "멘토링 신청 취소", description = "멘토링 신청을 취소합니다")
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<?> deleteApply(
		@PathVariable(name = "id") Long applyId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		applyService.deleteApply(applyId, userDetails.getId());

		return ApiResponse.of(true, HttpStatus.OK, "멘토링 신청이 취소되었습니다");
	}

}
