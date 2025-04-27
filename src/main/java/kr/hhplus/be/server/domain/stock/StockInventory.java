package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockInventory {

    private Map<String, Long> skuStocks;

    private StockInventory(Map<String, Long> skuStocks) {
        this.skuStocks = skuStocks;
    }

    public static StockInventory fromStock(StockCommand.Order stockCommand) {
        Map<String, Long> skuQuantities = stockCommand.items().stream()
                .collect(Collectors.groupingBy(
                        item -> item.skuId(),
                        Collectors.summingLong(item -> item.ea())
                ));
        return new StockInventory(skuQuantities);
    }

    public static StockInventory fromStockData(List<EnoughStockDTO> stockList) {
        Map<String, Long> stockMap = stockList.stream()
                .collect(Collectors.toMap(
                        EnoughStockDTO::getSkuId,
                        EnoughStockDTO::getEa
                ));
        return new StockInventory(stockMap);
    }

    public void validateAgainst(StockInventory requestInventory) {
        for (Map.Entry<String, Long> entry : requestInventory.getSkuStocks().entrySet()) {
            String skuId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            if (!isEnoughStock(skuId, requestedQuantity)) {
                Long availableQuantity = getAvailableQuantity(skuId);
                throw new RuntimeException(
                        String.format("재고 부족: SKU %s (요청: %d개, 가용: %d개)",
                                skuId, requestedQuantity, availableQuantity)
                );
            }
        }
    }


    public Long getAvailableQuantity(String skuId) {
        return skuStocks.getOrDefault(skuId, 0L);
    }

    public boolean isEnoughStock(String skuId, Long requestedQuantity) {
        return getAvailableQuantity(skuId) >= requestedQuantity;
    }

    public List<String> getSkuIds() {
        return new ArrayList<>(skuStocks.keySet());
    }

    public record StockShortage(String skuId, Long requestedQuantity, Long availableQuantity) {
    }
}