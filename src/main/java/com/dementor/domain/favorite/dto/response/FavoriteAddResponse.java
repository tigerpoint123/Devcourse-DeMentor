package com.dementor.domain.favorite.dto.response;

import com.dementor.domain.favorite.entity.Favorite;

public record FavoriteAddResponse(
    Long favoriteId,
    Long mentoringClassId,
    Long memberId
) {
    public static FavoriteAddResponse of(Favorite favorite) {
        return new FavoriteAddResponse(
                favorite.getId(),
                favorite.getMentoringClassId(),
                favorite.getMemberId()
        );
    }
}
