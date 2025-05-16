package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.infrastructure.redis.RedisTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponCacheRepository {

    private final RedisTemplateRepository redisTemplateRepository;

    private static final String COUPON_COUNTER_KEY = "coupon:counter:%s";
    private static final String COUPON_ISSUED_SET_KEY = "coupon:issued:%s";
    private static final String QUEUE_KEY = "coupon:queue:%d";

    public void initializeCoupon(Long couponId, Long quantity) {
        String counterKey = String.format(COUPON_COUNTER_KEY, couponId);
        redisTemplateRepository.setData(counterKey, quantity);
        log.info("쿠폰 초기화 완료 - CouponId: {}, Quantity: {}", couponId, quantity);
    }

    public Long issueCoupon(Long couponId, Long userId) {
        String counterKey = String.format(COUPON_COUNTER_KEY, couponId);
        String issuedSetKey = String.format(COUPON_ISSUED_SET_KEY, couponId);

        if (redisTemplateRepository.setAdd(issuedSetKey, String.valueOf(userId)) == 0)
            throw new RuntimeException("이미 발급한 쿠폰입니다.");

        Long remainCount = redisTemplateRepository.decrement(counterKey);
        if (remainCount < 0)
            throw new RuntimeException("쿠폰 재고가 부족합니다");
        return couponId;
    }

    public void enterQueue(Long couponId, Long userId) {
        String queueKey = String.format(QUEUE_KEY, couponId);
        redisTemplateRepository.addToListLeft(queueKey, userId);
    }

    public List<String> pullQueueCoupon(Long couponId, Long count) {
        String queueKey = String.format(QUEUE_KEY, couponId);
        return redisTemplateRepository.leftPopMultiple(queueKey, count, String.class);
    }
}