package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.entity.MentoringClassEntity;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringClassService {
    private final MentoringClassRepository mentoringClassRepository;

    public List<MentoringClassEntity> selectClass(Long jobId) {


        return mentoringClassRepository.findAll();
    }
}
