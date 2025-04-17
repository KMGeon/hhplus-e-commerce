package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderCommand {
    public record Order(Long userId, List<Item>items) {

        public Order {
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("주문 아이템은 최소 1개 이상이어야 합니다.");
            }
        }

        public  long calculateTotalPrice() {
            return OrderCommand.calculateTotalPrice(this.items);
        }
        public long calculateTotalQuantity() {
            return OrderCommand.calculateTotalQuantity(this.items);
        }
    }

    public record Item(Long productId, Long ea, Long price) {
    }

    public static long calculateTotalPrice(List<Item> items) {
        return items.stream()
                .collect(Collectors.summingLong(item -> item.ea() * item.price()));
    }

    public static long calculateTotalQuantity(List<Item> items) {
        return items.stream()
                .collect(Collectors.summingLong(Item::ea));
    }
}