package com.dementor.domain.opensearch.controller;

import com.dementor.domain.opensearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.opensearch.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class OpenSearchController {
    // TODO : 데이터 추가/수정/삭제 시, OpenSearch에 반영하는 로직 추가 필요

    private final OpenSearchService openSearchService;
    String indexName = "mentoring_class";

    @GetMapping
    public List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException {
        return openSearchService.search(indexName, keyword);
    }
}
