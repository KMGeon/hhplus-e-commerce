package kr.hhplus.be.server.domain.order;

import java.util.List;

public class OrderCommand {
    public record Order(Long userId, List<Item>items) {
        public Order {
            if (items == null || items.isEmpty())
                throw new IllegalArgumentException("주문 아이템은 최소 1개 이상이어야 합니다.");
        }
    }

    public record Item(String skuId, Long ea) {
    }
}