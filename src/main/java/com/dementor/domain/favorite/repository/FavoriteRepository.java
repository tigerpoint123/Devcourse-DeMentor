package com.dementor.domain.favorite.repository;

import com.dementor.domain.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    void deleteByIdAndMemberId(Long favoriteId, Long memberId);

    Page<Favorite> findByMemberId(Long memberId, Pageable domainPageable);
}
