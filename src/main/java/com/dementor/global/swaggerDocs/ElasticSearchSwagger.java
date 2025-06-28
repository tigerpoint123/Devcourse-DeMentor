package com.dementor.global.swaggerDocs;

import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "검색 기능", description = "검색 엔진 기반 검색 기능")
public interface ElasticSearchSwagger {
    @Operation(summary = "키워드 검색", description = "키워드를 포함하거나 동의어 설정된 모든 결과값을 제공.")
    ResponseEntity<ApiResponse<Page<MentoringClassDocument>>> search(
            @RequestParam String keyword,
            @Parameter(description = "페이지 정보", example = """
            {
              "page": 1,
              "size": 10,
              "sort": "id,desc"
            }
            """) Pageable pageable
    ) throws IOException;
}
