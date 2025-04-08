package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.stock.StockEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {
    private Long id;
    private String skuId;
    private char category;
    private String productName;
    private long price;
    private StockEntity stock;

    public ProductEntity(Long id, String skuId, char category, String productName, long price) {
        this.id = id;
        this.skuId = skuId;
        this.category = category;
        this.productName = productName;
        this.price = price;
    }

    public void setStock(StockEntity stock) {
        if (stock == null) {
            throw new IllegalArgumentException("재고 정보는 null일 수 없습니다.");
        }
        if (!this.skuId.equals(stock.getSkuId())) {
            throw new IllegalArgumentException("제품과 재고의 SKU ID가 일치하지 않습니다.");
        }
        this.stock = stock;
    }

    public int getQuantity() {
        return stock != null ? stock.getQuantity() : 0;
    }

    public boolean decreaseStock(int amount) {
        if (stock == null) {
            return false;
        }
        return stock.decreaseStock(amount);
    }

    public void increaseStock(int amount) {
        if (stock == null) {
            throw new IllegalStateException("재고 정보가 설정되지 않았습니다.");
        }
        stock.increaseStock(amount);
    }
}