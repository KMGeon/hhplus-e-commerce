package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponFacadeService {

    private final UserCouponService userCouponService;
    private final CouponService couponService;


    @Transactional
    public long publishCouponLock(CouponCriteria.PublishCriteria criteria) {
        couponService.decreaseCouponQuantityAfterCheckLock(criteria.couponId());
        return userCouponService.publishOnlyIfFirstTime(criteria);
    }
}
