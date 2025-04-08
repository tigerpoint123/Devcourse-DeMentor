package com.dementor.domain.admin.controller;

import com.dementor.domain.admin.AdminMentorApplymentService;
import com.dementor.domain.admin.dto.ApplymentResponse;
import com.dementor.global.ApiResponse;
import com.dementor.global.common.pagination.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/mentor/applyment")
@RequiredArgsConstructor
public class AdminMentorApplymentController {

    private final AdminMentorApplymentService adminMentorApplymentService;

    @GetMapping
    @Operation(summary = "멘토 지원 목록 전체조회", description = "멘토 지원자 정보를 조회합니다")
    public ResponseEntity<ApiResponse<Page<ApplymentResponse>>> adminMentorApplyment(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Pageable domainPageable = PaginationUtil.getApplymentPageable(pageable);

        Page<ApplymentResponse> responses = adminMentorApplymentService.findAllApplyment(domainPageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "Success",
                        responses
                ));
    }

}
