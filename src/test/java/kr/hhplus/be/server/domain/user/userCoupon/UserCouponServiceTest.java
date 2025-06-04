package kr.hhplus.be.server.domain.user.userCoupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    private static final Long USER_ID = 1L;
    private static final Long COUPON_ID = 100L;
    private static final Long USER_COUPON_ID = 1000L;
    private static final Long ORDER_ID = 10000L;

    @Test
    void 유효한_쿠폰검증_쿠폰_ID를_반환한다() {
        // given
        UserCouponEntity validCoupon = UserCouponEntity.publishCoupon(USER_ID, COUPON_ID);
        when(userCouponRepository.findById(USER_COUPON_ID)).thenReturn(validCoupon);

        // when
        Long result = userCouponService.checkUserCoupon(USER_COUPON_ID, USER_ID);

        // then
        assertThat(result).isEqualTo(COUPON_ID);
        verify(userCouponRepository, times(1)).findById(USER_COUPON_ID);
    }

    @Test
    void 유효하지_않은_사용자가_쿠폰_검증_예외가_발생() {
        // given
        UserCouponEntity validCoupon = UserCouponEntity.publishCoupon(USER_ID, COUPON_ID);
        when(userCouponRepository.findById(USER_COUPON_ID)).thenReturn(validCoupon);

        Long invalidUserId = 999L; // 다른 사용자 ID

        // when
        // then
        assertThatThrownBy(() -> userCouponService.checkUserCoupon(USER_COUPON_ID, invalidUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 정보가 일치하지 않습니다");

        verify(userCouponRepository, times(1)).findById(USER_COUPON_ID);
    }

    @Test
    void 쿠폰_사용_처리가_정상적으로_이루어진다() {
        // given
        UserCouponEntity coupon = mock(UserCouponEntity.class);
        when(userCouponRepository.findById(USER_COUPON_ID)).thenReturn(coupon);

        // when
        userCouponService.useCoupon(USER_COUPON_ID, ORDER_ID);

        // then
        verify(userCouponRepository, times(1)).findById(USER_COUPON_ID);
        verify(coupon, times(1)).use(ORDER_ID);
    }
}