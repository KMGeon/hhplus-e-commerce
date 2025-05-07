package kr.hhplus.be.server.domain.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

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
        PRODUCT("product", Duration.ofDays(1)),
        HOT_PRODUCT("hot_product", Duration.ofDays(3));

        private final String cacheName;
        private final Duration ttl;

        CacheType(String cacheName, Duration ttl) {
            this.cacheName = cacheName;
            this.ttl = ttl;
        }

    }
}