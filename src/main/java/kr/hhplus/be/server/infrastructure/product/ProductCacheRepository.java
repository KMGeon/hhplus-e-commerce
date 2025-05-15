package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.infrastructure.redis.RedisTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCacheRepository {

    private final RedisTemplateRepository redisRepository;
    private static final String HOT_PRODUCT_CACHE_NAME = "hot_product::[]";
    private static final Duration HOT_PRODUCT_CACHE_EXPIRE_TIME = Duration.ofDays(3);
    private static final int HOT_PRODUCT_CACHE_LIMIT = 5;

    public List<HotProductQuery> findHotProductsCacheLimit5(){
        return redisRepository.getTopNFromSortedSet(HOT_PRODUCT_CACHE_NAME, HOT_PRODUCT_CACHE_LIMIT, HotProductQuery.class);
    }

    public void setHotProductsCacheLimit5(List<HotProductQuery> hotProductsCache) {
        redisRepository.delete(List.of(HOT_PRODUCT_CACHE_NAME));
        for (int i = 0; i < hotProductsCache.size(); i++) {
            long score = (long) hotProductsCache.size() - i;
            redisRepository.addToSortedSet(HOT_PRODUCT_CACHE_NAME, hotProductsCache.get(i), score);
        }
        redisRepository.expire(HOT_PRODUCT_CACHE_NAME, HOT_PRODUCT_CACHE_EXPIRE_TIME);
    }

    public void deleteHotProductsCache() {
        redisRepository.delete(List.of(HOT_PRODUCT_CACHE_NAME));
    }
}