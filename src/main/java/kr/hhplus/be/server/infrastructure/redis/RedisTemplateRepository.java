package kr.hhplus.be.server.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisTemplateRepository {
    private final RedisTemplate<String, String> template;
    private final ObjectMapper objectMapper; // Gson 대신 ObjectMapper 사용

    @Value("${spring.data.redis.default-expiration}")
    private Duration defaultExpireTime;
    public Boolean delete(String key) {
        return template.delete(key);
    }

    public Boolean expire(String key, Duration timeout) {
        return template.expire(key, timeout);
    }

    public <T> void addToSortedSet(String key, T value, Float score) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            template.opsForZSet().add(key, jsonValue, score);
            template.expire(key, defaultExpireTime);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 오류", e);
        }
    }

    public <T> List<T> getTopNFromSortedSet(String key, int n, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().reverseRange(key, 0, n - 1);
        List<T> resultSet = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                try {
                    T v = objectMapper.readValue(jsonValue, clazz);
                    resultSet.add(v);
                } catch (Exception e) {
                    throw new RuntimeException("JSON 역직렬화 오류", e);
                }
            }
        }

        return resultSet;
    }

    public void setBit(String key, long offset, boolean value) {
        template.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, long offset) {
        return Boolean.TRUE.equals(template.opsForValue().getBit(key, offset));
    }
}