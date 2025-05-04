package com.dementor.domain.favorite.service;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import com.dementor.domain.favorite.entity.Favorite;
import com.dementor.domain.favorite.event.FavoriteAddedEvent;
import com.dementor.domain.favorite.event.FavoriteRemovedEvent;
import com.dementor.domain.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public FavoriteAddResponse addFavorite(Long classId, Long memberId) {
        Favorite favorite = Favorite.builder()
                .mentoringClassId(classId)
                .memberId(memberId)
                .build();

        favorite = favoriteRepository.save(favorite);
        eventPublisher.publishEvent(new FavoriteAddedEvent(classId));

        return FavoriteAddResponse.of(favorite);
    }

    @Transactional
    public void deleteFavorite(Long classId, Long memberId) {
        Favorite favorite = favoriteRepository.findByMentoringClassIdAndMemberId(classId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기를 찾을 수 없습니다: " + classId));

        favoriteRepository.delete(favorite);
        eventPublisher.publishEvent(new FavoriteRemovedEvent(favorite.getMentoringClassId()));
    }

    public Page<FavoriteFindResponse> findAllFavorite(Long memberId, Pageable domainPageable) {
        Page<Favorite> mentoringClasses = favoriteRepository.findByMemberId(memberId, domainPageable);
        return mentoringClasses.map(FavoriteFindResponse::from);
    }
}
