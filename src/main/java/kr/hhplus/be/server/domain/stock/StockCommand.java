package kr.hhplus.be.server.domain.stock;

import java.util.List;

public class StockCommand {
    public record Order(List<Item> items) {
        public record Item(String skuId, Long ea) {
        }
    }
}
