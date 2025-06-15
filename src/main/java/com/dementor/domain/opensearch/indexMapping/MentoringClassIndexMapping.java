package com.dementor.domain.opensearch.indexMapping;

import org.opensearch.client.opensearch._types.analysis.TokenFilterDefinition;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.IndexSettings;

public class MentoringClassIndexMapping {
    public static final TypeMapping mapping = TypeMapping.of(t -> t
            .properties("id", Property.of(p -> p.keyword(k -> k)))
            .properties("title", Property.of(p -> p.text(k -> k.analyzer("synonym_analyzer"))))
            .properties("stack", Property.of(p -> p.text(k -> k.analyzer("synonym_analyzer"))))
            .properties("content", Property.of(p -> p.text(k -> k.analyzer("synonym_analyzer"))))
            .properties("price", Property.of(p -> p.integer(i -> i)))
            .properties("mentor", Property.of(p -> p.object(o -> o
                    .properties("id", Property.of(p2 -> p2.keyword(k -> k)))
                    .properties("name", Property.of(p2 -> p2.text(t2 -> t2)))
                    .properties("career", Property.of(p2 -> p2.integer(i2 -> i2)))
                    .properties("job", Property.of(p2 -> p2.object(o2 -> o2
                            .properties("id", Property.of(p3 -> p3.keyword(k2 -> k2)))
                            .properties("name", Property.of(p3 -> p3.text(t2 -> t2))))))
            )))
            .properties("favoriteCount", Property.of(p -> p.integer(i -> i)))
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