package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentor.dto.response.MyMentoringResponse;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface MentoringClassService {
    Page<MentoringClassFindResponse> findAllClass(List<Long> jobId, Pageable pageable);

    @Transactional
    MentoringClassDetailResponse createClass(Long mentorId, MentoringClassCreateRequest request) throws IOException;

    MentoringClassDetailResponse findOneClassFromRedis(Long classId);

    MentoringClassDetailResponse findOneClassFromDb(Long classId);

    @Transactional
    void deleteClass(Long classId) throws IOException;

    @Transactional
    MentoringClassDetailResponse updateClass(Long classId, Long memberId, MentoringClassUpdateRequest request) throws IOException;

    List<MyMentoringResponse> getMentorClassFromMentor(Long memberId);

    int findFavoriteCount(Long classId);
}
