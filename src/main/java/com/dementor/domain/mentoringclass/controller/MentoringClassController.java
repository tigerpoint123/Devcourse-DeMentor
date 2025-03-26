package com.dementor.domain.mentoringclass.controller;

import com.dementor.domain.mentoringclass.entity.MentoringClassEntity;
import com.dementor.domain.mentoringclass.service.MentoringClassService;
import com.dementor.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class MentoringClassController {
    private final MentoringClassService mentoringClassService;

    @GetMapping // 전체 조회
    public ApiResponse<?> getClass(
            @RequestParam(required = false) Long jobId
    ) {
        List<MentoringClassEntity> list = mentoringClassService.selectClass(jobId);
        return ApiResponse.success(
                "멘토링 클래스 선택 성공",
                list
        );
    }

    @GetMapping("/{mentor_id}")
    public ApiResponse<?> getClassByMentorId(
        @PathVariable(required = false) Long mentorId
    ) {
        return null;
    }

    @GetMapping("/{class_id}")
    public ApiResponse<?> getClassById(
            @PathVariable Long classId
    ) {
        return null;
    }

    @PostMapping
    public ApiResponse<?> createClass() {
        // TODO: 실제 생성 로직 구현
        return ApiResponse.success("멘토링 클래스 생성 성공", "생성된 클래스 ID");
    }

    @PutMapping("/{class_id}")
    public ApiResponse<?> updateClass(
            @PathVariable Long classId
    ) {
        // TODO: 실제 생성 로직 구현
        return ApiResponse.success("멘토링 클래스 수정 성공", "생성된 클래스 ID");
    }

    @DeleteMapping("/{class_id}")
    public ApiResponse<?> deleteClass(
            @PathVariable Long classId
    ) {
        // TODO: 실제 생성 로직 구현
        return ApiResponse.success("멘토링 클래스 삭제 성공", "생성된 클래스 ID");
    }
}
