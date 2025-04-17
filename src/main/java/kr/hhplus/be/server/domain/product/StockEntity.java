package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockEntity {

    private static final long MIN_STOCK = 0L;
    private static final long MAX_STOCK = 9999L;

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuId;
    private Long ea;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private ProductEntity productEntity;

    public long getCurrentStock() {
        return this.ea;
    }

    public ProductEntity getProduct() {
        return this.productEntity;
    }

    public String getSkuId() {
        return this.skuId;
    }

    public StockEntity decreaseEa(long ea) {
        isEnoughStock(ea);
        validatePositiveEa(ea);

        if (this.ea < ea)
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.ea + ", 요청 수량: " + ea);

        this.ea -= ea;
        return this;
    }

    public StockEntity increaseEa(long ea) {
        validatePositiveEa(ea);
        long newStock = this.ea + ea;
        validateMaxStockLimit(newStock);

        this.ea = newStock;
        return this;
    }

    public StockEntity updateEa(long ea) {
        validateMaxStockLimit(ea);
        isEnoughStock(ea);
        this.ea = ea;
        return this;
    }


    public StockEntity updateProduct(ProductEntity productEntity) {
        this.productEntity = productEntity;
        this.skuId = productEntity.getSkuId();
        return this;
    }

    public boolean isEnoughStock(long requiredea) {
        return this.ea >= requiredea;
    }

    public boolean isBelowThreshold(long threshold) {
        return this.ea <= threshold;
    }


    private void validatePositiveEa(long ea) {
        if (ea <= 0) {
            throw new IllegalArgumentException("수량은 양수여야 합니다. 입력된 수량: " + ea);
        }
    }

    private void validateMaxStockLimit(long newStock) {
        if (newStock > MAX_STOCK) {
            throw new IllegalArgumentException("재고 최대 한도를 초과합니다. 최대 한도: " + MAX_STOCK + ", 새로운 재고: " + newStock);
        }
    }


}