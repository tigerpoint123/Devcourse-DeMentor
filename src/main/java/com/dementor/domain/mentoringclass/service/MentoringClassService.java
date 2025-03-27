package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentoringClassService {
    private final MentoringClassRepository mentoringClassRepository;

    public List<MentoringClassFindResponse> selectClass(Long jobId) {
        return mentoringClassRepository.findAll()
                .stream()
                .map(mentoringClass -> new MentoringClassFindResponse(
                        mentoringClass.getId(),
                        mentoringClass.getStack(),
                        mentoringClass.getContent(),
                        mentoringClass.getTitle(),
                        mentoringClass.getPrice()
//                        new MentorInfo(
//                                mentoringClass.getMentor().getId(),
//                                mentoringClass.getMentor().getName(),
//                                mentoringClass.getMentor().getCareer(),
//                                mentoringClass.getMentor().getIntroduction()
//
//                        )
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createClass(MentoringClassCreateRequest request) {
        // TODO: 멘토 도메인 개발 완료 후 실제 생성 로직 구현
        // 1. MentoringClass 엔티티 생성
        // 2. 멘토 정보 연동
        // 3. 저장 및 ID 반환
        return 1L;
    }
}
