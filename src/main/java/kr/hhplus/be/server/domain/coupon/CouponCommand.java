package kr.hhplus.be.server.domain.coupon;

public class CouponCommand {
    public record Create(String couponName, String discountType, long initQuantity, double discountAmount) {
        public static Create of(String couponName, String discountType, long initQuantity, double discountAmount) {
                return new Create(couponName, discountType, initQuantity, discountAmount);
        }
    }

    public record Publish(Long userId, Long couponId) { }
}
