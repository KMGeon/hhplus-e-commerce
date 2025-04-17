package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public enum CouponDiscountType {
    PERCENTAGE, // 퍼센트 할인
    FIXED_AMOUNT; // 고정 금액 할인

    public static CouponDiscountType findDiscountType(String discountType) {
        if (discountType == null || discountType.isEmpty())
            throw new IllegalArgumentException("할인 타입은 필수 값입니다.");
        return CouponDiscountType.valueOf(discountType.toUpperCase());
    }
}