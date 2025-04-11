package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemEntity {

    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long ea;
    private Long price;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private OrderEntity order;


    public static OrderItemEntity createOrderItem(Long productId, Long ea, Long price) {
        return OrderItemEntity.builder()
                .productId(productId)
                .ea(ea)
                .price(price)
                .build();
    }

    @Builder
    private OrderItemEntity(Long id, Long productId, Long ea, Long price, OrderEntity order) {
        this.id = id;
        this.productId = productId;
        this.ea = ea;
        this.price = price;
        this.order = order;
    }

    public long getTotalPrice() {
        return this.price * this.ea;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}