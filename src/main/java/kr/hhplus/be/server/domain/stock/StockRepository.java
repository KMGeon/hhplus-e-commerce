package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;

import java.util.List;

public interface StockRepository {
    Long getCountByProductId(Long productId);

    List<EnoughStockDTO> findSkuIdAndAvailableEa(List<String> skuIds);

    int updateStockDecreaseFifo(long orderId, String skuId, long ea);
}
