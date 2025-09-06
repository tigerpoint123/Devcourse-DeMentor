package com.dementor.domain.screenShare.controller;

import com.dementor.domain.apply.dto.response.ApplyIdResponse;
import com.dementor.domain.screenShare.dto.ScreenShareTokenRequest;
import com.dementor.domain.screenShare.dto.ScreenShareTokenResponse;
import com.dementor.domain.screenShare.service.ScreenShareService;
import com.dementor.global.ApiResponse;
import com.dementor.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screen-share")
@RequiredArgsConstructor
public class ScreenShareController {

    private final ScreenShareService screenShareService;

    @Operation(summary = "화면공유 토큰 발급", description = "applyId 기반으로 멘토/멘티 당사자에게만 화면공유 토큰을 발급합니다.")
    @PostMapping("/token")
    public ApiResponse<ScreenShareTokenResponse> issueToken(
        @RequestBody ScreenShareTokenRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        ScreenShareTokenResponse response = screenShareService.createShareToken(request.applyId(), user.getId());
        return ApiResponse.of(true, HttpStatus.OK, "화면공유 토큰 발급", response);
    }

    @Operation(summary = "화면공유 참가자 ID 조회", description = "applyId 기반으로 신청자(멘티)와 멘토의 ID를 반환합니다.")
    @GetMapping("/participants/{applyId}")
    public ApiResponse<ApplyIdResponse> getParticipants(
        @PathVariable Long applyId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        ApplyIdResponse response = screenShareService.getApplyParticipants(applyId, user.getId());
        return ApiResponse.of(true, HttpStatus.OK, "참가자 ID 조회", response);
    }
}


