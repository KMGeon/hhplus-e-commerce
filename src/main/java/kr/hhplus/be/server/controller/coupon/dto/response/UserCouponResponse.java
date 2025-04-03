package kr.hhplus.be.server.controller.coupon.dto.response;

import java.time.LocalDateTime;

public record UserCouponResponse (
        Long id,
        String couponName,
        double discountPercentage,
        LocalDateTime expirationDate
){
}
