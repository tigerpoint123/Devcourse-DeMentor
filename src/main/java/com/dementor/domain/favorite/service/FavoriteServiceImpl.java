package com.dementor.domain.favorite.service;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import com.dementor.domain.favorite.entity.Favorite;
import com.dementor.domain.favorite.event.FavoriteAddedEvent;
import com.dementor.domain.favorite.event.FavoriteRemovedEvent;
import com.dementor.domain.favorite.exception.FavoriteException;
import com.dementor.domain.favorite.exception.FavoriteExceptionCode;
import com.dementor.domain.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements  FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public FavoriteAddResponse addFavoriteDB(Long classId, Long memberId) {
        Optional<Favorite> favorite = favoriteRepository.findByMentoringClassIdAndMemberId(classId, memberId);
        if (favorite.isPresent())
            throw new FavoriteException(FavoriteExceptionCode.FAVORITE_ALREADY_EXISTS);

        Favorite newFavorite = Favorite.builder()
                .mentoringClassId(classId)
                .memberId(memberId)
                .build();

        newFavorite = favoriteRepository.save(newFavorite);

        return FavoriteAddResponse.of(newFavorite);
    }

    @Transactional
    public FavoriteAddResponse addFavoriteRedis(Long classId, Long memberId) {
        Optional<Favorite> favorite = favoriteRepository.findByMentoringClassIdAndMemberId(classId, memberId);
        if (favorite.isPresent())
            throw new FavoriteException(FavoriteExceptionCode.FAVORITE_ALREADY_EXISTS);

        Favorite newFavorite = Favorite.builder()
                .mentoringClassId(classId)
                .memberId(memberId)
                .build();

        newFavorite = favoriteRepository.save(newFavorite);
        eventPublisher.publishEvent(new FavoriteAddedEvent(classId));

        return FavoriteAddResponse.of(newFavorite);
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
