package com.dementor.domain.admin.controller;

import com.dementor.domain.admin.AdminMentorApplymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/mentor/applyment")
@RequiredArgsConstructor
public class AdminMentorApplymentController {

    private final AdminMentorApplymentService adminMentorApplymentService;

//    @GetMapping
//    @Operation(summary = "멘토링 신청 목록 조회", description = "내가 신청한 멘토링 목록을 조회합니다")
//    public ResponseEntity<ApiResponse<ApplymentResponse>> adminMentorApplyment(
//
//    ) {
//        List<ApplymentResponse> responses = adminMentorApplymentService.findAllApplyment();
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.of(
//                        true,
//                        HttpStatus.OK,
//                        "Success",
//                        adminMentorApplymentService.adminMentorApplyment()
//                ))
//    }

}
