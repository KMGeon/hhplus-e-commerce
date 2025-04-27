package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.ProductEntity;
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