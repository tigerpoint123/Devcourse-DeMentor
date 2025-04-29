package com.dementor.global.swaggerDocs;

import com.dementor.domain.favorite.dto.response.FavoriteAddResponse;
import com.dementor.domain.favorite.dto.response.FavoriteFindResponse;
import com.dementor.global.ApiResponse;
import com.dementor.global.custom.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "즐겨찾기", description = "즐겨찾기 관리")
public interface FavoriteSwagger {

    @Operation(summary = "즐겨찾기 등록", description = "회원이 특정 멘토링 수업을 즐겨찾기 추가합니다.")
    ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavorite(
            @PathVariable Long classId,
            @CurrentUser Long memberId
    );

    @Operation(summary = "즐겨찾기 삭제", description = "회원이 특정 멘토링 수업을 즐겨찾기 삭제합니다.")
    ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long favoriteId,
            @CurrentUser Long memberId
    );

    @Operation(summary = "회원의 즐겨찾기 목록 조회", description = "회원이 등록한 즐겨찾기 목록을 조회합니다.")
    ResponseEntity<ApiResponse<Page<FavoriteFindResponse>>> getFavoriteList(
            @PathVariable Long memberId,
            @Parameter(description = "페이지 정보", example = """
                    {
                      "page": 1,
                      "size": 10,
                      "sort": "id,desc"
                    }
                    """) Pageable pageable,
            HttpServletRequest request
    );

}
