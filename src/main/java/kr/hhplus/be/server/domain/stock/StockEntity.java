package kr.hhplus.be.server.domain.stock;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import lombok.*;


@Getter
@ToString
@Entity(name = "stock")
@Table(indexes = {
        @Index(name = "idx_sku_id", columnList = "sku_id"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_stock_order_id_sku_id", columnList = "order_id, sku_id")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockEntity extends BaseTimeEntity {

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    private String skuId;

    @Column(name = "order_id", nullable = true)
    private Long orderId;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}