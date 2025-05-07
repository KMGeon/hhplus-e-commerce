package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import kr.hhplus.be.server.domain.support.DistributedLock;
import kr.hhplus.be.server.domain.support.RedisLockKeyStore;
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
    @DistributedLock(key = RedisLockKeyStore.DECREASE_STOCK_ORDER_LOCK)
    public int decreaseStockLock(final Long createOrderId, StockCommand.Order stockCommand) {
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

    public void restoreStock(List<Long> orderId) {
        stockRepository.restoreStockByOrderIds(orderId);
    }

}
