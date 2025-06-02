package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponFacadeServiceConcurrencyTest extends ApplicationContext {

    @BeforeEach
    public void setUp() {
        redisTemplateRepository.flushAll();
    }

    @Test
    void 잔여_10개쿠폰_5명이_동시에요청하기() throws InterruptedException {
        // given
        int threadCount = 5;
        int availableCoupons = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CouponInfo.CreateInfo getCreateInfo = couponService.save(new CouponCommand.Create("한정 10개 쿠폰", "FIXED_AMOUNT", availableCoupons, 1000L));

        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = (long) i;
            CouponCommand.Publish criteria = new CouponCommand.Publish(userId, getCreateInfo.couponId());

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(criteria);
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

        Thread.sleep(10000);

        // then
        CouponEntity updatedCoupon = couponRepository.findCouponById(getCreateInfo.couponId());
        long remainQuantity = updatedCoupon.getRemainQuantity();
        assertThat(remainQuantity).isEqualTo(availableCoupons - successCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);
    }

    @Test
    void 잔여_5개쿠폰_6명이_동시에요청하면_1명_실패() throws InterruptedException {
        // given
        int threadCount = 5;
        int availableCoupons = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        CouponInfo.CreateInfo getCreateInfo = couponService.save(new CouponCommand.Create("한정 5개 쿠폰", "FIXED_AMOUNT", availableCoupons, 1000L));

        // when
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = 100L + i;
            CouponCommand.Publish criteria = new CouponCommand.Publish(userId, getCreateInfo.couponId());

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();


        Thread.sleep(10000);


        // then
        CouponEntity updatedCoupon = couponRepository.findCouponById(getCreateInfo.couponId());
        List<UserCouponEntity> all = userCouponJpaRepository.findAll();
        assertThat(successCount.get()).isEqualTo(availableCoupons);
        assertThat(updatedCoupon.getRemainQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("잔여 5개 쿠폰을 5명이 동시에 요청 후, 추가 1명이 요청하면 실패")
    void 잔여_5개쿠폰_5명_동시요청_후_추가1명_요청시_실패() throws InterruptedException {
        // given
        int firstBatchCount = 5;
        int availableCoupons = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(firstBatchCount);
        CountDownLatch latch = new CountDownLatch(firstBatchCount);
        CouponInfo.CreateInfo getCreateInfo = couponService.save(new CouponCommand.Create("한정 5개 쿠폰", "FIXED_AMOUNT", availableCoupons, 1000L));

        // when - 첫 번째 배치: 5명이 동시에 요청
        AtomicInteger firstBatchSuccessCount = new AtomicInteger(0);

        for (int i = 0; i < firstBatchCount; i++) {
            Long userId = 200L + i;
            CouponCommand.Publish criteria = new CouponCommand.Publish(userId, getCreateInfo.couponId());

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(criteria);
                    firstBatchSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Thread.sleep(10000);

        // then - 첫 번째 배치 검증
        assertThat(firstBatchSuccessCount.get()).isEqualTo(availableCoupons); // 5명 모두 성공

        // when - 두 번째 요청: 추가 1명이 요청
        Long additionalUserId = 206L;
        CouponCommand.Publish additionalCriteria =
                new CouponCommand.Publish(additionalUserId, getCreateInfo.couponId());

        // then
        assertThatThrownBy(() -> couponService.publishCoupon(additionalCriteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("쿠폰 재고가 부족합니다");

        // 최종 쿠폰 수량 확인
        CouponEntity updatedCoupon = couponRepository.findCouponById(getCreateInfo.couponId());
        assertThat(updatedCoupon.getRemainQuantity()).isEqualTo(0);
    }

    @Test
    void 잔여_0개쿠폰_여러명_동시요청_모두_실패() throws InterruptedException {
        // given
        int threadCount = 5;
        int availableCoupons = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CouponInfo.CreateInfo getCreateInfo = couponService.save(new CouponCommand.Create("0개 쿠폰", "FIXED_AMOUNT", availableCoupons, 1000L));
        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = 300L + i;
            CouponCommand.Publish criteria = new CouponCommand.Publish(userId, getCreateInfo.couponId());

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Thread.sleep(10000);

        // then
        assertThat(successCount.get()).isEqualTo(0);
        assertThat(failureCount.get()).isEqualTo(threadCount);

        CouponEntity updatedCoupon = couponRepository.findCouponById(getCreateInfo.couponId());
        assertThat(updatedCoupon.getRemainQuantity()).isEqualTo(0);
    }
}