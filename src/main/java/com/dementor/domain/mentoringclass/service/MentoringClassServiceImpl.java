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
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.entity.Schedule;
import com.dementor.domain.mentoringclass.exception.MentoringClassException;
import com.dementor.domain.mentoringclass.exception.MentoringClassExceptionCode;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import com.dementor.domain.mentoringclass.repository.ScheduleRepository;
import com.dementor.domain.opensearch.document.mentoringClass.MentoringClassDocument;
import com.dementor.domain.opensearch.service.OpenSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MentoringClassServiceImpl implements MentoringClassService, ApplicationListener<ContextRefreshedEvent> {

    private final MentoringClassRepository mentoringClassRepository;
    private final ScheduleRepository scheduleRepository;
    private final MentorRepository mentorRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
//    private final ElasticSearchService elasticSearchService;
    private final OpenSearchService elasticSearchService;

    private static final int POPULAR_CLASS_LIMIT = 10;
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final String openSearchIndexName = "mentoring_class";

    // 서버 시작 완료 후 실행
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("서버 시작 완료: 멘토링 클래스 pre-warming 시작");
        try {
            warmUpPopularClasses();
            log.info("멘토링 클래스 pre-warming 완료");
        } catch (Exception e) {
            log.error("서버 시작 시 pre-warming 실패", e);
        }
    }

    // 1시간마다 pre warming 실행 (캐시 TTL과 동일하게 설정)
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000 밀리초
    public void scheduledPreWarming() {
        log.info("멘토링 클래스 주기적 pre-warming 시작");
        try {
            warmUpPopularClasses();
            log.info("멘토링 클래스 주기적 pre-warming 완료");
        } catch (Exception e) {
            log.error("주기적 pre-warming 실패", e);
        }
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
    public MentoringClassDetailResponse createClass(Long mentorId, MentoringClassCreateRequest request) throws IOException {
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

        // 오픈서치 인덱싱
        MentoringClassDocument document = MentoringClassDocument.from(mentoringClass);
        elasticSearchService.saveDocument(openSearchIndexName, document.getId(), document);

        return MentoringClassDetailResponse.from(mentoringClass, schedules);
    }

    public MentoringClassDetailResponse findOneClassFromRedis(Long classId) {
        String cacheKey = "mentoringClass::" + classId;

        try {
            // Redis에서 캐시된 데이터 조회
            String cachedData = redisTemplate.opsForValue().get(cacheKey);

            if (cachedData != null) {
                log.info("Redis 캐시 히트: 멘토링 클래스 ID {}", classId);
                return objectMapper.readValue(cachedData, MentoringClassDetailResponse.class);
            }
            log.info("Redis 캐시 미스: 멘토링 클래스 ID {}", classId);

            return findOneClassFromDb(classId);
        } catch (Exception e) {
            log.error("Redis 캐시 처리 중 오류 발생: {}", e.getMessage());
            return findOneClassFromDb(classId);
        }
    }

    private void warmUpPopularClasses() {
        try {
            List<Long> popularClassIds = mentoringClassRepository.findTopIdsByOrderByFavoriteCountDesc(
                    PageRequest.of(0, POPULAR_CLASS_LIMIT)
            );
            log.info("인기 클래스 ID 목록: {}", popularClassIds);

            for (Long classId : popularClassIds) {
                try {
                    MentoringClassDetailResponse response = findOneClassFromDb(classId);
                    String cacheKey = "mentoringClass::" + classId;

                    redisTemplate.opsForValue().set(
                            cacheKey,
                            objectMapper.writeValueAsString(response),
                            CACHE_TTL
                    );

                    log.info("멘토링 클래스 ID {} 캐시 저장 완료", classId);
                } catch (Exception e) {
                    log.error("멘토링 클래스 ID {} pre-warming 실패: {}", classId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("인기 클래스 pre-warming 실패", e);
            throw e;
        }
    }

    public MentoringClassDetailResponse findOneClassFromDb(Long classId) {
        MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByMentoringClassId(classId);

        return MentoringClassDetailResponse.from(mentoringClass, schedules);
    }

    @Transactional
    public void deleteClass(Long classId) throws IOException {
        MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByMentoringClassId(classId);
        scheduleRepository.deleteAll(schedules);
        mentoringClassRepository.delete(mentoringClass);
        // 오픈서치 인덱스 삭제
        elasticSearchService.deleteDocument(openSearchIndexName, classId);
    }

    @Transactional
    public MentoringClassDetailResponse updateClass(Long classId, Long memberId, MentoringClassUpdateRequest request) throws IOException {
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
        List<Schedule> scheduleList = scheduleRepository.findByMentoringClassId(classId);
        if (request.schedules() != null) {
            scheduleRepository.deleteAll(scheduleList);

            scheduleList = request.schedules().stream()
                    .map(scheduleReq -> Schedule.builder()
                            .mentoringClassId(classId)
                            .dayOfWeek(scheduleReq.dayOfWeek())
                            .time(scheduleReq.time())
                            .build())
                    .map(scheduleRepository::save)
                    .toList();
        }

        //오픈서치 업데이트 인덱싱
        MentoringClassDocument document = MentoringClassDocument.from(mentoringClass);
        elasticSearchService.saveDocument(openSearchIndexName, document.getId(), document);

        return MentoringClassDetailResponse.from(mentoringClass, scheduleList);
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

}
