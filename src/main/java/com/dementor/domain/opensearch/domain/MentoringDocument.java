package com.dementor.domain.opensearch.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MentoringDocument {
    private String id;
    private String title;
    private String content;
}
