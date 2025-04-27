package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInfo {
    public record Stock(String skuId, Long ea, Long unitPrice) {
        public static List<OrderCommand.Product> toOrderCommand(
                List<StockInfo.Stock> stocks,
                StockCommand.Order stockCommand) {
            Map<String, Long> requestMap = new HashMap<>();
            for (StockCommand.Order.Item item : stockCommand.items()) {
                requestMap.put(item.skuId(), item.ea());
            }

            return stocks.stream()
                    .map(stock -> {
                        Long requestedEa = requestMap.get(stock.skuId());
                        return new OrderCommand.Product(stock.skuId(), requestedEa, stock.unitPrice());
                    })
                    .toList();
        }
    }
}
