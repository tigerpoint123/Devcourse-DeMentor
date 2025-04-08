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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    //요청한 사용자가 파일의 소유자인지 확인
    public boolean isFileOwner(Long fileId, Long userId) {
        if (fileId == null || userId == null) {
            return false; // null 값이 있으면 소유자가 아님
        }
        return postAttachmentRepository.findById(fileId)
                .map(file -> file.getMember().getId().equals(userId))
                .orElse(false);
    }

    // 요청한 사용자가 마크다운 이미지의 소유자인지 확인
    public boolean isMarkdownImageOwner(String uniqueIdentifier, Long userId) {
        return postAttachmentRepository.findByUniqueIdentifier(uniqueIdentifier)
                .map(file -> file.getMember().getId().equals(userId))
                .orElse(false);
    }

    //파일 업로드 처리
    @Transactional
    public List<FileInfoDto> uploadFiles(List<MultipartFile> files, ImageType imageType, Long memberId, String markdownText) {
        // 멤버 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.UNAUTHORIZED_ACCESS,
                        "사용자를 찾을 수 없습니다."));

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
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_ERROR,
                        "파일 저장 중 오류가 발생했습니다: " + e.getMessage());
            }
        }

        // 마크다운 텍스트가 제공된 경우, 이미지 참조 처리
        if (markdownText != null && !markdownText.isEmpty()) {
            List<PostAttachment> markdownAttachments = processMarkdownImages(markdownText, member, mentor, imageType);

            // 처리된 마크다운 이미지를 응답에 추가
            for (PostAttachment attachment : markdownAttachments) {
                FileInfoDto fileInfo = FileInfoDto.builder()
                        .attachmentId(attachment.getId())
                        .originalFilename(attachment.getOriginalFilename())
                        .fileSize(attachment.getFileSize())
                        .fileUrl("/api/files/markdown-images/" + attachment.getUniqueIdentifier())
                        .uniqueIdentifier(attachment.getUniqueIdentifier())
                        .build();

                uploadedFiles.add(fileInfo);
            }
        }

        return uploadedFiles;
    }

    //마크다운 텍스트에서 이미지 참조를 처리하는 메서드
    private List<PostAttachment> processMarkdownImages(String markdownText, Member member, Mentor mentor, ImageType imageType) {
        List<PostAttachment> processedAttachments = new ArrayList<>();

        // 마크다운에서 이미지 패턴 찾기
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(markdownText);

        while (matcher.find()) {
            String altText = matcher.group(1);
            String imageUrl = matcher.group(2);

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
                        log.info("처리된 이미지 목록에 추가");
                    } else {
                        log.info("이미 처리된 이미지 - 중복 제외");
                    }
                } else {
                    log.warn("이미지를 찾을 수 없음 - uniqueIdentifier: {}", uniqueIdentifier);
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

            // GitHub 및 외부 URL 처리
            else if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                String uniqueIdentifier = UUID.randomUUID().toString();

                // 확장자 추출 시도
                String extension = ".jpg"; // 기본값
                if (imageUrl.toLowerCase().endsWith(".png")) extension = ".png";
                else if (imageUrl.toLowerCase().endsWith(".gif")) extension = ".gif";
                else if (imageUrl.toLowerCase().endsWith(".svg")) extension = ".svg";

                // 외부 URL은 storeFilePath에 그대로 저장하여 구분
                PostAttachment attachment = PostAttachment.builder()
                        .filename(uniqueIdentifier + extension)
                        .originalFilename(altText + extension)
                        .storeFilePath(imageUrl) // URL을 그대로 저장
                        .fileSize(0L) // 실제 크기는 알 수 없음
                        .member(member)
                        .mentor(mentor)
                        .imageType(imageType)
                        .uniqueIdentifier(uniqueIdentifier)
                        .build();

                PostAttachment savedAttachment = postAttachmentRepository.save(attachment);
                processedAttachments.add(savedAttachment);
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

            // Base64 문자열 정리 (공백, 개행 문자 제거)
            imageData = imageData.trim().replaceAll("\\s", "");

            // Base64 디코딩
            byte[] imageBytes;
            try {
                imageBytes = Base64.getDecoder().decode(imageData);
            } catch (IllegalArgumentException e) {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_ERROR,
                        "올바르지 않은 Base64 인코딩 형식입니다: " + e.getMessage());
            }

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
        } catch (PostAttachmentException e) {
            throw e; // 이미 처리된 예외는 그대로 던짐
        } catch (Exception e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_ERROR,
                    "이미지 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 마크다운 전용 처리 메서드
    @Transactional
    public List<FileInfoDto> processMarkdownOnly(String markdownText, ImageType imageType, Long memberId) {
        if (markdownText == null || markdownText.isEmpty()) {
            return new ArrayList<>();
        }

        // 멤버 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.UNAUTHORIZED_ACCESS,
                        "사용자를 찾을 수 없습니다."));

        // 멘토 정보 (있는 경우)
        Mentor mentor = mentorRepository.findByMemberId(memberId).orElse(null);

        List<FileInfoDto> result = new ArrayList<>();

        // 마크다운에서 이미지 추출 및 처리
        List<PostAttachment> processedAttachments = processMarkdownImages(markdownText, member, mentor, imageType);

        // 이미지가 없는 경우 빈 리스트 반환
        if (processedAttachments.isEmpty()) {
            return result;
        }

        // 파일 개수 제한 확인
        long currentFileCount = postAttachmentRepository.countByMemberId(memberId);
        if (currentFileCount + processedAttachments.size() > maxFilesPerUser) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED,
                    "파일 업로드 제한을 초과했습니다. 최대 " + maxFilesPerUser + "개까지 가능합니다.");
        }

        // 추출된 이미지들을 DTO로 변환
        for (PostAttachment attachment : processedAttachments) {
            FileInfoDto fileInfo = FileInfoDto.builder()
                    .attachmentId(attachment.getId())
                    .originalFilename(attachment.getOriginalFilename())
                    .fileSize(attachment.getFileSize())
                    .fileUrl("/api/files/markdown-images/" + attachment.getUniqueIdentifier())
                    .uniqueIdentifier(attachment.getUniqueIdentifier())
                    .build();

            result.add(fileInfo);
        }

        return result;
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
            // storeFilePath가 http로 시작하면 외부 URL로 판단하여 물리적 삭제는 건너뜀
            if (!attachment.getStoreFilePath().startsWith("http")) {
                // 물리적 파일 삭제
                Path filePath = Paths.get(attachment.getStoreFilePath());
                Files.deleteIfExists(filePath);
            }

            // DB 레코드 삭제
            postAttachmentRepository.delete(attachment);

        } catch (IOException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_UPLOAD_ERROR,
                    "파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //일반 첨부 파일 다운로드
    public Map<String, Object> downloadFile(Long attachmentId, Long memberId) {
        // 파일 존재 확인
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND));

        Mentor mentor = mentorRepository.findByMemberId(memberId).orElse(null);

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

                return fileInfo;
            } else {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR);
            }
        } catch (MalformedURLException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.INVALID_FILE_PATH);
        } catch (IOException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR,
                    "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //마크다운 이미지 다운로드
    public Map<String, Object> downloadMarkdownImage(String uniqueIdentifier, Integer width, Integer height) {
        // 식별자로 파일 조회
        PostAttachment attachment = postAttachmentRepository.findByUniqueIdentifier(uniqueIdentifier)
                .orElseThrow(() -> new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND, "이미지를 찾을 수 없습니다."));

        // storeFilePath가 http로 시작하면 외부 URL로 판단
        if (attachment.getStoreFilePath().startsWith("http")) {
            try {
                URL url = new URL(attachment.getStoreFilePath());

                // HttpURLConnection 사용
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(10000); // 10초 타임아웃
                connection.setReadTimeout(10000);

                // 응답 코드 확인
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR,
                            "외부 이미지에 접근할 수 없습니다. 응답 코드: " + responseCode);
                }

                // MIME 타입 확인
                String contentType = connection.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    contentType = "image/jpeg"; // 기본값
                }

                // 이미지 데이터 읽기 및 ByteArrayOutputStream에 저장
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try (InputStream inputStream = connection.getInputStream()) {
                    if (width != null && height != null) {
                        // 리사이징 처리
                        Thumbnails.of(inputStream)
                                .size(width, height)
                                .toOutputStream(outputStream);
                    } else {
                        // 원본 이미지 그대로 복사
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }

                final byte[] imageBytes = outputStream.toByteArray();
                outputStream.close();

                // ByteArrayResource 생성
                ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                    @Override
                    public String getFilename() {
                        return attachment.getOriginalFilename() != null ?
                                attachment.getOriginalFilename() : "image.jpg";
                    }
                };

                Map<String, Object> externalInfo = new HashMap<>();
                externalInfo.put("resource", resource);
                externalInfo.put("contentType", contentType);
                externalInfo.put("fileName", attachment.getOriginalFilename());
                externalInfo.put("isExternalUrl", true);

                return externalInfo;

            } catch (Exception e) {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR,
                        "외부 이미지 로드 중 오류가 발생했습니다: " + e.getMessage());
            }
        }

        // 내부 파일 처리 로직 (로컬 파일)
        try {
            // 파일 경로 확인 및 검증
            Path filePath = Paths.get(attachment.getStoreFilePath());
            File sourceFile = filePath.toFile();

            if (!sourceFile.exists() || !sourceFile.canRead()) {
                throw new PostAttachmentException(PostAttachmentErrorCode.FILE_NOT_FOUND,
                        "파일을 찾을 수 없거나 읽을 수 없습니다: " + attachment.getStoreFilePath());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Resource resource;

            if (width != null && height != null) {
                // 메모리에서 직접 리사이징
                Thumbnails.of(sourceFile)
                        .size(width, height)
                        .toOutputStream(baos);

                final byte[] resizedImageBytes = baos.toByteArray();
                baos.close();

                resource = new ByteArrayResource(resizedImageBytes) {
                    @Override
                    public String getFilename() {
                        return attachment.getOriginalFilename();
                    }
                };
            } else {
                // 파일 바이트를 직접 읽어 ByteArrayResource 생성
                byte[] fileContent = Files.readAllBytes(filePath);
                resource = new ByteArrayResource(fileContent) {
                    @Override
                    public String getFilename() {
                        return attachment.getOriginalFilename();
                    }
                };
            }

            // 이미지 타입 감지
            String contentType = Files.probeContentType(filePath);
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = "image/jpeg"; // 기본값
            }

            // 반환 정보
            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("resource", resource);
            imageInfo.put("contentType", contentType);
            imageInfo.put("fileName", attachment.getOriginalFilename());

            return imageInfo;
        } catch (IOException e) {
            throw new PostAttachmentException(PostAttachmentErrorCode.FILE_READ_ERROR,
                    "이미지 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}