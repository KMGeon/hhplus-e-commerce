package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.domain.coupon.CouponCommand;

public class CouponRequest {
    public record Create(String name, String discountType, long initQuantity, double discountAmount) {
        public CouponCommand.Create toCommand() {
            return CouponCommand.Create.of(this.name, this.discountType, this.initQuantity, this.discountAmount);
        }
    }

    public record Publish(@NotNull Long userId, @NotNull Long couponId) {
        public  CouponCriteria.PublishCriteria toCriteria() {
            return new CouponCriteria.PublishCriteria(this.userId, this.couponId);
        }
    }
}
