package kr.hhplus.be.server.domain.user.userCoupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCouponEntityTest {

    @Test
    @DisplayName("쿠폰 사용 성공 테스트")
    void use_success() {
        // given
        Long userId = 100L;
        Long couponId = 200L;
        Long orderId = 300L;

        UserCouponEntity userCoupon = UserCouponEntity.publishCoupon(userId, couponId);
        assertEquals(CouponStatus.AVAILABLE, userCoupon.getCouponStatus());
        assertFalse(userCoupon.isUsed());

        // when
        userCoupon.use(orderId);

        // then
        assertEquals(CouponStatus.USED, userCoupon.getCouponStatus());
        assertTrue(userCoupon.isUsed());
        assertEquals(orderId, userCoupon.getOrderId());
    }
}