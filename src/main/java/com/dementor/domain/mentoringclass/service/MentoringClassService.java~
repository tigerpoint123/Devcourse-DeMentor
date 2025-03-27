package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.dto.response.MentorInfo;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
                        mentoringClass.getPrice(),
                        new MentorInfo(
                                mentoringClass.getMentor().getMentorId(),
                                mentoringClass.getMentor().getName(),
                                mentoringClass.getMentor().getCareer(),
                                mentoringClass.getMentor().getIntroduction()

                        )
                ))
                .collect(Collectors.toList());
    }
}
