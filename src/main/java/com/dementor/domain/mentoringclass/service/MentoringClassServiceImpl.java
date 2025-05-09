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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private static final String POPULAR_CLASSES_CACHE_KEY = "popular:classes:";
    private static final String CLASS_DETAIL_CACHE_KEY = "class:detail:";
    private static final long CACHE_TTL = 3600; // 1시간

    public Page<MentoringClassFindResponse> findAllClass(List<Long> jobId, Pageable pageable) {
        // 인기순 정렬인 경우에만 캐시 사용
        if (isPopularSort(pageable))
            return findPopularClassesWithCache(jobId, pageable);

        Page<MentoringClass> mentoringClasses;
        if (jobId == null || jobId.isEmpty()) {
            mentoringClasses = mentoringClassRepository.findAll(pageable);
        } else if (jobId.size() == 1) {
            mentoringClasses = mentoringClassRepository.findByMentor_Job_Id(jobId.get(0), pageable);
        } else {
            mentoringClasses = mentoringClassRepository.findByMentor_Job_IdIn(jobId, pageable);
        }

        return mentoringClasses.map(MentoringClassFindResponse::from);
    }

    private boolean isPopularSort(Pageable pageable) {
        return pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("favoriteCount"));
    }

    private Page<MentoringClassFindResponse> findPopularClassesWithCache(List<Long> jobId, Pageable pageable) {
        String cacheKey = POPULAR_CLASSES_CACHE_KEY +
                (jobId != null ? String.join(",", jobId.stream().map(String::valueOf).toList()) : "all") +
                ":page:" + pageable.getPageNumber();

        try {
            // Redis에서 캐시된 결과 조회
            String cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return deserializePage(cachedResult);
            }
        } catch (Exception e) {
            log.error("Redis 캐시 조회 실패", e);
        }

        // 캐시 미스 시 DB 조회
        Page<MentoringClass> mentoringClasses;
        if (jobId == null || jobId.isEmpty()) {
            mentoringClasses = mentoringClassRepository.findAll(pageable);
        } else if (jobId.size() == 1) {
            mentoringClasses = mentoringClassRepository.findByMentor_Job_Id(jobId.get(0), pageable);
        } else {
            mentoringClasses = mentoringClassRepository.findByMentor_Job_IdIn(jobId, pageable);
        }

        Page<MentoringClassFindResponse> result = mentoringClasses.map(MentoringClassFindResponse::from);

        try {
            // 결과를 Redis에 캐싱
            redisTemplate.opsForValue().set(
                    cacheKey,
                    serializePage(result),
                    CACHE_TTL,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Redis 캐시 저장 실패", e);
        }

        return result;
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

    public MentoringClassDetailResponse findOneClassFromRedis(Long classId) {
        String cacheKey = CLASS_DETAIL_CACHE_KEY + classId;

        try {
            String cachedResult = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                return objectMapper.readValue(cachedResult, MentoringClassDetailResponse.class);
            }
        } catch (Exception e) {
            log.error("Redis 캐시 조회 실패", e);
        }

        MentoringClassDetailResponse response = findOneClassFromDb(classId);

        try {
            // 결과를 Redis에 캐싱
            redisTemplate.opsForValue().set(
                cacheKey,
                objectMapper.writeValueAsString(response),
                CACHE_TTL,
                TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Redis 캐시 저장 실패", e);
        }

        return response;
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
                new TypeReference<List<MentoringClassFindResponse>>() {}
        );

        return new PageImpl<>(content);
    }

}
