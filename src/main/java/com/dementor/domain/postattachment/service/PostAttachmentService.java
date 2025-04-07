package com.dementor.domain.postattachment.service;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.postattachment.dto.response.FileResponse.FileInfoDto;
import com.dementor.domain.postattachment.entity.PostAttachment;
import com.dementor.domain.postattachment.entity.PostAttachment.ImageType;
import com.dementor.domain.postattachment.exception.PostAttachmentErrorCode;
import com.dementor.domain.postattachment.exception.PostAttachmentException;
import com.dementor.domain.postattachment.repository.PostAttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostAttachmentService {

    private final PostAttachmentRepository postAttachmentRepository;
    private final MemberRepository memberRepository;
    private final MentorRepository mentorRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.max-size}")
    private long maxFileSize; // 바이트 단위 (10MB = 10 * 1024 * 1024)

    @Value("${file.max-per-user}")
    private int maxFilesPerUser; // 사용자당 최대 파일 수

    // 멘토 관련 파일을 업로드하는 새로운 메서드 추가
    @Transactional
    public Map<ImageType, List<FileInfoDto>> uploadMentorFiles(
            List<MultipartFile> introductionImages,
            List<MultipartFile> bestForImages,
            List<MultipartFile> attachmentFiles,
            Long memberId,
            String markdownIntroduction,
            String markdownBestFor) {

        Map<ImageType, List<FileInfoDto>> result = new HashMap<>();

        // 파일 업로드 처리를 하나의 유틸리티 메서드로 분리
        processImagesForType(result, introductionImages, ImageType.MARKDOWN_SELF_INTRODUCTION,
                memberId, markdownIntroduction);
        processImagesForType(result, bestForImages, ImageType.MARKDOWN_RECOMMENDATION,
                memberId, markdownBestFor);
        processImagesForType(result, attachmentFiles, ImageType.NORMAL,
                memberId, null);

        return result;
    }

    // 각 이미지 유형별 처리를 담당하는 도우미 메서드
    private void processImagesForType(Map<ImageType, List<FileInfoDto>> result,
                                      List<MultipartFile> images,
                                      ImageType imageType,
                                      Long memberId,
                                      String markdownText) {
        if (images != null && !images.isEmpty()) {
            List<FileInfoDto> files = uploadFiles(images, imageType, memberId, markdownText);
            result.put(imageType, files);
        }
    }

    //파일 업로드 처리
    @Transactional
    public List<FileInfoDto> uploadFiles(List<MultipartFile> files, ImageType imageType, Long memberId, String markdownText) {
        // 멤버 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 멘토 정보 (있는 경우)
        Mentor mentor = mentorRepository.findByMemberId(memberId).orElse(null);

        // 파일 개수 제한 확인
        long currentFileCount = postAttachmentRepository.countByMemberId(memberId);
        if (currentFileCount + files.size() > maxFilesPerUser) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED,
                    "파일 업로드 제한을 초과했습니다. 최대 " + maxFilesPerUser + "개까지 가능합니다.");
        }

        List<FileInfoDto> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            // 파일 존재 확인
            if (file.isEmpty()) {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_REQUIRED);
            }

            // 파일 크기 확인
            if (file.getSize() > maxFileSize) {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_SIZE_EXCEEDED,
                        "파일 크기가 허용 범위를 초과했습니다. 최대 " + (maxFileSize / (1024 * 1024)) + "MB까지 가능합니다.");
            }

            try {
                // 원본 파일명 추출
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

                // 저장 파일명 생성 (UUID)
                String filename = UUID.randomUUID().toString() + "_" + originalFilename;

                // 저장 경로 생성
                String filePath = uploadDir + File.separator + filename;
                Path targetPath = Paths.get(filePath);

                // 디렉토리 생성 (없는 경우)
                Files.createDirectories(targetPath.getParent());

                // 파일 저장
                Files.copy(file.getInputStream(), targetPath);

                // 마크다운용 고유 식별자 (필요한 경우)
                String uniqueIdentifier = null;
                if (imageType != ImageType.NORMAL) {
                    uniqueIdentifier = UUID.randomUUID().toString();
                }

                // DB 저장
                PostAttachment attachment = PostAttachment.builder()
                        .filename(filename)
                        .originalFilename(originalFilename)
                        .storeFilePath(filePath)
                        .fileSize(file.getSize())
                        .member(member)
                        .mentor(mentor)
                        .imageType(imageType)
                        .uniqueIdentifier(uniqueIdentifier)
                        .build();

                postAttachmentRepository.save(attachment);

                FileInfoDto fileInfo;

                if (imageType != ImageType.NORMAL && uniqueIdentifier != null) { // 마크다운 이미지인 경우 URL 및 식별자 추가
                    fileInfo = FileInfoDto.builder()
                            .attachmentId(attachment.getId())
                            .originalFilename(originalFilename)
                            .fileSize(file.getSize())
                            .fileUrl("/api/files/markdown-images/" + uniqueIdentifier)
                            .uniqueIdentifier(uniqueIdentifier)
                            .build();
                } else { // 파일 정보 DTO 생성
                    fileInfo = FileInfoDto.builder()
                            .attachmentId(attachment.getId())
                            .originalFilename(originalFilename)
                            .fileSize(file.getSize())
                            .build();
                }

                uploadedFiles.add(fileInfo);

            } catch (IOException e) {
                log.error("파일 업로드 중 오류 발생", e);
                throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
            }
        }

        // 마크다운 텍스트가 제공된 경우, 이미지 참조 처리
        if (markdownText != null && !markdownText.isEmpty()) {
            processMarkdownImages(markdownText, member, mentor, imageType);
        }

        return uploadedFiles;
    }

    //마크다운 텍스트에서 이미지 참조를 처리하는 메서드
    private List<PostAttachment> processMarkdownImages(String markdownText, Member member, Mentor mentor, ImageType imageType) {
        List<PostAttachment> processedAttachments = new ArrayList<>();

        // 정규식으로 마크다운 이미지 구문 찾기
        // ![대체텍스트](/api/files/markdown-images/uniqueIdentifier) 형식 또는
        // ![대체텍스트](http://example.com/image.jpg) 형식 모두 처리
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(markdownText);

        while (matcher.find()) {
            String altText = matcher.group(1);
            String imageUrl = matcher.group(2);

            // 외부 URL인 경우 건너뛰기 (http://, https://)
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                continue;
            }

            // 내부 이미지 참조 처리
            if (imageUrl.contains("/api/files/markdown-images/")) {
                // URL에서 uniqueIdentifier 추출
                String uniqueIdentifier = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                // 해당 식별자로 첨부 파일 찾기
                Optional<PostAttachment> attachment = postAttachmentRepository.findByUniqueIdentifier(uniqueIdentifier);

                if (attachment.isPresent()) {
                    PostAttachment existingAttachment = attachment.get();

                    // 이미 처리된 이미지인지 확인
                    if (!processedAttachments.contains(existingAttachment)) {
                        processedAttachments.add(existingAttachment);

                        // 필요한 추가 처리 (예: 이미지와 특정 엔티티 연결)
                        // 이 부분은 비즈니스 요구사항에 따라 달라질 수 있음
                        log.info("마크다운 이미지 참조 처리: {}", existingAttachment.getId());
                    }
                } else {
                    log.warn("마크다운 텍스트에 참조된 이미지를 찾을 수 없습니다: {}", uniqueIdentifier);
                }
            }
            // 새로운 이미지 업로드가 필요한 경우 (data:image/... 형식의 base64 인코딩 이미지)
            else if (imageUrl.startsWith("data:image/")) {
                try {
                    // Base64 이미지 디코딩 및 저장
                    PostAttachment newImageAttachment = saveBase64Image(imageUrl, altText, member, mentor, imageType);
                    processedAttachments.add(newImageAttachment);
                } catch (Exception e) {
                    log.error("Base64 이미지 처리 중 오류 발생", e);
                }
            }
        }

        return processedAttachments;
    }

    //Base64 인코딩된 이미지를 디코딩하고 저장하는 메서드
    private PostAttachment saveBase64Image(String base64Image, String altText, Member member, Mentor mentor, ImageType imageType) {
        try {
            // "data:image/jpeg;base64," 형식에서 데이터 부분만 추출
            String[] parts = base64Image.split(",");
            String imageData = parts.length > 1 ? parts[1] : parts[0];

            // MIME 타입 추출 (예: "image/jpeg")
            String mimeType = "image/jpeg"; // 기본값
            if (parts.length > 1 && parts[0].contains("image/")) {
                mimeType = parts[0].substring(parts[0].indexOf("image/"), parts[0].indexOf(";base64"));
            }

            // 확장자 결정
            String extension = ".jpg"; // 기본값
            if (mimeType.equals("image/png")) extension = ".png";
            else if (mimeType.equals("image/gif")) extension = ".gif";
            else if (mimeType.equals("image/svg+xml")) extension = ".svg";

            // Base64 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(imageData);

            // 파일명 생성 (UUID)
            String filename = UUID.randomUUID().toString() + extension;

            // 저장 경로 생성
            String filePath = uploadDir + File.separator + filename;
            Path targetPath = Paths.get(filePath);

            // 디렉토리 생성 (없는 경우)
            Files.createDirectories(targetPath.getParent());

            // 파일 저장
            Files.write(targetPath, imageBytes);

            // 마크다운용 고유 식별자 생성
            String uniqueIdentifier = UUID.randomUUID().toString();

            // DB 저장
            PostAttachment attachment = PostAttachment.builder()
                    .filename(filename)
                    .originalFilename(altText + extension) // 대체 텍스트를 원본 파일명으로 사용
                    .storeFilePath(filePath)
                    .fileSize((long) imageBytes.length)
                    .member(member)
                    .mentor(mentor)
                    .imageType(imageType)
                    .uniqueIdentifier(uniqueIdentifier)
                    .build();

            return postAttachmentRepository.save(attachment);
        } catch (Exception e) {
            throw new RuntimeException("이미지 처리 중 오류가 발생했습니다.", e);
        }
    }

    //파일 삭제 처리
    @Transactional
    public void deleteFile(Long attachmentId, Long memberId) {
        // 파일 존재 확인
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND));

        // 권한 확인 (본인 파일만 삭제 가능)
        if (!attachment.getMember().getId().equals(memberId)) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_DELETE_NO_PERMISSION);
        }

        try {
            // 물리적 파일 삭제
            Path filePath = Paths.get(attachment.getStoreFilePath());
            Files.deleteIfExists(filePath);

            // DB 레코드 삭제
            postAttachmentRepository.delete(attachment);

        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생", e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    //일반 첨부 파일 다운로드
    public Map<String, Object> downloadFile(Long attachmentId, Long memberId) {
        // 파일 존재 확인
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND));

        // 권한 확인 (멘토와 관리자만 다운로드 가능)
        Mentor mentor = mentorRepository.findByMemberId(memberId).orElse(null);

        // TODO: 관리자 권한 확인 로직 추가 필요
        // UserDetails에서 권한 정보를 확인하여 ROLE_ADMIN 권한이 있는지 확인
        // 현재는 멘토 여부만 확인

        if (mentor == null) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_ACCESS_DENIED,
                    "해당 파일에 접근할 권한이 없습니다. 멘토 또는 관리자만 접근 가능합니다.");
        }

        try {
            // 파일 리소스 생성
            Path filePath = Paths.get(attachment.getStoreFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // 파일 타입 감지
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                // 다운로드용 파일 정보 반환
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("resource", resource);
                fileInfo.put("contentType", contentType);
                fileInfo.put("fileName", attachment.getOriginalFilename());
                fileInfo.put("contentDisposition", "attachment; filename=\"" + attachment.getOriginalFilename() + "\"");

                return fileInfo;
            } else {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR);
            }
        } catch (MalformedURLException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.INVALID_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    //마크다운 이미지 다운로드
    public Map<String, Object> downloadMarkdownImage(String uniqueIdentifier, Integer width, Integer height) {
        // 식별자로 파일 조회
        PostAttachment attachment = postAttachmentRepository.findByUniqueIdentifier(uniqueIdentifier)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND, "이미지를 찾을 수 없습니다."));

        // 마크다운 이미지인지 확인
        if (attachment.getImageType() == ImageType.NORMAL) {
            throw new PostAttachmentException(PostAttachmentErrorCode.NOT_MARKDOWN_IMAGE);
        }

        try {
            // 파일 리소스 생성
            Path filePath = Paths.get(attachment.getStoreFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // 이미지 타입 감지
                String contentType = Files.probeContentType(filePath);
                if (contentType == null || !contentType.startsWith("image/")) {
                    contentType = "image/jpeg"; // 기본값
                }

                // TODO: 이미지 리사이징 처리 (width, height 파라미터가 있는 경우)
                // 실제 구현 시에는 이미지 처리 라이브러리를 사용하여 리사이징 처리

                // 인라인 이미지 정보 반환
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("resource", resource);
                imageInfo.put("contentType", contentType);
                imageInfo.put("fileName", attachment.getOriginalFilename());
                imageInfo.put("contentDisposition", "inline; filename=\"" + attachment.getOriginalFilename() + "\"");

                return imageInfo;
            } else {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR, "이미지를 찾을 수 없거나 읽을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.INVALID_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("이미지 다운로드 중 오류가 발생했습니다.", e);
        }
    }
}