package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentor.dto.response.MyMentoringResponse;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MentoringClassService {
    Page<MentoringClassFindResponse> findAllClass(List<Long> jobId, Pageable pageable);

    @Transactional
    MentoringClassDetailResponse createClass(Long mentorId, MentoringClassCreateRequest request);

    MentoringClassDetailResponse findOneClass(Long classId);

    @Transactional
    void deleteClass(Long classId) ;

    @Transactional
    MentoringClassUpdateResponse updateClass(Long classId, Long memberId, MentoringClassUpdateRequest request);

    List<MyMentoringResponse> getMentorClassFromMentor(Long memberId);

    int findFavoriteCount(Long classId);
}
