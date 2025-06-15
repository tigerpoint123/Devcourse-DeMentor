package com.dementor.domain.elasticsearch.controller;

import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.elasticsearch.service.ElasticSearchService;
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
public class ElasticSearchController {
    // TODO : 데이터 추가/수정/삭제 시, Elasticsearch에 반영하는 로직 추가 필요

    private final ElasticSearchService elasticSearchService;
    String indexName = "mentoring_class";

    @GetMapping
    public List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException {
        return elasticSearchService.search(indexName, keyword);
    }
}
