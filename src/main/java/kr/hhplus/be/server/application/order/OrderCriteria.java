package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<Item>products
    ){
        public OrderCommand.Order toCommand() {
            return new OrderCommand.Order(
                    this.userId,
                    this.products().stream()
                            .map(item -> new OrderCommand.Item(item.productId(), item.ea(), item.price()))
                            .toList()
            );
        }
    }

    public record Item(Long productId, long ea, long price) {
    }
}
