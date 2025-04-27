package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponFacadeServiceTest {

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponFacadeService couponFacadeService;

    @Test
    void 쿠폰_발행_성공() {
        Long userId = 1L;
        Long couponId = 100L;
        CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);

        when(userCouponService.publishOnlyIfFirstTime(criteria)).thenReturn(userId);

        long result = couponFacadeService.publishCoupon(criteria);

        assertThat(result).isEqualTo(userId);

        InOrder inOrder = inOrder(couponService, userCouponService);
        inOrder.verify(couponService).decreaseCouponQuantityAfterCheck(couponId);
        inOrder.verify(userCouponService).publishOnlyIfFirstTime(criteria);
    }

    @Test
    void 쿠폰_재고_부족_시_예외_발생() {
        Long userId = 1L;
        Long couponId = 100L;
        CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);

        doThrow(new RuntimeException("쿠폰이 모두 소진되었습니다."))
                .when(couponService).decreaseCouponQuantityAfterCheck(couponId);

        assertThatThrownBy(() -> couponFacadeService.publishCoupon(criteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("쿠폰이 모두 소진되었습니다");

        verify(couponService).decreaseCouponQuantityAfterCheck(couponId);
        verify(userCouponService, never()).publishOnlyIfFirstTime(any());
    }

    @Test
    void 이미_발행된_쿠폰_예외_발생() {
        Long userId = 1L;
        Long couponId = 100L;
        CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);

        doThrow(new RuntimeException("이미 발행된 쿠폰입니다"))
                .when(userCouponService).publishOnlyIfFirstTime(criteria);

        assertThatThrownBy(() -> couponFacadeService.publishCoupon(criteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 발행된 쿠폰입니다");

        InOrder inOrder = inOrder(couponService, userCouponService);
        inOrder.verify(couponService).decreaseCouponQuantityAfterCheck(couponId);
        inOrder.verify(userCouponService).publishOnlyIfFirstTime(criteria);
    }
}