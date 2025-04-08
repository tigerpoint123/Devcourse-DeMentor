package com.dementor.global.common.swaggerDocs;

import com.dementor.domain.admin.dto.wtf.ApplymentDetailResponse;
import com.dementor.domain.admin.dto.wtf.ApplymentResponse;
import com.dementor.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "멘토 지원 ", description = "멘토 지원 데이터 CRUD")
public interface AdminMentorApplymentSwagger {
    @Operation(summary = "멘토 지원 목록 전체조회", description = "멘토 지원자 정보를 조회합니다")
    ResponseEntity<ApiResponse<Page<ApplymentResponse>>> findAllAdminMentorApplyment(Pageable pageable);

    ResponseEntity<ApiResponse<ApplymentDetailResponse>> findOneApplyment(Long applymentId);

}
