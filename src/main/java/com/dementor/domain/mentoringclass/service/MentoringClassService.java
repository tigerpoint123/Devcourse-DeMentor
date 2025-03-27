package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
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
    public Long createClass(Long mentorId, MentoringClassCreateRequest request) {
        // TODO: mentorId를 통해 멘토 정보를 조회하고 연동
        // 1. 멘토 정보 조회
        // 2. MentoringClass 엔티티 생성
        MentoringClass mentoringClass = MentoringClass.builder()
                .title(request.title())
                .stack(request.stack())
                .content(request.content())
                .price(request.price())
                // .mentor(mentor) // 멘토 정보 연동
                .build();
        
        // 3. 저장 및 ID 반환
        return mentoringClassRepository.save(mentoringClass).getId();
    }
}
