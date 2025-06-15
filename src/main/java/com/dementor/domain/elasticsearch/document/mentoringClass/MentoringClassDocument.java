package com.dementor.domain.elasticsearch.document.mentoringClass;

import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.elasticsearch.document.MentorInfo;
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

    /*
    검색으로 나오는 리스트를 전체조회처럼 나오게 할려면 프론트엔드 역할인지 ?
    : 백엔드에서 가공 후 전달 (권장)
    > 오픈서치에서 검색된 document 리스트를 백엔드에서 DTO로 변환
    > 필요하다면 DB에서 추가 정보(스케줄)도 조회해서 합쳐서 반환
    * */

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

