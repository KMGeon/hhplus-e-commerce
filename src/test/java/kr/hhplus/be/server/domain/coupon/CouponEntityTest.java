package kr.hhplus.be.server.domain.coupon;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class CouponEntityTest {

    @Test
    @DisplayName("쿠폰을 생성할 수 있다")
    void createCoupon() {
        // given
        String couponName = "신규가입 10% 할인 쿠폰";
        String discountType = "PERCENTAGE";
        long initQuantity = 100;
        double discountAmount = 10.0;
        LocalDateTime now = LocalDateTime.now();

        // when
        CouponEntity coupon = CouponEntity.createCoupon(
                couponName, discountType, initQuantity, discountAmount, now);

        // then
        assertThat(coupon.getName()).isEqualTo(couponName);
        assertThat(coupon.getDiscountType()).isEqualTo(CouponDiscountType.PERCENTAGE);
        assertThat(coupon.getInitQuantity()).isEqualTo(initQuantity);
        assertThat(coupon.getRemainQuantity()).isEqualTo(initQuantity);
        assertThat(coupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(coupon.getExpireTime()).isEqualTo(now.plusDays(10));
    }

    @Test
    @DisplayName("쿠폰 수량을 감소시킬 수 있다")
    void decreaseQuantity() {
        // given
        CouponEntity coupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getRemainQuantity), 10L)
                .create();

        long initialQuantity = coupon.getRemainQuantity();

        // when
        coupon.decreaseQuantity();

        // then
        assertThat(coupon.getRemainQuantity()).isEqualTo(initialQuantity - 1);
    }

    @Test
    @DisplayName("정액 할인 쿠폰은 지정된 금액만큼 할인한다")
    void calculateDiscountAmount_fixedAmount() {
        // given
        CouponEntity fixedCoupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getDiscountType), CouponDiscountType.FIXED_AMOUNT)
                .set(field(CouponEntity::getDiscountAmount), 5000.0)
                .create();

        BigDecimal orderAmount1 = BigDecimal.valueOf(10000); // 주문금액 > 할인금액
        BigDecimal orderAmount2 = BigDecimal.valueOf(3000);  // 주문금액 < 할인금액

        // when
        BigDecimal discountAmount1 = fixedCoupon.calculateDiscountAmount(orderAmount1);
        BigDecimal discountAmount2 = fixedCoupon.calculateDiscountAmount(orderAmount2);

        // then
        assertThat(discountAmount1).isEqualTo(BigDecimal.valueOf(5000.0));
        assertThat(discountAmount2).isEqualTo(BigDecimal.valueOf(3000)); // 주문금액보다 할인액이 크면 주문금액만큼만 할인
    }

    @Test
    @DisplayName("비율 할인 쿠폰은 주문 금액의 지정된 퍼센트만큼 할인한다")
    void calculateDiscountAmount_percentage() {
        // given
        CouponEntity percentageCoupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getDiscountType), CouponDiscountType.PERCENTAGE)
                .set(field(CouponEntity::getDiscountAmount), 20.0) // 20% 할인
                .create();

        BigDecimal orderAmount = BigDecimal.valueOf(10000);
        BigDecimal expected = BigDecimal.valueOf(2000); // 10000 * 20% = 2000

        // when
        BigDecimal discountAmount = percentageCoupon.calculateDiscountAmount(orderAmount);

        // then
        assertThat(discountAmount).isEqualByComparingTo(expected);
    }

    @Test
    @DisplayName("만료된 쿠폰은 발행 시 예외가 발생한다")
    void validateForPublish_expiredCoupon() {
        // given
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(1);

        CouponEntity expiredCoupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getExpireTime), expiredTime)
                .set(field(CouponEntity::getRemainQuantity), 10L)
                .create();

        // when & then
        assertThatThrownBy(() -> expiredCoupon.validateForPublish())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @Test
    @DisplayName("수량이 0인 쿠폰은 발행 시 예외가 발생한다")
    void validateForPublish_zeroQuantity() {
        // given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(5);

        CouponEntity zeroCoupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getExpireTime), futureTime)
                .set(field(CouponEntity::getRemainQuantity), 0L)
                .create();

        // when
        // then
        assertThatThrownBy(() -> zeroCoupon.validateForPublish())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("쿠폰이 모두 소진되었습니다.");
    }

    @Test
    @DisplayName("유효한 쿠폰은 발행 시 예외가 발생하지 않는다")
    void validateForPublish_validCoupon() {
        // given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(5);

        CouponEntity validCoupon = Instancio.of(CouponEntity.class)
                .set(field(CouponEntity::getExpireTime), futureTime)
                .set(field(CouponEntity::getRemainQuantity), 10L)
                .create();

        // when
        // then
        validCoupon.validateForPublish();
    }

}