package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponFacadeService {
    private final CouponService couponService;
    private final UserService userService;
    private final UserCouponService userCouponService;

    @Transactional
    public long publishCoupon(CouponCriteria.PublishCriteria criteria) {
        // 1. 사용자 검증
        long userId = userService.validateUserForCoupon(criteria.userId(), criteria.couponId());

        // 2. 쿠폰 발행 가능 여부 검증 및 수량 감소
        couponService.validateAndDecreaseCoupon(criteria.couponId());

        // 3. 사용자 쿠폰 발행
        userCouponService.save(userId, criteria.couponId());

        return userId;
    }
}
