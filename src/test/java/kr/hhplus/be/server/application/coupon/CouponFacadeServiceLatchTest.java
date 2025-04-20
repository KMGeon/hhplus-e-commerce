package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CouponFacadeServiceLatchTest {

    @Autowired
    private CouponFacadeService couponFacadeService;

    @Autowired
    private CouponJpaRepository couponRepository;

    private Long couponId = 1L;
    private List<Long> userIds = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        userIds.clear();
        for (int i = 1; i <= 30; i++) {
            userIds.add((long) i);
        }
    }

    @Test
    void 잔여_10개쿠폰_5명이_동시에요청하기() throws InterruptedException {
        // given
        int threadCount = 5;

        CouponEntity initialCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalStateException("테스트용 쿠폰이 존재하지 않습니다. ID: " + couponId));
        long initialQuantity = initialCoupon.getRemainQuantity();

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = userIds.get(i);
            CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);

            executorService.submit(() -> {
                try {
                    startLatch.await();
                    try {
                        couponFacadeService.publishCoupon(criteria);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        endLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        CouponEntity updatedCoupon = couponRepository.findById(couponId).orElseThrow();

        assertThat(updatedCoupon.getRemainQuantity()).isNotEqualTo(initialQuantity - successCount.get());
    }
}