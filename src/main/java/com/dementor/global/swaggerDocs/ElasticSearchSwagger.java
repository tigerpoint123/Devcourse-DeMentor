package com.dementor.global.swaggerDocs;

import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Tag(name = "검색 기능", description = "검색 엔진 기반 검색 기능")
public interface ElasticSearchSwagger {
    @Operation(summary = "키워드 검색", description = "키워드를 포함하거나 동의어 설정된 모든 결과값을 제공.")
    List<MentoringClassDocument> search(
            @RequestParam String keyword
    ) throws IOException;
}
