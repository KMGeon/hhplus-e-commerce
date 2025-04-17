package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;


    @Transactional
    public void isEnoughStock(StockCommand.Order stockCommand) {
        Map<String, Long> orderQuantityMap = new HashMap<>(stockCommand.items().size());
        for (StockCommand.Order.Item item : stockCommand.items()) {
            orderQuantityMap.merge(item.skuId(), item.ea(), Long::sum);
        }

        List<EnoughStockDTO> availableStocks = stockRepository.findSkuIdAndAvailableEa(
                new ArrayList<>(orderQuantityMap.keySet())
        );

        for (String skuId : orderQuantityMap.keySet()) {
            Long orderEa = orderQuantityMap.get(skuId);
            Long availableEa = availableStocks.stream()
                    .filter(stock -> stock.getSkuId().equals(skuId))
                    .findFirst()
                    .map(EnoughStockDTO::getEa)
                    .orElse(0L);

            if (orderEa > availableEa) {
                throw new RuntimeException(
                    String.format("SKU: %s의 재고가 부족합니다. (주문수량: %d, 현재재고: %d)",
                        skuId, orderEa, availableEa)
                );
            }
        }
    }

    @Transactional
    public int decreaseStock(long createOrderId, StockCommand.Order stockCommand) {
        int cnt = 0;
        for (StockCommand.Order.Item item : stockCommand.items()) {
            cnt += stockRepository.updateStockDecreaseFifo(
                    createOrderId,
                    item.skuId(),
                    item.ea()
            );
        }
        return cnt;
    }
}
