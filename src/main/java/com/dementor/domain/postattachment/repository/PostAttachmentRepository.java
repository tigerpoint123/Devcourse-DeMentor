package com.dementor.domain.postattachment.repository;

import com.dementor.domain.postattachment.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Long> {
    List<PostAttachment> findByApplymentId(Long memberId);
}
