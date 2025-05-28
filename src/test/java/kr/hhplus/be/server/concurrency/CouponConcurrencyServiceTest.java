package kr.hhplus.be.server.concurrency;


import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CouponConcurrencyServiceTest extends ApplicationContext {

    private static final int THREAD_COUNT = 5;
    private static final int LARGE_THREAD_COUNT = 10;


    @Test
    public void 한정_5명_제한_쿠폰_동시에_5명_성공적으로_요청() throws Exception {
        redisTemplateRepository.flushAll();

        userJpaRepository.saveAll(List.of(
                UserEntity.createNewUser(),
                UserEntity.createNewUser(),
                UserEntity.createNewUser(),
                UserEntity.createNewUser(),
                UserEntity.createNewUser()
        ));

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        CouponInfo.CreateInfo getCreateInfo = couponService.save(new CouponCommand.Create("한정 5개 쿠폰", "FIXED_AMOUNT", 5, 1000L));

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);


        for (int i = 1; i <= THREAD_COUNT; i++) {
            final Long orderId = (long) i;

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(new CouponCommand.Publish(orderId, getCreateInfo.couponId()));
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


        Thread.sleep(10000L);

        List<UserCouponEntity> rtn = userCouponJpaRepository.findAll();

        assertEquals(rtn.size(),THREAD_COUNT,"");
        assertEquals(THREAD_COUNT, successCount.get(), "성공 카운트 검증");
    }


    @Test
    public void 한정_5개_쿠폰에_10명이_동시에_요청하면_5명만_성공() throws Exception {
        redisTemplateRepository.flushAll();

        // 10명의 사용자 생성
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < LARGE_THREAD_COUNT; i++) {
            users.add(UserEntity.createNewUser());
        }
        userJpaRepository.saveAll(users);

        ExecutorService executorService = Executors.newFixedThreadPool(LARGE_THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(LARGE_THREAD_COUNT);

        // 5개 한정 쿠폰 생성
        CouponInfo.CreateInfo getCreateInfo = couponService.save(
                new CouponCommand.Create("한정 5개 쿠폰", "FIXED_AMOUNT", 5, 1000L)
        );

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= LARGE_THREAD_COUNT; i++) {
            final Long userId = (long) i;

            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(
                            new CouponCommand.Publish(userId, getCreateInfo.couponId())
                    );
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    // 예외 로그는 예상된 것이므로 출력하지 않음
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Thread.sleep(10000L);
        List<UserCouponEntity> rtn = userCouponJpaRepository.findAll();

        assertEquals(5, rtn.size(), "발급된 쿠폰은 5개여야 함");
        assertEquals(5, successCount.get(), "성공 카운트는 5여야 함");
        assertEquals(5, failureCount.get(), "실패 카운트는 5여야 함");
    }

    @Test
    public void 동일_사용자가_중복_요청시_1개만_발급() throws Exception {
        redisTemplateRepository.flushAll();

        UserEntity user = userJpaRepository.save(UserEntity.createNewUser());
        Long userId = user.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        CouponInfo.CreateInfo getCreateInfo = couponService.save(
                new CouponCommand.Create("중복 방지 쿠폰", "FIXED_AMOUNT", 10, 1000L)
        );

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 같은 사용자가 5번 동시 요청
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                try {
                    couponService.publishCoupon(
                            new CouponCommand.Publish(userId, getCreateInfo.couponId())
                    );
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

        Thread.sleep(10000L);
        List<UserCouponEntity> userCoupons = userCouponJpaRepository.findAll();

        assertEquals(1, userCoupons.size(), "같은 사용자는 1개만 발급받아야 함");
        assertEquals(1, successCount.get(), "성공은 1번만");
        assertEquals(4, failureCount.get(), "나머지 4번은 실패");
    }
}
