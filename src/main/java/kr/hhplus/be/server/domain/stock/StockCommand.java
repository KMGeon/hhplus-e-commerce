package kr.hhplus.be.server.domain.stock;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class StockCommand {
    public record Order(List<Item> items) {

        public String generateLockKey() {
            if (items == null || items.isEmpty()) {
                return "";
            }

            return items.stream()
                    .map(Item::skuId)
                    .filter(Objects::nonNull)
                    .filter(skuId -> !skuId.trim().isEmpty())
                    .sorted() // 일관된 키 생성을 위한 정렬
                    .collect(Collectors.joining(":"));
        }

        public record Item(String skuId, Long ea) {
        }
    }
}