package com.dementor.domain.mentor.controller;

import com.dementor.domain.mentor.dto.request.MentorApplicationRequest;
import com.dementor.domain.mentor.dto.request.MentorUpdateRequest;
import com.dementor.domain.mentor.dto.response.MentorInfoResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.service.MentorService;
import com.dementor.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;

    //멘토 지원하기 API
    @PostMapping
    public ResponseEntity<ApiResponse<?>> applyMentor(
            @RequestBody @Valid MentorApplicationRequest.MentorApplicationRequestDto requestDto,
            BindingResult bindingResult) {

        // 유효성 검사 실패 시 에러 응답
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> {
                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put("field", error.getField());
                        errorMap.put("message", error.getDefaultMessage());
                        return errorMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.of(false,  HttpStatus.BAD_REQUEST,"멘토 지원에 실패했습니다.", errors));
        }

        try {
            mentorService.applyMentor(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.of(true, HttpStatus.CREATED, "멘토 지원에 성공했습니다."));
        } catch (Exception e) {
            if (e.getMessage().contains("이미 멘토로 등록된 사용자")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.of(false, HttpStatus.CONFLICT, "이미 멘토로 등록된 사용자입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
        }
    }

    //멘토 정보 수정 API
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<?>> updateMentor(
            @PathVariable Long memberId,
            @RequestBody @Valid MentorUpdateRequest.MentorUpdateRequestDto requestDto,
            BindingResult bindingResult) {

        // 유효성 검사 실패 시 에러 응답
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> {
                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put("field", error.getField());
                        errorMap.put("message", error.getDefaultMessage());
                        return errorMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, "멘토 정보 수정 요청에 실패했습니다.", errors));
        }

        try {
            mentorService.updateMentor(memberId, requestDto);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("memberId", memberId);
            responseData.put("modificationStatus", Mentor.ModificationStatus.PENDING.name());

            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "멘토 정보 수정 요청에 성공했습니다.", responseData));
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, e.getMessage()));
            } else if (e.getMessage().contains("권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.of(false, HttpStatus.FORBIDDEN, "해당 멘토 정보를 수정할 권한이 없습니다."));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.of(false, HttpStatus.NOT_FOUND, "해당 멘토를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
        }
    }

    //멘토 정보 조회 API
    @GetMapping("/{memberId}/info")
    public ResponseEntity<ApiResponse<?>> getMentorInfo(@PathVariable Long memberId) {
        try {
            MentorInfoResponse mentorInfo = mentorService.getMentorInfo(memberId);

            Map<String, Object> responseData = getResponseData(mentorInfo);

            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "대시보드 조회에 성공했습니다.", responseData));
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, "멘토 정보 조회에 실패했습니다.", "유효하지 않은 멘토 ID입니다."));
            } else if (e.getMessage().contains("인증이 필요합니다")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.of(false, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."));
            } else if (e.getMessage().contains("접근 권한이 없습니다")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.of(false, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.of(false, HttpStatus.NOT_FOUND, "해당 멘토를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
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
