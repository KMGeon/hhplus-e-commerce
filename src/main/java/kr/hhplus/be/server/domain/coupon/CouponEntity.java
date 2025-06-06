package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.support.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEntity extends BaseTimeEntity {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private CouponDiscountType discountType;

    @Column(nullable = false)
    private long initQuantity;

    @Column(nullable = false)
    private long remainQuantity;

    @Column(nullable = false)
    private double discountAmount;

    private LocalDateTime expireTime;

    public static CouponEntity createCoupon(String couponName, String discountType,
                                            long initQuantity, double discountAmount,
                                            LocalDateTime expireTime) {
        CouponDiscountType type = CouponDiscountType.findDiscountType(discountType);
        return CouponEntity.builder()
                .name(couponName)
                .discountType(type)
                .initQuantity(initQuantity)
                .remainQuantity(initQuantity)
                .discountAmount(discountAmount)
                .expireTime(expireTimeProvider(expireTime))
                .build();
    }


    public void validateForPublish() {
        validateExpiration();
        validateQuantity();
    }

    public void decreaseRemainQuantity() {
        validateQuantity();
        this.remainQuantity--;
    }

    public void decreaseQuantity() {
        this.remainQuantity--;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal orderAmount) {
        return switch (discountType) {
            case FIXED_AMOUNT -> BigDecimal.valueOf(discountAmount).min(orderAmount);
            case PERCENTAGE -> orderAmount.multiply(BigDecimal.valueOf(discountAmount / 100.0));
        };
    }

    private static LocalDateTime expireTimeProvider(LocalDateTime now) {
        return now.plusDays(10);
    }

    private void validateQuantity() {
        if (remainQuantity <= 0) {
            throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
        }
    }
    private void validateExpiration() {
        if (LocalDateTime.now().isAfter(expireTime)) {
            throw new RuntimeException("만료된 쿠폰입니다.");
        }
    }

    @Builder
    private CouponEntity(Long id, String name, CouponDiscountType discountType, long initQuantity, long remainQuantity, double discountAmount, LocalDateTime expireTime) {
        this.id = id;
        this.name = name;
        this.discountType = discountType;
        this.initQuantity = initQuantity;
        this.remainQuantity = remainQuantity;
        this.discountAmount = discountAmount;
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "CouponEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discountType=" + discountType +
                ", initQuantity=" + initQuantity +
                ", remainQuantity=" + remainQuantity +
                ", discountAmount=" + discountAmount +
                ", expireTime=" + expireTime +
                "} " + super.toString();
    }
}