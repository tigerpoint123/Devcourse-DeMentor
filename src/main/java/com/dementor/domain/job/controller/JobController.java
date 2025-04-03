package com.dementor.domain.job.controller;

import com.dementor.domain.job.dto.request.JobCreaeteRequest;
import com.dementor.domain.job.dto.response.JobFindResponse;
import com.dementor.domain.job.service.JobService;
import com.dementor.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/job")
@RequiredArgsConstructor
@Tag(name = "직무 API", description = "멘토 지원, 정보 수정, 조회 API")
public class JobController {
    private final JobService jobService;

    @GetMapping
    public ApiResponse<?> getJobList() {
        List<JobFindResponse> list = jobService.getJobList();
        return ApiResponse.of(
                true,
                HttpStatus.OK,
                "직무 전체 조회 성공",
                list
        );
    }

    @PostMapping
    public ApiResponse<?> createJob(
            @RequestBody JobCreaeteRequest request,
            Authentication authentication
    ) {
        Long jobId = jobService.createJob(request);
        return ApiResponse.of(
                true,
                HttpStatus.CREATED,
                "직무 생성 성공",
                jobId
        );
    }
}
