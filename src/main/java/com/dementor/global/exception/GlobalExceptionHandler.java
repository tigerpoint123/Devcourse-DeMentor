package com.dementor.global.exception;

import com.dementor.domain.apply.exception.ApplyErrorCode;
import com.dementor.domain.apply.exception.ApplyException;
import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.domain.mentor.exception.MentorErrorCode;
import com.dementor.domain.mentor.exception.MentorException;
import com.dementor.domain.mentoringclass.exception.MentoringClassException;
import com.dementor.domain.mentoringclass.exception.MentoringClassExceptionCode;
import com.dementor.domain.postattachment.exception.PostAttachmentException;
import com.dementor.global.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MemberException.class)
	public ApiResponse<?> handleMemberException(MemberException e) {
		MemberErrorCode errorCode = e.getErrorCode();
		return ApiResponse.of(false,
			errorCode.getStatus(),
			errorCode.getMessage());
	}

	@ExceptionHandler(ApplyException.class)
	public ApiResponse<?> handleApplyException(ApplyException e) {
		ApplyErrorCode errorCode = e.getErrorCode();
		return ApiResponse.of(false,
			errorCode.getStatus(),
			errorCode.getMessage());
	}

	@ExceptionHandler(PostAttachmentException.class)
	public ApiResponse<?> handlePostAttachmentException(PostAttachmentException e) {
		return ApiResponse.of(false, e.getErrorCode().getStatus(), e.getMessage());
	}

	@ExceptionHandler(MentorException.class)
	public ApiResponse<?> handleMentorException(MentorException e) {
		MentorErrorCode errorCode = e.getErrorCode();
		return ApiResponse.of(false,
			errorCode.getStatus(),
			errorCode.getMessage());
	}

	// MaxUploadSizeExceededException 처리
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ApiResponse<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		return ApiResponse.of(false, HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 허용 범위를 초과했습니다. 최대 10MB까지 가능합니다.");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
		return ApiResponse.of(false,
			HttpStatus.BAD_REQUEST,
			e.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
		return ApiResponse.of(false,
			HttpStatus.FORBIDDEN,
			e.getMessage());
	}

	@ExceptionHandler(MentoringClassException.class)
	public ApiResponse<?> handleMentoringClassException(MentoringClassException e) {
		MentoringClassExceptionCode errorCode = e.getErrorCode();
		return ApiResponse.of(false,
			errorCode.getStatus(),
			errorCode.getMessage());
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.of(false, HttpStatus.NOT_FOUND, e.getMessage()));
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
	}
}