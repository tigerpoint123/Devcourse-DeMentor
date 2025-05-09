package com.dementor.domain.favorite.event;

import com.dementor.domain.favorite.repository.FavoriteRepository;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.mentoringclass.exception.MentoringClassException;
import com.dementor.domain.mentoringclass.exception.MentoringClassExceptionCode;
import com.dementor.domain.mentoringclass.repository.MentoringClassRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteEventHandler {
    private final RedisTemplate<String, String> redisTemplate;
    private final MentoringClassRepository mentoringClassRepository;
    private final FavoriteRepository favoriteRepository;

    private static final String FAVORITE_COUNT_KEY = "favorite:count:";

    @PostConstruct
    @Transactional
    public void initializeFavoriteCounts() {
        log.info("=== 레디스 즐겨찾기 카운트 초기화 시작 ===");
        List<MentoringClass> mentoringClasses = mentoringClassRepository.findAll();

        for (MentoringClass mentoringClass : mentoringClasses) {
            String key = FAVORITE_COUNT_KEY + mentoringClass.getId();
            String count = String.valueOf(mentoringClass.getFavoriteCount());
            redisTemplate.opsForValue().set(key, count);
            log.info("초기화 - 클래스ID: {}, 즐겨찾기 개수: {}", mentoringClass.getId(), count);
        }
        log.info("=== 레디스 즐겨찾기 카운트 초기화 완료 ===");
    }

    @EventListener
    public void handleFavoriteAdded(FavoriteAddedEvent event) {
        String key = FAVORITE_COUNT_KEY + event.classId();
        Long newCount = redisTemplate.opsForValue().increment(key);
        log.info("즐겨찾기 추가 - 클래스ID: {}, 새로운 개수: {}", event.classId(), newCount);
    }

    @EventListener
    public void handleFavoriteRemoved(FavoriteRemovedEvent event) {
        String key = FAVORITE_COUNT_KEY + event.classId();
        Long newCount = redisTemplate.opsForValue().decrement(key);
        log.info("즐겨찾기 삭제 - 클래스ID: {}, 새로운 개수: {}", event.classId(), newCount);
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncFavoriteCounts() {
        log.info("=== DB 동기화 시작 ===");
        Set<String> keys = redisTemplate.keys(FAVORITE_COUNT_KEY + "*");
        if (keys != null) {
            for (String key : keys) {
                Long classId = Long.parseLong(key.substring(FAVORITE_COUNT_KEY.length()));
                String count = redisTemplate.opsForValue().get(key);
                if (count != null) {
                    MentoringClass mentoringClass = mentoringClassRepository.findById(classId)
                            .orElseThrow(() -> new MentoringClassException(MentoringClassExceptionCode.MENTORING_CLASS_NOT_FOUND));
                    mentoringClass.updateFavoriteCount(Integer.parseInt(count));
                    mentoringClassRepository.save(mentoringClass);
                    log.info("동기화 - 클래스ID: {}, DB 업데이트 개수: {}", classId, count);
                }
            }
        }
        log.info("=== DB 동기화 완료 ===");
    }
} 