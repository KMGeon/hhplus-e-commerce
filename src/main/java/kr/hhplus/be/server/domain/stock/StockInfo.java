package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.List;

public class StockInfo {

    public record Stock(String skuId, Long ea, Long unitPrice) {
        public static Stock fromStock(String skuId, Long ea, Long unitPrice) {
            return new Stock(skuId, ea, unitPrice);
        }

        public static List<OrderCommand.Product> toOrderCommand(List<StockInfo.Stock> stocks) {
            return stocks.stream()
                    .map(v1 -> new OrderCommand.Product(v1.skuId(), v1.ea(), v1.unitPrice()))
                    .toList();
        }
    }
}