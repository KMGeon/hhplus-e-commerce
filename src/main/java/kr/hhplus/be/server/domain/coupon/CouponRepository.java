package kr.hhplus.be.server.domain.coupon;


import java.util.List;

public interface CouponRepository {
    CouponEntity save(CouponEntity coupon);
    CouponEntity findCouponById(Long id);
    void initializeCoupon(Long couponId, Long quantity);
    List<CouponEntity>findCouponByNotExpired();
    Long issueCoupon(Long couponId, Long userId);
    void enterQueue(Long couponId, Long userId);
    List<String> pullQueueCoupon(Long couponId, Long count);
}
