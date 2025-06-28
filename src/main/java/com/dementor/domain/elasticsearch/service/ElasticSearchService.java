package com.dementor.domain.elasticsearch.service;

import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ElasticSearchService {

    void saveDocument(String index, Long id, MentoringClassDocument document) throws IOException;

    Page<MentoringClassDocument> search(String index, String keyword, Pageable pageable) throws IOException;

    void createMentoringClassIndex(String indexName) throws IOException;

    void deleteDocument(String indexName, Long classId) throws IOException;
}
