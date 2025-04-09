package com.dementor.firebase.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FirebaseStorageService {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Value("${firebase.storage.url-expiry}")
    private Long urlExpiry;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(firebaseConfigPath).getInputStream()))
                    .setStorageBucket(storageBucket)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 성공");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패", e);
            throw new RuntimeException("Firebase 초기화 중 오류 발생", e);
        }
    }

    // MultipartFile 업로드
    public String uploadFile(MultipartFile file, String directory) {
        try {
            return uploadFile(file.getBytes(), file.getOriginalFilename(), file.getContentType(), directory);
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 바이트 배열 업로드 (Base64 이미지 등)
    public String uploadFile(byte[] fileData, String originalFilename, String contentType, String directory) {
        try {
            // 파일명 생성 (UUID + 원본 파일명)
            String filename = UUID.randomUUID().toString();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                // 확장자 추출
                int lastDotIndex = originalFilename.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    filename += originalFilename.substring(lastDotIndex);
                }
            } else {
                // 컨텐츠 타입에 따른 확장자 추가
                if (contentType != null) {
                    if (contentType.equals("image/png")) filename += ".png";
                    else if (contentType.equals("image/jpeg")) filename += ".jpg";
                    else if (contentType.equals("image/gif")) filename += ".gif";
                    else if (contentType.equals("application/pdf")) filename += ".pdf";
                    else if (contentType.equals("text/plain")) filename += ".txt";
                    else filename += ".bin";
                }
            }

            // 저장 경로
            String fullPath = directory + "/" + filename;

            // Firebase Storage에 업로드
            BlobId blobId = BlobId.of(storageBucket, fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            Storage storage = StorageClient.getInstance().bucket().getStorage();
            Blob blob = storage.create(blobInfo, fileData);

            // 서명된 URL 생성
            String downloadUrl = blob.signUrl(urlExpiry, TimeUnit.DAYS).toString();
            log.info("Firebase에 파일 업로드 완료: {}", fullPath);

            return downloadUrl;
        } catch (Exception e) {
            log.error("Firebase Storage 파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 파일 경로 추출
            String filePath = extractPathFromUrl(fileUrl);
            if (filePath == null) {
                log.warn("삭제할 파일 경로를 찾을 수 없음: {}", fileUrl);
                return;
            }

            // Firebase Storage에서 삭제
            BlobId blobId = BlobId.of(storageBucket, filePath);
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("Firebase Storage에서 파일 삭제 완료: {}", filePath);
            } else {
                log.warn("Firebase Storage에서 파일을 찾을 수 없음: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Firebase Storage 파일 삭제 실패", e);
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage());
        }
    }

    // URL에서 파일 경로 추출
    private String extractPathFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(storageBucket)) {
            return null;
        }

        try {
            // Firebase Storage URL 형식: https://firebasestorage.googleapis.com/v0/b/BUCKET/o/PATH?token=...
            int pathStart = fileUrl.indexOf("/o/") + 3;
            int pathEnd = fileUrl.indexOf("?");

            if (pathStart >= 3 && pathEnd > pathStart) {
                String encodedPath = fileUrl.substring(pathStart, pathEnd);
                // URL 디코딩
                return java.net.URLDecoder.decode(encodedPath, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            log.error("URL에서 파일 경로 추출 실패", e);
            return null;
        }
    }
}
