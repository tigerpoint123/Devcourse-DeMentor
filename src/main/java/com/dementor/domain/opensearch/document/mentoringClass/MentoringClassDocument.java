package com.dementor.domain.opensearch.document.mentoringClass;

import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.opensearch.document.MentorInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MentoringClassDocument {
    private Long id;
    private String title;
    private String stack;
    private String content;
    private int price;
    private MentorInfo mentor;
    private int favoriteCount;

    public static MentoringClassDocument from(MentoringClass mentoringClass) {
        MentoringClassDocument document = new MentoringClassDocument();
        document.setId(mentoringClass.getId());
        document.setTitle(mentoringClass.getTitle());
        document.setStack(mentoringClass.getStack() != null ? String.join(",", mentoringClass.getStack()) : "");
        document.setContent(mentoringClass.getContent());
        document.setPrice(mentoringClass.getPrice());
        document.setMentor(MentorInfo.from(mentoringClass.getMentor()));
        document.setFavoriteCount(mentoringClass.getFavoriteCount());
        return document;
    }
} 