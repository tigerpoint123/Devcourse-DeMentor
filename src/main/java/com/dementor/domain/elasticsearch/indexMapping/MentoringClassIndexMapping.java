package com.dementor.domain.elasticsearch.indexMapping;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch._types.analysis.TokenFilterDefinition;

public class MentoringClassIndexMapping {
    public static final TypeMapping mapping = TypeMapping.of(t -> t
            .properties("id", p -> p
                    .keyword(k -> k))
            .properties("title", p -> p
                    .text(k -> k.analyzer("synonym_analyzer")))
            .properties("stack", p -> p
                    .text(k -> k.analyzer("synonym_analyzer")))
            .properties("content", p -> p
                    .text(k -> k.analyzer("synonym_analyzer")))
            .properties("price", p -> p
                    .integer(i -> i))
            .properties("mentor", p -> p.object(o -> o
                    .properties("id", p2 -> p2.keyword(k -> k))
                    .properties("name", p2 -> p2.text(t2 -> t2))
                    .properties("career", p2 -> p2.integer(i2 -> i2))
                    .properties("job", p2 -> p2.object(o2 -> o2
                            .properties("id", p3 -> p3.keyword(k2 -> k2))
                            .properties("name", p3 -> p3.text(t2 -> t2)))))
            )
            .properties("favoriteCount", p -> p
                    .integer(i -> i))
    );

    public static final IndexSettings settings = IndexSettings.of(s -> s
            .analysis(a -> a
                    .analyzer("synonym_analyzer", an -> an
                            .custom(c -> c
                                    .tokenizer("standard")
                                    .filter("lowercase", "synonym_filter")))
                    .filter("synonym_filter", f -> f
                            .definition(TokenFilterDefinition.of(def -> def
                                    .synonym(syn -> syn
                                            .synonyms("Java, 자바")
                                    )
                            ))
                    )
            )
    );
}
