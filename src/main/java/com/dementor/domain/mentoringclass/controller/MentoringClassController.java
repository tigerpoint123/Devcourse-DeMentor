package com.dementor.domain.mentoringclass.controller;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.service.MentoringClassService;
import com.dementor.global.ApiResponse;
import com.dementor.global.pagination.PaginationUtil;
import com.dementor.global.security.CustomUserDetails;
import com.dementor.global.swaggerDocs.MentoringClassSwagger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@Slf4j
public class MentoringClassController implements MentoringClassSwagger {
    // TODO : 멘토링 신청 시 메시지 큐를 활용한 비동기 처리 고려
    private final MentoringClassService mentoringClassService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MentoringClassFindResponse>>> getClass(
            @RequestParam(required = false) List<String> jobId,
            Pageable pageable
    ) {
        Pageable domainPageable = PaginationUtil.getDefaultPageable(pageable);

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

    @Override
    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> getClassByIdFromRedis(
            @PathVariable Long classId
    ) {
        MentoringClassDetailResponse response = mentoringClassService.findOneClassFromRedis(classId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "멘토링 수업 상세 조회 성공",
                        response
                ));
    }

    @Override
    @GetMapping("/db/{classId}")
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> getClassByIdFromDb(
            @PathVariable Long classId
    ) {
        MentoringClassDetailResponse response = mentoringClassService.findOneClassFromDb(classId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "멘토링 수업 상세 조회 성공 (DB)",
                        response
                ));
    }

    @Override
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> createClass(
            @RequestBody MentoringClassCreateRequest request,
            Authentication authentication
    ) throws IOException {
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

    @Override
    @PreAuthorize("hasRole('MENTOR')")
    @PatchMapping("/{classId}")
    public ResponseEntity<ApiResponse<MentoringClassDetailResponse>> updateClass(
            @PathVariable Long classId,
            @RequestBody MentoringClassUpdateRequest request,
            Authentication authentication
    ) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        MentoringClassDetailResponse response = mentoringClassService.updateClass(classId, memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "멘토링 클래스 수정 성공",
                        response
                ));
    }

    @Override
    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/{classId}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(
            @PathVariable Long classId
    ) throws IOException {
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

    @Override
    @GetMapping("/favoriteCount/{classId}")
    public ResponseEntity<ApiResponse<Integer>> findFavoriteCount(@PathVariable Long classId) {
        int favoriteCount = mentoringClassService.findFavoriteCount(classId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "즐겨찾기 개수 조회 성공",
                        favoriteCount
                ));
    }

}
