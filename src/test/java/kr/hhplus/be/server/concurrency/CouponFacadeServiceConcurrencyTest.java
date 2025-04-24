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

    @Autowired
    private CouponFacadeService couponFacadeService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;


    private Long couponId = 1L;

    @BeforeEach
    public void setUp() {
        CouponEntity coupon = CouponEntity.createCoupon("생일기념 쿠폰", "FIXED_AMOUNT", 10, 1000, LocalDateTime.now());
        couponJpaRepository.save(coupon);

        UserEntity newUser1 = UserEntity.createNewUser();
        UserEntity newUser2 = UserEntity.createNewUser();
        UserEntity newUser3 = UserEntity.createNewUser();
        UserEntity newUser4 = UserEntity.createNewUser();
        UserEntity newUser5 = UserEntity.createNewUser();
        UserEntity newUser6 = UserEntity.createNewUser();
        UserEntity newUser7 = UserEntity.createNewUser();
        UserEntity newUser8 = UserEntity.createNewUser();
        UserEntity newUser9 = UserEntity.createNewUser();
        UserEntity newUser10 = UserEntity.createNewUser();

        List<UserEntity> newUser11 = List.of(newUser1, newUser2, newUser3, newUser4, newUser5, newUser6, newUser7, newUser8, newUser9, newUser10);
        userJpaRepository.saveAll(newUser11);

    }

    @Test
    void 잔여_10개쿠폰_5명이_동시에요청하기() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        CouponEntity initialCoupon = couponJpaRepository.findById(couponId)
                .orElseThrow(()-> new RuntimeException("쿠폰을 찾을 수 없습니다."));
        long initialQuantity = initialCoupon.getRemainQuantity();

        assertThat(initialQuantity).isGreaterThanOrEqualTo(1);

        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Long userId = (long) i;
            CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);

            executorService.submit(() -> {
                try {
                    couponFacadeService.publishCouponPessimistic(criteria);
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
        CouponEntity updatedCoupon = couponRepository.findCouponById(couponId);
        long remainQuantity = updatedCoupon.getRemainQuantity();
        assertThat(remainQuantity).isEqualTo(initialQuantity - successCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        if (initialQuantity < threadCount) {
            assertThat(failureCount.get()).isGreaterThan(0);
        }
    }
}