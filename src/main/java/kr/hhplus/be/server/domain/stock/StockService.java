package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public int decreaseStock(final Long createOrderId, StockCommand.Order stockCommand) {
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

    public void restoreStock(Long orderId){
        stockRepository.restoreStockByOrderId(orderId);
    }

}
