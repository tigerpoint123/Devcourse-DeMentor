package com.dementor.domain.favorite.repository;

import com.dementor.domain.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Page<Favorite> findByMemberId(Long memberId, Pageable domainPageable);

    Optional<Favorite> findByMentoringClassIdAndMemberId(Long classId, Long memberId);
}
