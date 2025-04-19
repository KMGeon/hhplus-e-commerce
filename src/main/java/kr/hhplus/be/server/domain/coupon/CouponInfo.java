package kr.hhplus.be.server.domain.coupon;

public class CouponInfo {
    public record CreateInfo(Long couponId){
        public static CreateInfo of(Long couponId) {
            return new CreateInfo(couponId);
        }
    }
}
