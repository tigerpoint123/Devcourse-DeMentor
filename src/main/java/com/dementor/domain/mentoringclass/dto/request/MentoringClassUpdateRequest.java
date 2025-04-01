package com.dementor.domain.mentoringclass.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringClassUpdateRequest {
    
    @Schema(description = "수업 제목")
    private String title;
    
    @Schema(description = "수업 내용")
    private String content;
    
    @Schema(description = "수업 가격")
    private Integer price;
}