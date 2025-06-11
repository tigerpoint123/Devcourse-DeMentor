package com.dementor.domain.opensearch.document.mentoringClass;

import com.dementor.domain.opensearch.document.MentorInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MentoringClassDocument {
    private String id;
    private String title;
    private String stack;
    private String content;
    private int price;
    private MentorInfo mentor;
    private int favoriteCount;

}
