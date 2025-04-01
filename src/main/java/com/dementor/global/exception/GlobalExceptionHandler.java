package com.dementor.global.exception;

import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.global.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MemberException.class)
	public ApiResponse<?> handleMemberException(MemberException e) {
		MemberErrorCode errorCode = e.getErrorCode();
		return ApiResponse.of(false,
			errorCode.getStatus(),
			errorCode.getMessage());
	}

	// EntityNotFoundException 처리 추가
	@ExceptionHandler(EntityNotFoundException.class)
	public ApiResponse<?> handleEntityNotFoundException(EntityNotFoundException e) {
		return ApiResponse.of(false,
				HttpStatus.NOT_FOUND,
				e.getMessage());
	}

	// IllegalStateException 처리 추가
	@ExceptionHandler(IllegalStateException.class)
	public ApiResponse<?> handleIllegalStateException(IllegalStateException e) {
		return ApiResponse.of(false,
				HttpStatus.BAD_REQUEST,
				e.getMessage());
	}

	// 일반 예외 처리 추가
	@ExceptionHandler(Exception.class)
	public ApiResponse<?> handleGeneralException(Exception e) {
		return ApiResponse.of(false,
				HttpStatus.INTERNAL_SERVER_ERROR,
				"서버 오류가 발생했습니다: " + e.getMessage());
	}
}