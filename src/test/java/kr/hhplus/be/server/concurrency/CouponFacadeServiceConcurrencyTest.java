package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CouponFacadeServiceConcurrencyTest extends ApplicationContext {

    @Test
    void 잔여_10개쿠폰_5명이_동시에요청하기() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        CouponEntity initialCoupon = couponJpaRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다."));
        long initialQuantity = initialCoupon.getRemainQuantity();
        assertThat(initialQuantity).isGreaterThanOrEqualTo(1);

        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = (long) i;
            CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, 1L);

            executorService.submit(() -> {
                try {
                    couponFacadeService.publishCouponLock(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        CouponEntity updatedCoupon = couponRepository.findCouponById(1L);
        long remainQuantity = updatedCoupon.getRemainQuantity();
        assertThat(remainQuantity).isEqualTo(initialQuantity - successCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        if (initialQuantity < threadCount) {
            assertThat(failureCount.get()).isGreaterThan(0);
        }
    }
}