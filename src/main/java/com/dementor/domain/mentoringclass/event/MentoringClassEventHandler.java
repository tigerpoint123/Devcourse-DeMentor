package com.dementor.domain.mentoringclass.event;

import com.dementor.domain.favorite.event.FavoriteAddedEvent;
import com.dementor.domain.favorite.event.FavoriteRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MentoringClassEventHandler {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String POPULAR_CLASSES_CACHE_KEY = "popular:classes:";

    @EventListener
    public void handleFavoriteAdded(FavoriteAddedEvent event) {
        // 즐겨찾기 추가 시 관련 캐시 삭제
        String pattern = POPULAR_CLASSES_CACHE_KEY + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    @EventListener
    public void handleFavoriteRemoved(FavoriteRemovedEvent event) {
        // 즐겨찾기 삭제 시 관련 캐시 삭제
        String pattern = POPULAR_CLASSES_CACHE_KEY + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}