package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;

import java.util.List;

public interface StockRepository {
    List<EnoughStockDTO> findSkuIdAndAvailableEa(List<String> skuIds);
    int updateStockDecreaseFifo(long orderId, String skuId, long ea);

    void restoreStockByOrderId(Long orderId);
}
