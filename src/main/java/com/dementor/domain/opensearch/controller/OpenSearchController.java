package com.dementor.domain.opensearch.controller;

import com.dementor.domain.opensearch.domain.MentoringClassDocument;
import com.dementor.domain.opensearch.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class OpenSearchController {

    private final OpenSearchService openSearchService;

    @GetMapping
    public List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException {
        return openSearchService.search("my_index", keyword);
    }
}
