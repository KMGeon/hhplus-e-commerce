package kr.hhplus.be.server.domain.user.userCoupon;


import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCouponEntity extends BaseTimeEntity {

    @Id
    @Column(name = "user_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "order_id", nullable = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    public static UserCouponEntity publishCoupon(long userId, long couponId) {
        return UserCouponEntity.builder()
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();
    }

    public boolean isFirstPublish() {
        return this == null ? true : false;
    }


    public long getCouponId() {
        return this.couponId;
    }


    public void checkThisCouponCanUse(final Long userId) {
        if (!this.userId.equals(userId))
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");

        if (isUsed())
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다. 주문번호: " + this.orderId);

        if (this.couponStatus != CouponStatus.AVAILABLE)
            throw new IllegalArgumentException("사용 가능한 상태가 아닙니다. 현재 상태: " + this.couponStatus);
    }

    public boolean isUsed() {
        return this.orderId != null;
    }

    public void use(Long orderId) {
        this.orderId = orderId;
        this.couponStatus = CouponStatus.USED;
    }


    @Builder
    private UserCouponEntity(Long id, Long userId, Long couponId, Long orderId, CouponStatus couponStatus) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.orderId = orderId;
        this.couponStatus = couponStatus;
    }
}