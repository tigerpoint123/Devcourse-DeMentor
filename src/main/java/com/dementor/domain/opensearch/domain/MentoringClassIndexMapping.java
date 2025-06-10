package com.dementor.domain.opensearch.domain;

import org.opensearch.client.opensearch._types.mapping.TypeMapping;

public class MentoringClassIndexMapping {
    public static final TypeMapping mapping = TypeMapping.of(t -> t
            .properties("id", p -> p
                    .keyword(k -> k))
            .properties("title", p -> p
                    .text(k -> k))
            .properties("content", p -> p
                    .text(k -> k))
    );
}
