package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.vo.RankingItem;
import kr.hhplus.be.server.infrastructure.redis.RedisTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static kr.hhplus.be.server.support.CacheKeyManager.CacheKeyName.DAILY_SUMMERY_PRODUCT;

@Repository
@RequiredArgsConstructor
public class OrderCacheRepository {

    private final RedisTemplateRepository redisTemplateRepository;

    public void addDailySummeryRanking(String key, RankingItem value, Long score) {
        redisTemplateRepository.addToSortedSet(String.format(DAILY_SUMMERY_PRODUCT, key), value, score, Duration.ofDays(10));
    }
}
