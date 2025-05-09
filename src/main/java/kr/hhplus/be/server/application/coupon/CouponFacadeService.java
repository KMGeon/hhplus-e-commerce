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

    private final UserCouponService userCouponService;
    private final CouponService couponService;

    @Transactional
    public long publishCoupon(CouponCriteria.PublishCriteria criteria) {
        couponService.decreaseCouponQuantityAfterCheck(criteria.couponId());
        return userCouponService.publishOnlyIfFirstTime(criteria);
    }

    @Transactional
    public long publishCouponPessimistic(CouponCriteria.PublishCriteria criteria) {
        couponService.decreaseCouponQuantityAfterCheckPessimistic(criteria.couponId());
        return userCouponService.publishOnlyIfFirstTime(criteria);
    }
}
