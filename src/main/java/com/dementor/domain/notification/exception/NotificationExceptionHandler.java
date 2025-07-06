package com.dementor.domain.notification.exception;

import com.dementor.global.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.dementor.domain.notification")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<?>> handleNotificationException(NotificationException e) {
        NotificationExceptionCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.of(
                        false,
                        errorCode.getStatus(),
                        errorCode.getMessage()
                ));
    }

}
