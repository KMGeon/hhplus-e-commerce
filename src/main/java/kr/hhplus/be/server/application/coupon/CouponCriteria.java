package kr.hhplus.be.server.application.coupon;

public class CouponCriteria {
    public record PublishCriteria(
            Long userId,
            Long couponId
    ) {

    }
}
