package kr.hhplus.be.server.controller.coupon.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CouponPublishRequest(
        @NotNull(message = "쿠폰 ID는 필수 값입니다.")
        @Positive(message = "쿠폰 ID는 양수여야 합니다.")
        Long couponId,

        @NotNull(message = "사용자 ID는 필수 값입니다.")
        @Positive(message = "사용자 ID는 양수여야 합니다.")
        Long userId
) {
}