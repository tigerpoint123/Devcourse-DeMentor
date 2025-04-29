package com.dementor.domain.favorite.dto.response;

import com.dementor.domain.favorite.entity.Favorite;

public record FavoriteFindResponse(
    Long id,
    Long mentoringClassId,
    Long memberId
) {
    public static FavoriteFindResponse from(Favorite favorite) {
        return new FavoriteFindResponse(
                favorite.getId(),
                favorite.getMentoringClassId(),
                favorite.getMemberId()
        );
    }
}
