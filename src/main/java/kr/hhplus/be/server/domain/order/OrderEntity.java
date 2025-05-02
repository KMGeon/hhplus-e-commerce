package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.support.BaseTimeEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter
@ToString(exclude = {"orderProducts"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseTimeEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_coupon_id", nullable = true)
    private Long userCouponId;


    @Column(length = 5, nullable = false, columnDefinition = "varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin")
    private String datePath;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private List<OrderItemEntity> orderProducts = new ArrayList<>();

    private BigDecimal totalEa;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private LocalDateTime expireTime;


    public static OrderEntity createOrder(long userId, LocalDateTime now) {
        return OrderEntity.builder()
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .datePath(DatePathProvider.toPath(now))
                .expireTime(expireTimeProvider(now))
                .orderProducts(new ArrayList<>())
                .build();
    }


    public void isAvailablePaymentState() {
        isNotExpiredOrder();
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    String.format("결제 가능한 상태가 아닙니다. 주문번호: %d, 현재 상태: %s", this.id, this.status)
            );
        }
    }

    public void addOrderItems(List<OrderItemEntity> orderItems) {
        this.orderProducts.addAll(orderItems);
        calculateTotalAmounts();
    }

    public void applyDiscount(BigDecimal discountAmount) {
        setDiscountAmount(discountAmount);
        calculateFinalAmount();
    }

    public void complete() {
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }


    private static LocalDateTime expireTimeProvider(LocalDateTime now) {
        return now.plusMinutes(10);
    }

    private void calculateFinalAmount() {
        this.finalAmount = this.totalPrice.subtract(this.discountAmount);
        if (this.finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.finalAmount = BigDecimal.ZERO;
        }
    }

    private void calculateTotalAmounts() {
        this.totalPrice = BigDecimal.valueOf(
                this.orderProducts.stream()
                        .mapToLong(OrderItemEntity::getTotalPrice)
                        .sum()
        );

        this.totalEa = BigDecimal.valueOf(
                this.orderProducts.stream()
                        .mapToLong(OrderItemEntity::getEa)
                        .sum()
        );
    }

    private boolean isNotExpiredOrder() {
        if (LocalDateTime.now().isAfter(this.expireTime)) {
            throw new RuntimeException("주문이 만료되었습니다. 주문번호: " + this.id);
        }
        return true;
    }

    private void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }


    @Builder
    private OrderEntity(Long id, Long userId, Long userCouponId, String datePath, OrderStatus status, List<OrderItemEntity> orderProducts, BigDecimal totalPrice, BigDecimal totalEa, LocalDateTime expireTime) {
        this.id = id;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.datePath = datePath;
        this.status = status;
        this.orderProducts = orderProducts;
        this.totalPrice = totalPrice;
        this.totalEa = totalEa;
        this.expireTime = expireTime;
    }
}
