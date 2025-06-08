package kr.hhplus.be.server.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static kr.hhplus.be.server.support.CacheKeyManager.CacheKeyName.HOT_PRODUCT_CACHE_NAME;
import static kr.hhplus.be.server.support.CacheKeyManager.CacheKeyName.PRODUCT_CACHE_NAME;


@Slf4j
@Component
@RequiredArgsConstructor
public class CacheKeyManager {
    private final ObjectMapper objectMapper;

    public String generateKey(Object... params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return String.valueOf(java.util.Arrays.hashCode(params));
        }
    }

    @Getter
    public enum CacheType {
        PRODUCT(PRODUCT_CACHE_NAME, Duration.ofDays(1)),
        HOT_PRODUCT(HOT_PRODUCT_CACHE_NAME, Duration.ofDays(3)),
        HOT_PRODUCT_QUERYDSL(CacheKeyName.HOT_PRODUCT_QUERYDSL, Duration.ofDays(7));

        private final String cacheName;
        private final Duration ttl;

        CacheType(String cacheName, Duration ttl) {
            this.cacheName = cacheName;
            this.ttl = ttl;
        }

    }

    public static class CacheKeyName{
        public static final String HOT_PRODUCT_CACHE_NAME = "hot_product";
        public static final String HOT_PRODUCT_QUERYDSL = "hot_product::three:%s";
        public static final String PRODUCT_CACHE_NAME = "product";
        public static final String DAILY_SUMMERY_PRODUCT = "product::daily:%s";
    }

    public static class RedisLockKey {
        public static final String DECREASE_STOCK_ORDER_LOCK = "#stockCommand.generateLockKey()";
        public static final String DECREASE_COUPON_LOCK = "#couponId.toString()";
    }
}