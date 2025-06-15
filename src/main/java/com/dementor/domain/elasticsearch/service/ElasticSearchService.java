package com.dementor.domain.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.dementor.domain.elasticsearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.elasticsearch.indexMapping.MentoringClassIndexMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public void saveDocument(String index, Long id, MentoringClassDocument document) throws IOException {
        IndexRequest<MentoringClassDocument> request = new IndexRequest.Builder<MentoringClassDocument>()
                .index(index)
                .id(String.valueOf(id))
                .document(document)
                .build();
        elasticsearchClient.index(request);
    }

    public List<MentoringClassDocument> search(String index, String keyword) throws IOException {
        SearchRequest request = new SearchRequest.Builder()
                .index(index)
                .query(q -> q
                        .bool(b -> b
                                .should(s1 -> s1.match(m -> m.field("content").query(keyword)))
                                .should(s2 -> s2.match(m -> m.field("title").query(keyword)))
                                .should(s3 -> s3.match(m -> m.field("stack").query(keyword)))
                        )
                )
                .build();

        SearchResponse<MentoringClassDocument> response = elasticsearchClient.search(request, MentoringClassDocument.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    public void createMentoringClassIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(indexName)
                .mappings(MentoringClassIndexMapping.mapping)
                .settings(MentoringClassIndexMapping.settings)
                .build();
        elasticsearchClient.indices().create(request);
    }

    public void deleteDocument(String indexName, Long classId) throws IOException {
        DeleteRequest request = new DeleteRequest.Builder()
                .index(indexName)
                .id(String.valueOf(classId))
                .build();
        elasticsearchClient.delete(request);
    }
}
