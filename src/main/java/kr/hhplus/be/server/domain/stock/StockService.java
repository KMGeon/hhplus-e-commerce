package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private static final int MAX_RETRY = 5;
    private static final long BACKOFF_INITIAL_MS = 100L;

    public List<StockInfo.Stock> checkEaAndProductInfo(StockCommand.Order stockCommand) {
        StockInventory requestInventory = StockInventory.fromStock(stockCommand);

        List<EnoughStockDTO> stockList = stockRepository.findSkuIdAndAvailableEa(requestInventory.getSkuIds());

        StockInventory availableInventory = StockInventory.fromStockData(stockList);

        availableInventory.validateAgainst(requestInventory);

        return stockList.stream()
                .map(v1 -> new StockInfo.Stock(v1.getSkuId(), v1.getEa(), v1.getUnitPrice()))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int decreaseStockPessimistic(final Long createOrderId, StockCommand.Order stockCommand) {
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

    public void restoreStock(Long orderId) {
        stockRepository.restoreStockByOrderId(orderId);
    }

}
