package com.dementor.domain.opensearch.init;

import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.opensearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.opensearch.service.OpenSearchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

//@Component
@RequiredArgsConstructor
@Slf4j
public class MentoringClassDataInit {

    private final OpenSearchService elasticSearchService;
    private final MentoringClassRepository mentoringClassRepository;
    private final OpenSearchClient openSearchClient;
    String indexName = "mentoring_class";

    // TODO : 대용량 데이터라면 ?
    @PostConstruct
    @Transactional(readOnly = true)
    public void init() throws IOException {
        // 1. 인덱스 존재 여부 확인
        boolean exists = openSearchClient.indices().exists(e -> e.index(indexName)).value();
        if(!exists) {
            elasticSearchService.createMentoringClassIndex(indexName);
        }

        List<MentoringClass> mentoringClasses = mentoringClassRepository.findAllWithMentor();
        for (MentoringClass entity : mentoringClasses) {
            MentoringClassDocument doc = MentoringClassDocument.from(entity);

            try {
                elasticSearchService.saveDocument(indexName, doc.getId(), doc);
            } catch (Exception e) {
                log.error("document 저장 실패 : {}", doc.getId(), e);
            }
        }
    }
} 