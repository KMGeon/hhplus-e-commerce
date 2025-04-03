package kr.hhplus.be.server.controller.coupon.dto.response;

import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String couponName,
        Double discountPercentage,
        LocalDateTime expirationDate
) {
}
