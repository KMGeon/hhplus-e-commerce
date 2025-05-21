package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.domain.vo.RankingItem;
import kr.hhplus.be.server.domain.vo.RankingPeriod;
import kr.hhplus.be.server.infrastructure.redis.RedisTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.support.CacheKeyManager.CacheKeyName.DAILY_SUMMERY_PRODUCT;

@Repository
@RequiredArgsConstructor
public class ProductCacheRepository {

    private final RedisTemplateRepository redisRepository;


    public Ranking findDailyRanking(String targetPath) {
        List<RankingItem> topNFromSortedSet = redisRepository.getSortedSetWithScores(
                String.format(DAILY_SUMMERY_PRODUCT, targetPath),
                RankingItem.class
        );

        if (topNFromSortedSet == null || topNFromSortedSet.isEmpty())
            return Ranking.empty(RankingPeriod.DAILY, targetPath);

        return Ranking.create(RankingPeriod.DAILY, targetPath, topNFromSortedSet);
    }
}