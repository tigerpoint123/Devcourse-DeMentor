package com.dementor.domain.favorite.exception;

import lombok.Getter;

@Getter
public class FavoriteException extends RuntimeException {
    private final FavoriteExceptionCode errorCode;

    public FavoriteException(FavoriteExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
