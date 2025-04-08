package kr.hhplus.be.server.domain.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockEntity {
    private Long id;
    private String skuId;
    private int quantity;

    public StockEntity(Long id, String skuId, int quantity) {
        this.id = id;
        this.skuId = skuId;
        this.quantity = quantity;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0보다 작을 수 없습니다.");
        }
        this.quantity = newQuantity;
    }

    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("재고 증가량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }

    public boolean decreaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("재고 감소량은 0보다 커야 합니다.");
        }

        if (this.quantity < amount) {
            return false; // 재고 부족
        }

        this.quantity -= amount;
        return true;
    }

    public void updateSafetyStock(int safetyStock) {
        if (safetyStock < 0) {
            throw new IllegalArgumentException("안전 재고는 0보다 작을 수 없습니다.");
        }
    }
}