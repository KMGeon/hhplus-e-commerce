package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_Id", nullable = true)
    private Long couponId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Long totalPrice;
    private Long totalEa;
    private Long discountPrice;
    private Long paymentPrice;
    private LocalDateTime expireTime;

    private static final Long ZERO = 0L;

    public static OrderEntity createOrder(long userId, long totalPrice, LocalDateTime now, long totalQuantity) {
        return OrderEntity.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .couponId(null)
                .totalPrice(totalPrice)
                .discountPrice(ZERO)
                .totalEa(totalQuantity)
                .paymentPrice(ZERO)
                .expireTime(now.plusMinutes(10))
                .build();
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return expireTime.isBefore(now);
    }

    @Builder
    private OrderEntity(Long id, Long userId, Long couponId, OrderStatus status, Long totalPrice, Long totalEa, Long discountPrice, Long paymentPrice, LocalDateTime expireTime) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.totalEa = totalEa;
        this.discountPrice = discountPrice;
        this.paymentPrice = paymentPrice;
        this.expireTime = expireTime;
    }
}