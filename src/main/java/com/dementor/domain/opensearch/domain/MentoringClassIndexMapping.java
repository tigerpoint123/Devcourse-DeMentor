package com.dementor.domain.opensearch.domain;

import org.opensearch.client.opensearch._types.mapping.TypeMapping;

public class MentoringClassIndexMapping {
    public static final TypeMapping mapping = TypeMapping.of(t -> t
            .properties("id", p -> p
                    .keyword(k -> k))
            .properties("title", p -> p
                    .text(k -> k))
            .properties("stack", p -> p
                    .text(k -> k))
            .properties("content", p -> p
                    .text(k -> k))
            .properties("price", p -> p
                    .integer(i -> i))
            .properties("mentor", p -> p.object(o -> o
                    .properties("id", p2 -> p2.keyword(k -> k))
                    .properties("member", p2 -> p2.object(o2 -> o2
                            .properties("id", p3 -> p3.keyword(k -> k))
                            .properties("name", p3 -> p3.text(t2 -> t2))
                            .properties("job", p3 -> p3.text(t2 -> t2))
                            .properties("career", p3 -> p3.integer(i2 -> i2))))
            ))
            .properties("favoriteCount", p -> p
                    .integer(i -> i))

    );
}
