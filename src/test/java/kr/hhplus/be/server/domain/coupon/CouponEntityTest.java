package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponEntityTest {

    @Test
    void 쿠폰을_생성() {
        // given
        String couponName = "테스트 쿠폰";
        String discountType = "FIXED_AMOUNT";
        long initQuantity = 100;
        double discountAmount = 5000;
        LocalDateTime now = LocalDateTime.now();

        // when
        CouponEntity coupon = CouponEntity.createCoupon(
                couponName, discountType, initQuantity, discountAmount, now);

        // then
        assertThat(coupon.getName()).isEqualTo(couponName);
        assertThat(coupon.getDiscountType()).isEqualTo(CouponDiscountType.FIXED_AMOUNT);
        assertThat(coupon.getInitQuantity()).isEqualTo(initQuantity);
        assertThat(coupon.getRemainQuantity()).isEqualTo(initQuantity);
        assertThat(coupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(coupon.getExpireTime()).isEqualTo(now.plusDays(10));
    }

    @Test
    void 쿠폰_수량_감소() {
        // given
        CouponEntity coupon = CouponEntity.createCoupon(
                "테스트 쿠폰", "FIXED_AMOUNT", 100, 5000, LocalDateTime.now());
        long initialQuantity = coupon.getRemainQuantity();

        // when
        coupon.decreaseQuantity();

        // then
        assertThat(coupon.getRemainQuantity()).isEqualTo(initialQuantity - 1);
    }

    @ParameterizedTest
    @CsvSource({
            "FIXED_AMOUNT, 5000, 10000, 5000",  // 정액 할인 (할인액 < 주문금액)
            "FIXED_AMOUNT, 15000, 10000, 10000",  // 정액 할인 (할인액 > 주문금액)
            "PERCENTAGE, 10, 10000, 1000",  // 퍼센트 할인 (10%)
            "PERCENTAGE, 50, 10000, 5000"   // 퍼센트 할인 (50%)
    })
    void 할인_금액에_따른_각각의_결과를_반환을_처리함(String discountType, double discountAmount,
                                 int orderAmount, int expectedDiscount) {
        // given
        CouponEntity coupon = CouponEntity.createCoupon(
                "테스트 쿠폰", discountType, 100, discountAmount, LocalDateTime.now());
        BigDecimal orderAmountDecimal = BigDecimal.valueOf(orderAmount);

        // when
        BigDecimal discountAmountResult = coupon.calculateDiscountAmount(orderAmountDecimal);

        // then
        assertThat(discountAmountResult).isEqualByComparingTo(BigDecimal.valueOf(expectedDiscount));
    }

    @Test
    void 쿠폰을_발행할때_수량이_Zero_And_이하_이면_예외가_발행() {
        // given
        CouponEntity coupon = CouponEntity.builder()
                .name("테스트 쿠폰")
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(100)
                .remainQuantity(0)  // 남은 수량 0
                .discountAmount(5000)
                .expireTime(LocalDateTime.now().plusDays(10))
                .build();

        // when
        // then
        assertThatThrownBy(coupon::validateForPublish)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("쿠폰이 모두 소진되었습니다");
    }

    @Test
    void 쿠폰_발행_만료된_쿠폰이면_예외가_발생한다() {
        // given
        CouponEntity coupon = CouponEntity.builder()
                .name("테스트 쿠폰")
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(100)
                .remainQuantity(50)
                .discountAmount(5000)
                .expireTime(LocalDateTime.now().minusDays(1))  // 만료된 쿠폰
                .build();

        // when
        // then
        assertThatThrownBy(coupon::validateForPublish)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("만료된 쿠폰입니다");
    }

    @Test
    void 쿠폰_발행_시_유효기간이_남아있고_수량이_있다() {
        // given
        CouponEntity coupon = CouponEntity.builder()
                .name("테스트 쿠폰")
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(100)
                .remainQuantity(50)
                .discountAmount(5000)
                .expireTime(LocalDateTime.now().plusDays(5))
                .build();

        // when
        // then
        coupon.validateForPublish();
    }
}