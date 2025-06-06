package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.support.BaseTimeEntity;
import lombok.*;


@Entity
@Table(name = "order_items")
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemEntity extends BaseTimeEntity {
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuId;

    private Long ea;

    private Long unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;



    public static OrderItemEntity createOrderItem(String skuId, Long ea, Long unitPrice) {
        return OrderItemEntity.builder()
                .skuId(skuId)
                .ea(ea)
                .unitPrice(unitPrice)
                .build();
    }


    public long getTotalPrice() {
        return this.unitPrice * this.ea;
    }
}