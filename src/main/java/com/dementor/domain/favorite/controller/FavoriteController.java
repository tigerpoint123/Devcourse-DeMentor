package com.dementor.domain.favorite.controller;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import com.dementor.domain.favorite.service.FavoriteService;
import com.dementor.global.ApiResponse;
import com.dementor.global.custom.CurrentUser;
import com.dementor.global.pagination.PaginationUtil;
import com.dementor.global.swaggerDocs.FavoriteSwagger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
// TODO : 즐겨찾기 캐싱 전략 ( k6 + Grafana + Prometheus )
public class FavoriteController implements FavoriteSwagger {

    private final FavoriteService favoriteService;

    @PostMapping("/{classId}")
    public ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavorite(
            @PathVariable Long classId,
            @CurrentUser Long memberId
    ) {
        FavoriteAddResponse response = favoriteService.addFavorite(classId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "즐겨찾기 등록 성공",
                                response
                        )
                );
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long favoriteId,
            @CurrentUser Long memberId
    ) {
        favoriteService.deleteFavorite(favoriteId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "즐겨찾기 삭제 성공"
                        )
                );
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Page<FavoriteFindResponse>>> getFavoriteList(
        @PathVariable Long memberId,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
        HttpServletRequest request
    ) {
        System.out.println("Authorization 헤더: " + request.getHeader("Authorization"));
        Pageable domainPageable = PaginationUtil.getFavoritePageable(pageable);

        Page<FavoriteFindResponse> response = favoriteService.findAllFavorite(memberId, domainPageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "favorited",
                                response
                        )
                );
    }

}
