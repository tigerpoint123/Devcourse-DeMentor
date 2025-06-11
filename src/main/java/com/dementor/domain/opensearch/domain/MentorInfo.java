package com.dementor.domain.opensearch.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MentorInfo {
    private Long id;
    private String name;
    private JobInfo job;
    private int career;
}
