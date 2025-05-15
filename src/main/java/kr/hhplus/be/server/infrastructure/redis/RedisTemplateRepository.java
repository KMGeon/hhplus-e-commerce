package kr.hhplus.be.server.infrastructure.redis;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
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

    public Long delete(Collection<String> keys) {
        return template.delete(keys);
    }

    public Boolean expire(String key, Duration timeout) {
        return template.expire(key, timeout);
    }


    public void flushAll() {
        template.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushAll();
            return "OK";
        });
    }

    public <T> void addToSortedSet(String key, T value, Long score) {
        String jsonValue = gson.toJson(value);
        template.opsForZSet().add(key, jsonValue, score);
    }

    public <T> Set<T> rangeByScore(String key, Float minScore, Float maxScore, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().rangeByScore(key, minScore, maxScore);
        Set<T> resultSet = new HashSet<T>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    public <T> List<T> getTopNFromSortedSet(String key, int n, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().reverseRange(key, 0, n - 1);
        List<T> resultList = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                try {
                    T value = gson.fromJson(jsonValue, clazz);
                    resultList.add(value);
                } catch (Exception e) {
                    throw new RuntimeException("JSON 역직렬화 오류", e);
                }
            }
        }

        return resultList;
    }

    public <T> List<T> getAllFromSortedSet(String key, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().reverseRange(key, 0, -1);
        List<T> resultList = new ArrayList<>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                try {
                    T value = gson.fromJson(jsonValue, clazz);
                    resultList.add(value);
                } catch (Exception e) {
                    throw new RuntimeException("JSON 역직렬화 오류", e);
                }
            }
        }

        return resultList;
    }

}