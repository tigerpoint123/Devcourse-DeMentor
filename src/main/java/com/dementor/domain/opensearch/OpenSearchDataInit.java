package com.dementor.domain.opensearch;

import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.opensearch.domain.JobInfo;
import com.dementor.domain.opensearch.domain.MentorInfo;
import com.dementor.domain.opensearch.domain.MentoringClassDocument;
import com.dementor.domain.opensearch.service.OpenSearchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenSearchDataInit {

    private final OpenSearchService openSearchService;
    private final MentoringClassRepository mentoringClassRepository;

    // TODO : 대용량 데이터라면 ?
    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        // 1. 인덱스 존재 여부 확인 및 생성
        try {
            openSearchService.createMentoringClassIndex("mentoring_class");
        } catch (Exception e) {
            // 이미 존재하면 무시하거나, 예외 처리
        }

        List<MentoringClass> mentoringClasses = mentoringClassRepository.findAllWithMentor();
        for (MentoringClass entity : mentoringClasses) {
            MentoringClassDocument doc = new MentoringClassDocument();
            doc.setId(entity.getId().toString());
            doc.setTitle(entity.getTitle());
            doc.setContent(entity.getContent());
            doc.setStack(entity.getStack() != null ? String.join(",", entity.getStack()) : "");
            doc.setPrice(entity.getPrice());
            
            // MentorInfo 설정
            MentorInfo mentorInfo = new MentorInfo();
            mentorInfo.setId(entity.getMentor().getId());
            mentorInfo.setName(entity.getMentor().getName());
            mentorInfo.setCareer(entity.getMentor().getCareer());

            JobInfo jobInfo = new JobInfo();
            jobInfo.setId(entity.getMentor().getJob().getId());
            jobInfo.setName(entity.getMentor().getJob().getName());
            mentorInfo.setJob(jobInfo);

            doc.setMentor(mentorInfo);
            doc.setFavoriteCount(entity.getFavoriteCount());
            try {
                openSearchService.saveDocument("mentoring_class", doc.getId(), doc);
            } catch (Exception e) {
                // 예외 처리 로직 추가 필요
                e.printStackTrace();
            }
        }
    }
}
