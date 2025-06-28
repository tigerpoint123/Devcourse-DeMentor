package com.dementor.domain.favorite.service;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    FavoriteAddResponse addFavoriteDB(Long classId, Long memberId);
    FavoriteAddResponse addFavoriteRedis(Long classId, Long memberId);
    void deleteFavorite(Long classId, Long memberId);
    Page<FavoriteFindResponse> findAllFavorite(Long memberId, Pageable domainPageable);
} 