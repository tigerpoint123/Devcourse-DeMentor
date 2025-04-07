package com.dementor.domain.postattachment.controller;

import com.dementor.domain.postattachment.dto.response.FileResponse;
import com.dementor.domain.postattachment.dto.response.FileResponse.FileInfoDto;
import com.dementor.domain.postattachment.entity.PostAttachment.ImageType;
import com.dementor.domain.postattachment.exception.PostAttachmentException;
import com.dementor.domain.postattachment.service.PostAttachmentService;
import com.dementor.global.ApiResponse;
import com.dementor.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "멘토 파일첨부 API", description = "멘토 지원, 정보 수정에 파일 첨부할 수 있습니다.")
public class PostAttachmentController {

    private final PostAttachmentService postAttachmentService;

    //파일 업로드 API
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MENTEE') or hasRole('MENTOR')" )
    @Operation(summary = "파일 업로드", description = "새로운 파일을 업로드합니다. 회원가입한 회원(멘티)만 파일 업로드가 가능합니다.")
    public ResponseEntity<ApiResponse<?>> uploadFile(
            @RequestParam(value = "file", required = false) List<MultipartFile> files,
            @RequestParam(value = "introductionMarkdown", required = false) String introductionMarkdown,
            @RequestParam(value = "recommendationMarkdown", required = false) String recommendationMarkdown,
            @RequestParam(value = "imageType", required = false, defaultValue = "NORMAL") ImageType imageType,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 파일이나 마크다운 중 하나는 필수
            if ((files == null || files.isEmpty()) &&
                    (introductionMarkdown == null || introductionMarkdown.isEmpty()) &&
                    (recommendationMarkdown == null || recommendationMarkdown.isEmpty())) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, "파일 또는 마크다운 텍스트 중 하나는 필수입니다."));
            }

            List<FileInfoDto> allUploadedFiles = new ArrayList<>();

            // 1. 일반 파일 업로드 처리
            if (files != null && !files.isEmpty()) {
                List<FileInfoDto> uploadedFiles = postAttachmentService.uploadFiles(
                        files, ImageType.NORMAL, userDetails.getId(), null);
                allUploadedFiles.addAll(uploadedFiles);
            }

            // 2. 자기소개 마크다운 처리
            if (introductionMarkdown != null && !introductionMarkdown.isEmpty()) {
                List<FileInfoDto> introductionImages = postAttachmentService.processMarkdownOnly(
                        introductionMarkdown, ImageType.MARKDOWN_SELF_INTRODUCTION, userDetails.getId());
                allUploadedFiles.addAll(introductionImages);
            }

            // 3. 추천대상 마크다운 처리
            if (recommendationMarkdown != null && !recommendationMarkdown.isEmpty()) {
                List<FileInfoDto> recommendationImages = postAttachmentService.processMarkdownOnly(
                        recommendationMarkdown, ImageType.MARKDOWN_RECOMMENDATION, userDetails.getId());
                allUploadedFiles.addAll(recommendationImages);
            }

            FileResponse.FileUploadResponseDto responseDto = FileResponse.FileUploadResponseDto.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("업로드에 성공했습니다.")
                    .data(allUploadedFiles)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.of(true, HttpStatus.CREATED, "업로드에 성공했습니다.", responseDto));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (PostAttachmentException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus().value())
                    .body(ApiResponse.of(false, e.getErrorCode().getStatus(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    //파일 삭제 API
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MENTOR') and @postAttachmentService.isFileOwner(#fileId, authentication.principal.id))")  // 멘토와 관리자만 파일 삭제 가능
    @Operation(summary = "파일 삭제", description = "특정 파일을 삭제합니다. 파일 업로드한 본인만 삭제할 수 있습니다.")
    public ResponseEntity<ApiResponse<?>> deleteFile(
            @PathVariable("fileId") Long fileId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 파일 삭제 처리
            postAttachmentService.deleteFile(fileId, userDetails.getId());

            FileResponse.FileDeleteResponseDto responseDto = FileResponse.FileDeleteResponseDto.builder()
                    .status(HttpStatus.OK.value())
                    .message("파일 삭제에 성공했습니다.")
                    .build();

            // 응답 생성
            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "파일 삭제에 성공했습니다.", responseDto));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (PostAttachmentException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus().value())
                    .body(ApiResponse.of(false, e.getErrorCode().getStatus(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    //일반 첨부 파일 다운로드 API
    @GetMapping("/{attachmentId}/download")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MENTOR') and @postAttachmentService.isFileOwner(#attachmentId, authentication.principal.id))")  // 멘토와 관리자만 파일 다운로드 가능
    @Operation(summary = "일반 첨부 파일 다운로드", description = "특정 일반 첨부 파일을 다운로드합니다. 멘토와 관리자만 다운로드가 가능합니다.")
    public ResponseEntity<ApiResponse<?>> downloadFile(
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 파일 다운로드 처리
            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "파일 다운로드 성공",
                            postAttachmentService.downloadFile(attachmentId, userDetails.getId())));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (PostAttachmentException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus().value())
                    .body(ApiResponse.of(false, e.getErrorCode().getStatus(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    //마크다운 이미지 다운로드 API
    @GetMapping("/markdown-images/{uniqueIdentifier}")
    @PreAuthorize("hasRole('MENTOR') or (hasRole('MENTOR') and @postAttachmentService.isMarkdownImageOwner(#uniqueIdentifier, authentication.principal.id))")  // 멘토와 관리자만 이미지 다운로드 가능
    @Operation(summary = "마크다운 이미지 다운로드", description = "마크다운 텍스트 내에서 참조하는 이미지를 제공합니다. 이 API는 이미지를 inline으로 표시합니다.")
    public ResponseEntity<ApiResponse<?>> downloadMarkdownImage(
            @PathVariable String uniqueIdentifier,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height
    ) {
        try {
            // 이미지 다운로드 처리 (공개 액세스 - 인증 필요 없음)
            return ResponseEntity.ok()
                    .body(ApiResponse.of(true, HttpStatus.OK, "이미지 다운로드 성공",
                            postAttachmentService.downloadMarkdownImage(uniqueIdentifier, width, height)));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(false, HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (PostAttachmentException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus().value())
                    .body(ApiResponse.of(false, e.getErrorCode().getStatus(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(false, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}