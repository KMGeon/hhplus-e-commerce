package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;

public class CouponResponse {
    public record CreateCouponResponse(Long couponId){
        public static CreateCouponResponse of(CouponInfo.CreateInfo createInfo){
            return new CreateCouponResponse(createInfo.couponId());
        }
    }
}
