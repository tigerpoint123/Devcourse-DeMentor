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
@RequestMapping("/api/elasticsearch")
@RequiredArgsConstructor
public class ElasticSearchController {
    // TODO : 스케줄러로 DB 반영하는 로직에 엘라스틱 서치 반영도 포함해야 함

    private final ElasticSearchService elasticSearchService;
    String indexName = "mentoring_class";

    @GetMapping
    public List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException {
        return elasticSearchService.search(indexName, keyword);
    }
}
