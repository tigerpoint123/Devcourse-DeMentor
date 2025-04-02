package com.dementor.domain.mentor.controller;

import com.dementor.domain.mentor.dto.request.MentorApplicationRequest;
import com.dementor.domain.mentor.dto.request.MentorUpdateRequest;
import com.dementor.domain.mentor.dto.response.MentorInfoResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.service.MentorService;
import com.dementor.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
@Tag(name = "멘토 API", description = "멘토 지원, 정보 수정, 조회 API")
public class MentorController {
    private final MentorService mentorService;

    @PostMapping
    @Operation(summary = "멘토 지원", description = "새로운 멘토 지원 API")
    public ResponseEntity<ApiResponse<?>> applyMentor(
            @RequestBody @Valid MentorApplicationRequest.MentorApplicationRequestDto requestDto) {
        mentorService.applyMentor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(true, HttpStatus.CREATED, "멘토 지원에 성공했습니다."));
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "멘토 정보 수정", description = "멘토 정보 수정 API")
    public ResponseEntity<ApiResponse<?>> updateMentor(
            @PathVariable Long memberId,
            @RequestBody @Valid MentorUpdateRequest.MentorUpdateRequestDto requestDto) {
        mentorService.updateMentor(memberId, requestDto);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("memberId", memberId);
        responseData.put("modificationStatus", Mentor.ModificationStatus.PENDING.name());

        return ResponseEntity.ok()
                .body(ApiResponse.of(true, HttpStatus.OK, "멘토 정보 수정 요청에 성공했습니다.", responseData));
    }

    @GetMapping("/{memberId}/info")
    @Operation(summary = "멘토 정보 조회", description = "특정 멘토의 상세 정보 조회 API")
    public ResponseEntity<ApiResponse<?>> getMentorInfo(@PathVariable Long memberId) {
        try {
            MentorInfoResponse mentorInfo = mentorService.getMentorInfo(memberId);
            Map<String, Object> responseData = getResponseData(mentorInfo);

            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "대시보드 조회에 성공했습니다.", responseData));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.of(false, HttpStatus.NOT_FOUND, e.getMessage()));
        }
    }

    private static Map<String, Object> getResponseData(MentorInfoResponse mentorInfo) {
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("memberId", mentorInfo.Id());
        memberInfo.put("name", mentorInfo.name());
        memberInfo.put("job", mentorInfo.job());
        memberInfo.put("career", mentorInfo.career());
        memberInfo.put("phone", mentorInfo.phone());
        memberInfo.put("currentCompany", mentorInfo.currentCompany());
        memberInfo.put("introduction", mentorInfo.introduction());
        memberInfo.put("bestFor", mentorInfo.bestFor());
        memberInfo.put("isApproved", mentorInfo.approvalStatus() == com.dementor.domain.mentor.entity.Mentor.ApprovalStatus.APPROVED);
        memberInfo.put("totalClasses", mentorInfo.totalClasses());
        memberInfo.put("pendingRequests", mentorInfo.pendingRequests());
        memberInfo.put("completedSessions", mentorInfo.completedSessions());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("memberInfo", memberInfo);
        return responseData;
    }
}
