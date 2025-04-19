package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.user.userCoupon.CouponStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserCouponJpaRepositoryTest {

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Test
    void existsByUserIdAndCouponId_쿠폰이_존재하는_경우_true를_반환한다() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime now = LocalDateTime.now();

        CouponEntity coupon = CouponEntity.createCoupon("생일기념 쿠폰", "FIXED_AMOUNT", 10, 1000, now);
        couponJpaRepository.save(coupon);

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();
        userCouponJpaRepository.save(userCoupon);

        // when
        boolean exists = userCouponJpaRepository.existsByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserIdAndCouponId_쿠폰이_존재하지_않는_경우_false를_반환한다() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        // when
        boolean exists = userCouponJpaRepository.existsByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(exists).isFalse();
    }

}