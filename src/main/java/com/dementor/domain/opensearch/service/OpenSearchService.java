package com.dementor.domain.opensearch.service;

import com.dementor.domain.opensearch.domain.MentoringClassDocument;
import com.dementor.domain.opensearch.domain.MentoringClassIndexMapping;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchClient openSearchClient;

    // TODO : 데이터 추가/수정/삭제 시, OpenSearch에 반영하는 로직 추가 필요
    public void saveDocument(String index, String id, MentoringClassDocument document) throws IOException {
        IndexRequest<MentoringClassDocument> request = IndexRequest.of(i -> i
                .index(index)
                .id(id)
                .document(document));
        openSearchClient.index(request);
    }

    public List<MentoringClassDocument> search(String index, String keyword) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .match(m -> m
                                .field("content")
                                .query(FieldValue.of(keyword)))));

        SearchResponse<MentoringClassDocument> response = openSearchClient.search(request, MentoringClassDocument.class);
        List<MentoringClassDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();
        return results;
    }

    public void createMentoringClassIndex(String indexName) throws IOException {
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .mappings(MentoringClassIndexMapping.mapping));
        openSearchClient.indices().create(request);
    }
}
