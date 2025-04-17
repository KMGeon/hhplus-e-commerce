package kr.hhplus.be.server.domain.stock;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import lombok.*;


@Getter
@ToString
@Entity(name = "stock")
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

    public void decreaseStock(Long orderId) {
        if (this.orderId != null) throw new IllegalStateException("이미 할당된 재고입니다. stockId: " + this.id);
        this.orderId = orderId;
    }

    public boolean isAllocated() {
        return this.orderId != null;
    }
}