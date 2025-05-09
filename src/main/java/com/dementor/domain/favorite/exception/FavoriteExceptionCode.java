package com.dementor.domain.favorite.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FavoriteExceptionCode {
    FAVORITE_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기 목록을 찾을 수 없습니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 즐겨찾기에 등록된 클래스입니다."),
    FAVORITE_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "즐겨찾기 등록에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
