package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.entity.Schedule;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.mentoringclass.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // 현재 인증된 사용자의 정보를 가져옴
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // TODO: username을 통해 멘토 정보를 조회하고 연동
        // 1. 멘토 정보 조회
        // 2. MentoringClass 엔티티 생성
        MentoringClass mentoringClass = MentoringClass.builder()
                .title(request.title())
                .stack(request.stack())
                .content(request.content())
                .price(request.price())
                // .mentor(mentor) // 멘토 정보 연동
                .build();
        
        // 3. 멘토링 클래스 저장
        mentoringClass = mentoringClassRepository.save(mentoringClass);
        
        // 4. 스케줄 정보 저장
        if (request.schedules() != null) {
            request.schedules().forEach(scheduleRequest -> {
                Schedule schedule = Schedule.builder()
                        .dayOfWeek(scheduleRequest.dayOfWeek())
                        .time(scheduleRequest.time())
                        .mentoringClass(mentoringClass)
                        .build();
                scheduleRepository.save(schedule);
            });
        }
        
        return mentoringClass.getId();
    }
}
