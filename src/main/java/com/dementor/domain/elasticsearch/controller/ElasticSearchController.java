package com.dementor.domain.elasticsearch.controller;

import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.elasticsearch.service.ElasticSearchService;
import com.dementor.global.ApiResponse;
import com.dementor.global.pagination.PaginationUtil;
import com.dementor.global.swaggerDocs.ElasticSearchSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/elasticsearch")
@RequiredArgsConstructor
public class ElasticSearchController implements ElasticSearchSwagger {
    // TODO : 스케줄러로 DB 반영하는 로직에 엘라스틱 서치 반영도 포함해야 함 (완)

    private final ElasticSearchService elasticSearchService;
    private static final String indexName = "mentoring_class";

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MentoringClassDocument>>> search(
            @RequestParam String keyword,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException {
        Pageable domainPageable = PaginationUtil.getDefaultPageable(pageable);
        Page<MentoringClassDocument> page = elasticSearchService.search(indexName, keyword, domainPageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    ApiResponse.of(
                            true,
                            HttpStatus.OK,
                            "검색 성공",
                            page
                    )
                );
    }
}
