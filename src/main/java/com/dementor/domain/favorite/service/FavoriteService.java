package com.dementor.domain.favorite.service;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import com.dementor.domain.favorite.entity.Favorite;
import com.dementor.domain.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteAddResponse addFavorite(Long classId, Long memberId) {
        Favorite favorite = Favorite.builder()
                .mentoringClassId(classId)
                .memberId(memberId)
                .build();

        favorite = favoriteRepository.save(favorite);

        return FavoriteAddResponse.of(favorite);
    }

    public void deleteFavorite(Long favoriteId, Long memberId) {
        favoriteRepository.deleteByIdAndMemberId(favoriteId, memberId);
    }

    public Page<FavoriteFindResponse> findAllFavorite(Long memberId, Pageable domainPageable) {
        Page<Favorite> mentoringClasses = favoriteRepository.findByMemberId(memberId, domainPageable);

        return mentoringClasses.map(FavoriteFindResponse::from);
    }
}
