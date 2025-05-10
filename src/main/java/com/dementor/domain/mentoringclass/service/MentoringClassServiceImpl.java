package com.dementor.domain.mentoringclass.service;

import com.dementor.domain.mentor.dto.response.MyMentoringResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.exception.MentorErrorCode;
import com.dementor.domain.mentor.exception.MentorException;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassCreateRequest;
import com.dementor.domain.mentoringclass.dto.request.MentoringClassUpdateRequest;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassDetailResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassFindResponse;
import com.dementor.domain.mentoringclass.dto.response.MentoringClassUpdateResponse;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.entity.Schedule;
import com.dementor.domain.mentoringclass.exception.MentoringClassException;
import com.dementor.domain.mentoringclass.exception.MentoringClassExceptionCode;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.mentoringclass.repository.ScheduleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MentoringClassServiceImpl implements MentoringClassService {

    private final MentoringClassRepository mentoringClassRepository;
    private final ScheduleRepository scheduleRepository;
    private final MentorRepository mentorRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final int POPULAR_CLASS_LIMIT = 10; // 인기 클래스 기준 수

    // 서버 시작 시 자동으로 pre-warming 실행
    @PostConstruct
    public void init() {
        log.info("멘토링 클래스 pre-warming 시작");
        warmUpPopularClasses();
        log.info("멘토링 클래스 pre-warming 완료");
    }

    // 1시간마다 pre-warming 실행 (캐시 TTL과 동일하게 설정)
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000 밀리초
    public void scheduledPreWarming() {
        log.info("멘토링 클래스 주기적 pre-warming 시작");
        // 비동기로 pre-warming 실행
        CompletableFuture.runAsync(() -> {
            try {
                warmUpPopularClasses();
            } catch (Exception e) {
                log.error("멘토링 클래스 pre-warming 실패", e);
            }
        });
        log.info("멘토링 클래스 주기적 pre-warming 완료");
    }

    public Page<MentoringClassFindResponse> findAllClass(List<Long> jobId, Pageable pageable) {
        Page<MentoringClass> mentoringClasses;
        if (jobId == null || jobId.isEmpty())
            mentoringClasses = mentoringClassRepository.findAll(pageable);
        else if (jobId.size() == 1)
            mentoringClasses = mentoringClassRepository.findByMentor_Job_Id(jobId.get(0), pageable);
        else
            mentoringClasses = mentoringClassRepository.findByMentor_Job_IdIn(jobId, pageable);

        return mentoringClasses.map(MentoringClassFindResponse::from);
    }

    @Transactional
    public MentoringClassDetailResponse createClass(Long mentorId, MentoringClassCreateRequest request) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new MentorException(MentorErrorCode.MENTOR_NOT_FOUND));

        // 입력값 검증
        if (request.title() == null || request.content() == null)
            throw new MentoringClassException(MentoringClassExceptionCode.TITLE_OR_CONTENT_INPUT_NULL);
        else if (request.price() < 0)
            throw new MentoringClassException(MentoringClassExceptionCode.MINUS_PRICE);
        else if (request.schedules() == null)
            throw new MentoringClassException(MentoringClassExceptionCode.EMPTY_SCHEDULE);
        else if (request.stack() == null)
            throw new MentoringClassException(MentoringClassExceptionCode.EMPTY_STACK);

        MentoringClass mentoringClass = MentoringClass.builder()
                .title(request.title())
                .stack(String.join(",", request.stack()))
                .content(request.content())
                .price(request.price())
                .mentor(mentor)
                .build();
        mentoringClass = mentoringClassRepository.save(mentoringClass);

        // 스케줄 저장 로직 별도로 관리
        MentoringClass savedMentoringClass = mentoringClass;
        List<Schedule> schedules = request.schedules().stream()
                .map(scheduleRequest -> Schedule.builder()
                        .mentoringClassId(savedMentoringClass.getId())
                        .dayOfWeek(scheduleRequest.dayOfWeek())
                        .time(scheduleRequest.time())
                        .build())
                .map(scheduleRepository::save)
                .toList();

        return MentoringClassDetailResponse.from(mentoringClass, schedules);
    }

    @Cacheable(value = "mentoringClass", key = "#classId", unless = "#result == null")
    public MentoringClassDetailResponse findOneClassFromRedis(Long classId) {
        log.info("캐시 미스 발생: 멘토링 클래스 ID {}", classId);
        
        // DB에서 조회 (캐시에 없을 때만 실행됨)
        MentoringClassDetailResponse response = findOneClassFromDb(classId);
        
        // 인기 클래스인 경우에만 pre-warming 실행
        if (isPopularClass(classId)) {
            log.info("인기 클래스 캐시 미스: 멘토링 클래스 ID {} - pre-warming 시작", classId);
            // 비동기로 pre-warming 실행
            CompletableFuture.runAsync(() -> {
                try {
                    warmUpPopularClasses();
                    log.info("인기 클래스 pre-warming 완료: 멘토링 클래스 ID {}", classId);
                } catch (Exception e) {
                    log.error("멘토링 클래스 pre-warming 실패: {}", classId, e);
                }
            });
        }
        
        return response;
    }

    private boolean isPopularClass(Long classId) {
        try {
            // 인기순 정렬된 상위 10개 클래스 ID 조회
            List<Long> topClassIds = mentoringClassRepository.findTopIdsByOrderByFavoriteCountDesc(
                PageRequest.of(0, POPULAR_CLASS_LIMIT)
            );
            
            // 현재 클래스가 상위 10개에 포함되는지 확인
            return topClassIds.contains(classId);
        } catch (Exception e) {
            log.error("인기 클래스 확인 실패: {}", classId, e);
            return false;
        }
    }

    private void warmUpPopularClasses() {
        try {
            // 인기 클래스 ID 목록 조회
            List<Long> popularClassIds = mentoringClassRepository.findTopIdsByOrderByFavoriteCountDesc(
                PageRequest.of(0, POPULAR_CLASS_LIMIT)
            );

            // 병렬로 pre-warming 실행
            popularClassIds.parallelStream().forEach(classId -> {
                try {
                    findOneClassFromRedis(classId);
                } catch (Exception e) {
                    log.error("멘토링 클래스 ID {} pre-warming 실패: {}", classId, e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("인기 클래스 pre-warming 실패", e);
        }
    }

    public MentoringClassDetailResponse findOneClassFromDb(Long classId) {
        // 멘토링 클래스 정보 조회
        MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));
        // 조회한 클래스 id로 스케줄 정보 조회
        List<Schedule> schedules = scheduleRepository.findByMentoringClassId(classId);
        // 같이 response
        return MentoringClassDetailResponse.from(mentoringClass, schedules);
    }

    @Transactional
    public void deleteClass(Long classId) {
        MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByMentoringClassId(classId);
        scheduleRepository.deleteAll(schedules);

        mentoringClassRepository.delete(mentoringClass);
    }

    @Transactional
    public MentoringClassUpdateResponse updateClass(Long classId, Long memberId, MentoringClassUpdateRequest request) {
        MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));

        if (!mentoringClass.getMentor().getId().equals(memberId))
            throw new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_UNAUTHORIZED);

        // 일정 아닌 정보
        if (request.title() != null)
            mentoringClass.updateTitle(request.title());
        if (request.content() != null)
            mentoringClass.updateContent(request.content());
        if (request.price() != null)
            mentoringClass.updatePrice(request.price());
        if (request.stack() != null)
            mentoringClass.updateStack(request.stack());

        // 일정 정보
        Schedule schedule = scheduleRepository.findByMentoringClassId(classId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.SCHEDULE_NOT_FOUND));

        if (request.schedule() != null) {
            schedule.updateDayOfWeek(request.schedule().dayOfWeek());
            schedule.updateTime(request.schedule().time());
        }

        return new MentoringClassUpdateResponse(
                mentoringClass.getId(),
                new MentoringClassUpdateResponse.MentorInfo(
                        mentoringClass.getMentor().getId(),
                        mentoringClass.getMentor().getName(),
                        mentoringClass.getMentor().getJob().getName(),
                        mentoringClass.getMentor().getCareer()
                ),
                mentoringClass.getStack(),
                mentoringClass.getContent(),
                mentoringClass.getTitle(),
                mentoringClass.getPrice(),
                new MentoringClassUpdateResponse.ScheduleInfo(
                        schedule.getDayOfWeek(),
                        schedule.getTime()
                )
        );
    }

    public List<MyMentoringResponse> getMentorClassFromMentor(Long memberId) {
        List<MentoringClass> mentoringList = mentoringClassRepository.findByMentor_Id(memberId);

        return mentoringList.stream()
                .map(mentoringClass -> new MyMentoringResponse(
                        mentoringClass.getId(),
                        mentoringClass.getStack(),
                        mentoringClass.getContent(),
                        mentoringClass.getTitle(),
                        mentoringClass.getPrice()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public int findFavoriteCount(Long classId) {
        return mentoringClassRepository.findFavoriteCountById(classId);
    }

    private String serializePage(Page<MentoringClassFindResponse> page) {
        try {
            return objectMapper.writeValueAsString(page.getContent());
        } catch (JsonProcessingException e) {
            log.error("Page 직렬화 실패", e);
            throw new RuntimeException("Page 직렬화 실패", e);
        }
    }

    private Page<MentoringClassFindResponse> deserializePage(String json) {
        List<MentoringClassFindResponse> content = objectMapper.convertValue(
                json,
                new TypeReference<List<MentoringClassFindResponse>>() {
                }
        );

        return new PageImpl<>(content);
    }

}
