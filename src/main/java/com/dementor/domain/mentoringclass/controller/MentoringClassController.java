package com.dementor.domain.mentoringclass.controller;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassUpdateResponse;
import com.dementor.domain.mentoringclass.service.MentoringClassService;
import com.dementor.global.ApiResponse;
import com.dementor.global.common.pagination.PaginationUtil;
import com.dementor.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "멘토링 수업", description = "멘토링 수업 관리")
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@Slf4j
public class MentoringClassController {
    private final MentoringClassService mentoringClassService;

    @Operation(summary = "멘토링 수업 전체 조회", description = "모든 멘토링 수업을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MentoringClassFindResponse>>> getClass(
            @RequestParam(required = false) List<String> jobId,
            @Parameter(description = "페이지 정보", example = """
                {
                  "page": 1,
                  "size": 10,
                  "sort": "id,desc"
                }
                """)
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Pageable domainPageable = PaginationUtil.getMentoringClassPageable(pageable);

        // String List를 Long List로 변환
        List<Long> jobIds = jobId != null ?
                jobId.stream()
                        .map(Long::parseLong)
                        .toList() :
                null;

        Page<MentoringClassFindResponse> result = mentoringClassService.findAllClass(jobIds, domainPageable);

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.of(
                            true,
                            HttpStatus.OK,
                            "조회된 멘토링 수업이 없습니다.",
                            result
                    ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.of(
                            true,
                            HttpStatus.OK,
                            "멘토링 수업 조회 성공",
                            result
                    ));
        }

    }

    @Operation(summary = "멘토링 수업 상세 조회", description = "특정 멘토링 수업의 상세 정보를 조회합니다.")
    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> getClassById(
            @PathVariable Long classId
    ) {
        MentoringClassDetailResponse response = mentoringClassService.findOneClass(classId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "멘토링 수업 상세 조회 성공",
                        response
                ));
    }

    @Operation(summary = "멘토링 수업 등록", description = "멘토가 멘토링 수업을 등록합니다.")
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> createClass(
            @RequestBody MentoringClassCreateRequest request,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        MentoringClassDetailResponse response = mentoringClassService.createClass(memberId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.CREATED,
                        "멘토링 클래스 생성 성공",
                        response
                ));
    }

    @Operation(summary = "멘토링 수업 수정", description = "멘토링 수업 정보를 수정합니다.")
    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{classId}")
    public ResponseEntity<ApiResponse<MentoringClassUpdateResponse>> updateClass(
            @PathVariable Long classId,
            @RequestBody MentoringClassUpdateRequest request,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        MentoringClassUpdateResponse response = mentoringClassService.updateClass(classId, memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "멘토링 클래스 수정 성공",
                        response
                ));
    }

    @Operation(summary = "멘토링 수업 삭제", description = "멘토링 수업을 삭제합니다.")
    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{classId}")
    public ResponseEntity<ApiResponse<?>> deleteClass(
            @PathVariable Long classId
    ) {
        mentoringClassService.deleteClass(classId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.NO_CONTENT,
                        "멘토링 수업 삭제 성공",
                        null
                ));
    }
}
