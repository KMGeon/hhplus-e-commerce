package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.order.OrderCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInfo {

    public record Stock(String skuId, Long ea, Long unitPrice) {
//        public static List<OrderCommand.Product> toOrderCommand(List<StockInfo.Stock> stocks) {
//            return stocks.stream()
//                    .map(v1 -> new OrderCommand.Product(v1.skuId(), v1.ea(), v1.unitPrice()))
//                    .toList();
//        }

        public static List<OrderCommand.Product> toOrderCommand(
                List<StockInfo.Stock> stocks,
                StockCommand.Order stockCommand) {

            // 간단한 맵 생성
            Map<String, Long> requestMap = new HashMap<>();
            for (StockCommand.Order.Item item : stockCommand.items()) {
                requestMap.put(item.skuId(), item.ea());
            }

            return stocks.stream()
                    .map(stock -> {
                        // 요청 수량 사용 (재고 제한은 이미 checkEaAndProductInfo에서 처리됨)
                        Long requestedEa = requestMap.get(stock.skuId());
                        return new OrderCommand.Product(stock.skuId(), requestedEa, stock.unitPrice());
                    })
                    .toList();
        }
    }


}