package kr.hhplus.be.server.infrastructure.redis;

import com.google.gson.Gson;
import kr.hhplus.be.server.domain.vo.RankingItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisTemplateRepository {

    private final RedisTemplate<String, String> template;
    private final Gson gson;

    @Value("${spring.data.redis.default-expiration}")
    private Duration defaultExpireTime;

    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }
        return gson.fromJson(jsonValue, clazz);
    }

    public <T> void setData(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForValue().set(key, jsonValue);
    }

    public Long decrement(String key) {
        return template.opsForValue().decrement(key);
    }


    public <T> void addToListLeft(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().leftPush(key, jsonValue);
    }

    public <T> List<T> leftPopMultiple(String key, Long count, Class<T> clazz) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(clazz, "Class cannot be null");

        if (count == null || count <= 0)
            return Collections.emptyList();

        return Optional.of(template.executePipelined((RedisCallback<String>) connection -> {
                    IntStream.range(0, count.intValue())
                            .forEach(i -> connection.listCommands().lPop(key.getBytes()));
                    return null;
                }))
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(result -> convertAndDeserialize(result, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <T> T convertAndDeserialize(Object result, Class<T> clazz) {
        try {
            String jsonValue = result instanceof byte[]
                    ? new String((byte[]) result, StandardCharsets.UTF_8)
                    : result.toString();
            return gson.fromJson(jsonValue, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Long setAdd(String key, String value) {
        return template.opsForSet().add(key, value);
    }

    public void flushAll() {
        template.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushAll();
            return "OK";
        });
    }

    public <T> void addToSortedSet(String key, T value, Long score, Duration timeout) {
        String jsonValue = gson.toJson(value);
        template.opsForZSet().incrementScore(key, jsonValue, score);
        template.expire(key, timeout == null ? defaultExpireTime : timeout);
    }


    public <T> List<T> getSortedSetWithScores(String key, Class<T> clazz) {
        final int limit = 10;

        Set<ZSetOperations.TypedTuple<String>> tuples = template.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        List<T> resultList = new ArrayList<>();

        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                try {
                    T value = gson.fromJson(tuple.getValue(), clazz);

                    if (value instanceof RankingItem) {
                        ((RankingItem) value).setScore(tuple.getScore().longValue());
                    }

                    resultList.add(value);
                } catch (Exception e) {
                    throw new RuntimeException("JSON 역직렬화 오류", e);
                }
            }
        }

        return resultList;
    }
}