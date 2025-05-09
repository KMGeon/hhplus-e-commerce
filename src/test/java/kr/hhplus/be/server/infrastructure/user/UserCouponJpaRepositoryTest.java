package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.user.userCoupon.CouponStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponJpaRepositoryTest extends ApplicationContext {

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    private Long userId;
    private Long couponId;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        userCouponJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();

        userId = 1L;
        couponId = 1L;
    }

    @Test
    @DisplayName("유저와 쿠폰 ID로 유저-쿠폰 조회 - 존재하는 경우")
    void findByUserIdAndCouponId_ReturnsUserCoupon_WhenExists() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CouponEntity coupon = CouponEntity.createCoupon("생일기념 쿠폰", "FIXED_AMOUNT", 10, 1000, now);
        couponJpaRepository.save(coupon);

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .userId(userId)
                .couponId(coupon.getId())
                .couponStatus(CouponStatus.AVAILABLE)
                .build();
        userCouponJpaRepository.save(userCoupon);

        // when
        UserCouponEntity foundUserCoupon = userCouponJpaRepository.findByUserIdAndCouponId(userId, coupon.getId());

        // then
        assertThat(foundUserCoupon).isNotNull();
        assertThat(foundUserCoupon.getUserId()).isEqualTo(userId);
        assertThat(foundUserCoupon.getCouponId()).isEqualTo(coupon.getId());
        assertThat(foundUserCoupon.getCouponStatus()).isEqualTo(CouponStatus.AVAILABLE);
    }

}