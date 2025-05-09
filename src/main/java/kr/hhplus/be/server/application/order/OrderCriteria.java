package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.stock.StockCommand;

import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<Item>products
    ){
        public OrderCommand.Order toOrderCommand() {
            return new OrderCommand.Order(
                    this.userId,
                    this.products().stream()
                            .map(item -> new OrderCommand.Order.Item(item.skuId(), item.ea()))
                            .toList()
            );
        }

        public StockCommand.Order toStockCommand() {
            return new StockCommand.Order(
                    this.products().stream()
                            .map(item -> new StockCommand.Order.Item(item.skuId(), item.ea()))
                            .toList()
            );
        }
    }

    public record Item(String skuId, long ea) {
        public static OrderCriteria.Item[] toArray(List<Item> items){
            return items.toArray(new OrderCriteria.Item[0]);
        }
    }
}
