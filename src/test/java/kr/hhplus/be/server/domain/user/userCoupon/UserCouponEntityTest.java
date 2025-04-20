package kr.hhplus.be.server.domain.user.userCoupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserCouponEntityTest {


    @Test
    void 쿠폰을_발행을_처리한다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;

        // when
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);

        // then
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(userCoupon.getCouponStatus()).isEqualTo(CouponStatus.AVAILABLE);
        assertThat(userCoupon.getOrderId()).isNull();
    }

    @Test
    void 쿠폰이_사용_가능한_상태인지_확인을_해야된다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);

        // when
        // then
        userCoupon.checkThisCouponCanUse(userId);
    }

    @Test
    void 사용자_정보가_일치하지_않으면_쿠폰을_사용할_수_없다() {
        // given
        final long userId = 1L;
        final long wrongUserId = 2L;
        final long couponId = 100L;
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);

        // when
// then
        assertThatThrownBy(() -> userCoupon.checkThisCouponCanUse(wrongUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 정보가 일치하지 않습니다");
    }

    @Test
    void 이미_사용된_쿠폰은_사용할_수_없다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;
        final long orderId = 1000L;
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);
        userCoupon.use(orderId);

        // when
// then
        assertThatThrownBy(() -> userCoupon.checkThisCouponCanUse(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다");
    }

    @Test
    void 쿠폰상태가_AVAILABLE이_아니면_사용할_수_없다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;
        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.EXPIRED) // 만료된 상태로 설정
                .build();

        // when
// then
        assertThatThrownBy(() -> userCoupon.checkThisCouponCanUse(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 가능한 상태가 아닙니다");
    }

    @Test
    void 쿠폰_사용_여부를_확인할_수_있다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;
        final long orderId = 1000L;
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);

        // when
// then
        assertThat(userCoupon.isUsed()).isFalse();

        // when
        userCoupon.use(orderId);

        // then
        assertThat(userCoupon.isUsed()).isTrue();
    }

    @Test
    void 쿠폰을_사용_처리할_수_있다() {
        // given
        final long userId = 1L;
        final long couponId = 100L;
        final long orderId = 1000L;
        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);

        // when
        userCoupon.use(orderId);

        // then
        assertThat(userCoupon.getOrderId()).isEqualTo(orderId);
        assertThat(userCoupon.getCouponStatus()).isEqualTo(CouponStatus.USED);
    }
}