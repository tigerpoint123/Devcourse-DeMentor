package com.dementor.domain.opensearch.service;

import com.dementor.domain.opensearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.opensearch.indexMapping.MentoringClassIndexMapping;
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

    public void saveDocument(String index, Long id, MentoringClassDocument document) throws IOException {
        IndexRequest<MentoringClassDocument> request = IndexRequest.of(i -> i
                .index(index)
                .id(String.valueOf(id))
                .document(document));
        openSearchClient.index(request);
    }

    public List<MentoringClassDocument> search(String index, String keyword) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
                .index(index)
                .query(q -> q
                        .bool(b -> b
                                .should(s1 -> s1.match(m -> m.field("content").query(FieldValue.of(keyword))))
                                .should(s2 -> s2.match(m -> m.field("title").query(FieldValue.of(keyword))))
                                .should(s3 -> s3.match(m -> m.field("stack").query(FieldValue.of(keyword))))
                        )
                )
        );;

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
