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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponFacadeServiceTest {

    @Mock
    private CouponService couponService;

    @Mock
    private UserService userService;

    @Mock
    private UserCouponService userCouponService;

    @InjectMocks
    private CouponFacadeService couponFacadeService;

    @Test
    void 쿠폰_파사드_호출_테스트() {
        // given
        long userId = 1L;
        long couponId = 1L;
        CouponCriteria.PublishCriteria criteria = new CouponCriteria.PublishCriteria(userId, couponId);
        
        when(userService.validateUserForCoupon(userId, couponId)).thenReturn(userId);

        // when
        couponFacadeService.publishCoupon(criteria);

        // then
        InOrder inOrder = inOrder(userService, couponService, userCouponService);
        
        inOrder.verify(userService).validateUserForCoupon(userId, couponId);
        inOrder.verify(couponService).validateAndDecreaseCoupon(couponId);
        inOrder.verify(userCouponService).save(userId, couponId);
    }
} 