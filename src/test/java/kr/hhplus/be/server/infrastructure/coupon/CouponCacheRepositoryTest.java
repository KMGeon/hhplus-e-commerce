package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.config.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

class CouponCacheRepositoryTest extends ApplicationContext {

    @BeforeEach
    public void setUp(){
        redisTemplateRepository.flushAll();
    }

    @Test
    void 쿠폰_생성_계수기_테스트() {
        // given
        Long couponId = 1L;
        Long quantity = 100L;
        String counterKey = "coupon:counter:1";

        // when
        couponCacheRepository.initializeCoupon(couponId, quantity);
        String data = redisTemplate.opsForValue().get(counterKey);

        // then
        assertNotNull(data);
        assertEquals(quantity, Long.parseLong(data));
    }

    @Test
    void 쿠폰_발급_성공_테스트() {
        // given
        Long couponId = 2L;
        Long userId = 100L;
        Long quantity = 10L;

        couponCacheRepository.initializeCoupon(couponId, quantity);

        // when
        Long issuedCouponId = couponCacheRepository.issueCoupon(couponId, userId);

        // then
        assertEquals(couponId, issuedCouponId);

        // 남은 수량 확인
        String counterKey = String.format("coupon:counter:%s", couponId);
        String remainCount = redisTemplate.opsForValue().get(counterKey);
        assertEquals(9L, Long.parseLong(remainCount));
    }

    @Test
    void 쿠폰_중복_발급_예외_테스트() {
        // given
        Long couponId = 3L;
        Long userId = 100L;
        Long quantity = 10L;

        couponCacheRepository.initializeCoupon(couponId, quantity);
        couponCacheRepository.issueCoupon(couponId, userId);

        // when & then

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponCacheRepository.issueCoupon(couponId, userId));
        assertEquals("이미 발급한 쿠폰입니다.", exception.getMessage());
    }

    @Test
    void 쿠폰_재고_부족_예외_테스트() {
        // given
        Long couponId = 4L;
        Long quantity = 1L;

        couponCacheRepository.initializeCoupon(couponId, quantity);
        couponCacheRepository.issueCoupon(couponId, 100L);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponCacheRepository.issueCoupon(couponId, 200L));
        assertEquals("쿠폰 재고가 부족합니다", exception.getMessage());
    }

    @Test
    void 대기열_입장_테스트() {
        // given
        Long couponId = 5L;
        Long userId = 100L;

        // when
        couponCacheRepository.enterQueue(couponId, userId);

        // then
        String queueKey = String.format("coupon:queue:%d", couponId);
        Long queueSize = redisTemplate.opsForList().size(queueKey);
        assertEquals(1L, queueSize);
    }

    @Test
    void 대기열_쿠폰_가져오기_테스트() {
        // given
        Long couponId = 6L;
        int userCount = 5;

        // 대기열에 유저 추가
        for (int i = 0; i < userCount; i++) {
            couponCacheRepository.enterQueue(couponId, (long) i);
        }

        // when
        List<String> pulledUsers = couponCacheRepository.pullQueueCoupon(couponId, 3L);

        // then
        assertEquals(3, pulledUsers.size());
        assertEquals("4", pulledUsers.get(0)); // LIFO 방식
        assertEquals("3", pulledUsers.get(1));
        assertEquals("2", pulledUsers.get(2));

        // 남은 큐 사이즈 확인
        String queueKey = String.format("coupon:queue:%d", couponId);
        Long remainingSize = redisTemplate.opsForList().size(queueKey);
        assertEquals(2L, remainingSize);
    }

    @Test
    @DisplayName("빈 대기열에서 가져오기 시도")
    void 빈_대기열_테스트() {
        // given
        Long couponId = 7L;

        // when
        List<String> result = couponCacheRepository.pullQueueCoupon(couponId, 5L);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("""
            해당 initializeCoupon 메서드에서 동시성은 허용한다.
            이후 대기열에서 쿠폰을 가져오는 과정에서 동시성을 우회하여 문제를 해결한다.
            """)
    void 동시다발적_쿠폰_발급_테스트() throws InterruptedException {
        // given
        Long couponId = 8L;
        Long quantity = 50L;
        int threadCount = 100;

        couponCacheRepository.initializeCoupon(couponId, quantity);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    couponCacheRepository.issueCoupon(couponId, (long) i);
                } catch (Exception e) {
                    // 예외 발생은 정상 (재고 부족 또는 중복)
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();
        executorService.shutdown();

        // then
        String counterKey = String.format("coupon:counter:%s", couponId);
        String remainCount = redisTemplate.opsForValue().get(counterKey);
        assertNotEquals(0L, Long.parseLong(remainCount));

        String issuedSetKey = String.format("coupon:issued:%s", couponId);
        Long issuedCount = redisTemplate.opsForSet().size(issuedSetKey);
        assertNotEquals(quantity, issuedCount);
    }

    @Test
    @DisplayName("초기화 → 대기열 → 발급")
    void 전체_플로우_테스트() {
        // given
        Long couponId = 9L;
        Long quantity = 5L;

        // 1. 쿠폰 초기화
        couponCacheRepository.initializeCoupon(couponId, quantity);

        // 2. 대기열에 10명 입장
        for (int i = 0; i < 10; i++) {
            couponCacheRepository.enterQueue(couponId, (long) i);
        }

        // 3. 대기열에서 5명만 가져오기
        List<String> selectedUsers = couponCacheRepository.pullQueueCoupon(couponId, 5L);
        assertEquals(5, selectedUsers.size());

        // 4. 선택된 사용자들에게 쿠폰 발급
        for (String userIdStr : selectedUsers) {
            Long userId = Long.parseLong(userIdStr);
            Long issuedCouponId = couponCacheRepository.issueCoupon(couponId, userId);
            assertEquals(couponId, issuedCouponId);
        }

        // 5. 더 이상 발급 불가 확인
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> couponCacheRepository.issueCoupon(couponId, 999L));
        assertEquals("쿠폰 재고가 부족합니다", exception.getMessage());
    }
}