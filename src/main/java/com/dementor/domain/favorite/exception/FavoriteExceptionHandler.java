package com.dementor.domain.favorite.exception;

import com.dementor.global.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.dementor.domain.favorite")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FavoriteExceptionHandler {

    @ExceptionHandler(FavoriteException.class)
    public ResponseEntity<ApiResponse<?>> handleFavoriteException(FavoriteException e) {
        FavoriteExceptionCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.of(
                false,
                errorCode.getStatus(),
                errorCode.getMessage()
            ));
    }
}
