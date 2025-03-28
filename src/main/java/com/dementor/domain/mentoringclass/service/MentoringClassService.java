package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.entity.Schedule;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.mentoringclass.repository.ScheduleRepository;
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
    private final ScheduleRepository scheduleRepository;

    public List<MentoringClassFindResponse> selectClass(Long jobId) {
        return mentoringClassRepository.findAll()
                .stream()
                .map(mentoringClass -> new MentoringClassFindResponse(
                        mentoringClass.getId(),
                        mentoringClass.getStack(),
                        mentoringClass.getContent(),
                        mentoringClass.getTitle(),
                        mentoringClass.getPrice()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createClass(Long mentorId, MentoringClassCreateRequest request) {
        // TODO : 쿠키의 토큰에 저장된 key 에 맞게 수정 필요
        // 1. 멘토 정보 조회

        // 2. 스케줄 정보 생성
        List<Schedule> schedules = null;
        if (request.schedules() != null) {
            schedules = request.schedules().stream()
                    .map(scheduleRequest -> Schedule.builder()
                            .dayOfWeek(scheduleRequest.dayOfWeek())
                            .time(scheduleRequest.time())
                            .build())
                    .toList();
        }
        
        // 3. MentoringClass 엔티티 생성
        MentoringClass mentoringClass = MentoringClass.builder()
                .title(request.title())
                .stack(request.stack())
                .content(request.content())
                .price(request.price())
                .schedules(schedules)
                // .mentor(mentor) // 멘토 정보 연동
                .build();
        
        // 4. 멘토링 수업 저장
        return mentoringClassRepository.save(mentoringClass).getId();
    }
}
