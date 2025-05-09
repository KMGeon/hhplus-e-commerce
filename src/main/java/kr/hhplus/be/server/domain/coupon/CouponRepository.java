package kr.hhplus.be.server.domain.coupon;


public interface CouponRepository {
     CouponEntity save(CouponEntity coupon);
     CouponEntity findCouponById(Long id);
}
