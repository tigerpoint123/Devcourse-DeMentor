package com.dementor.domain.postattachment.entity;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename; // 서버에 저장된 파일명 (UUID 등 사용)

    @Column(nullable = false)
    private String originalFilename; //원본 파일명

    @Column(nullable = false)
    private String storeFilePath;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 회원 엔티티 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    // 파일이 마크다운 내 이미지인지 일반 첨부파일인지 구분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Builder
    public PostAttachment(String filename, String originalFilename, String storeFilePath,
                          Long fileSize, Member member, Mentor mentor, ImageType imageType) {
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.storeFilePath = storeFilePath;
        this.fileSize = fileSize;
        this.member = member;
        this.mentor = mentor;
        this.imageType = imageType != null ? imageType : ImageType.NORMAL;
    }

    // ImageType enum
    public enum ImageType {
        NORMAL,      // 일반 첨부 파일
        MARKDOWN     // 마크다운 내 이미지
    }
}
