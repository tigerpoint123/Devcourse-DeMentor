package com.dementor.domain.opensearch.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class JobInfo {
    private Long id;
    private String name;
}
