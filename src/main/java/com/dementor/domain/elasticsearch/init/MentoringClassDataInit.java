package com.dementor.domain.elasticsearch.init;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.elasticsearch.service.ElasticSearchService;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentoringClassDataInit {

    private final ElasticSearchService elasticSearchService;
    private final MentoringClassRepository mentoringClassRepository;
    private final ElasticsearchClient elasticsearchClient;
    String indexName = "mentoring_class";

    // TODO : 대용량 데이터라면 ?
    @PostConstruct
    @Transactional(readOnly = true)
    public void init() throws IOException {
        // 1. 인덱스 존재 여부 확인
        boolean exists = elasticsearchClient.indices().exists(e -> e.index(indexName)).value();
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
