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
public class FavoriteController implements FavoriteSwagger {

    private final FavoriteService favoriteService;

    @PostMapping("/{classId}")
    public ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavoriteRedis(
            @PathVariable Long classId,
            @CurrentUser Long memberId
    ) {
        FavoriteAddResponse response = favoriteService.addFavoriteRedis(classId, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "즐겨찾기 등록 성공",
                                response
                        )
                );
    }

    @PostMapping("/db/{classId}")
    public ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavoriteDB(
            @PathVariable Long classId,
            @CurrentUser Long memberId
    ) {
        FavoriteAddResponse response = favoriteService.addFavoriteDB(classId, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.CREATED,
                                "즐겨찾기 등록 성공",
                                response
                        )
                );
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long classId,
            @CurrentUser Long memberId
    ) {
        favoriteService.deleteFavorite(classId, memberId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.NO_CONTENT,
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
        Pageable domainPageable = PaginationUtil.getDefaultPageable(pageable);

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
