package com.dementor.domain.opensearch.controller;

import com.dementor.domain.opensearch.domain.MentoringClassDocument;
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

    private final OpenSearchService openSearchService;

    @GetMapping
    public List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException {
        return openSearchService.search("mentoring_class", keyword);
    }
}
