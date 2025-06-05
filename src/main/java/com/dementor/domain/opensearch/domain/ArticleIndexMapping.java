package com.dementor.domain.opensearch.domain;

public class ArticleIndexMapping {
    public static final String MAPPING = """
        {
            "properties": {
                "id": {
                    "type": "keyword"
                },
                "title": {
                    "type": "text",
                    "analyzer": "standard"
                },
                "content": {
                    "type": "text",
                    "analyzer": "standard"
                }
            }
        }
        """;
}
