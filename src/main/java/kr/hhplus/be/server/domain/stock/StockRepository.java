package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository {
    List<EnoughStockDTO> findSkuIdAndAvailableEa(List<String> skuIds);
    int updateStockDecreaseFifo(long orderId, String skuId, long ea);
    void restoreStockByOrderId(Long orderId);
    Integer getLock(@Param("key") String key);
    void releaseLock(@Param("key") String key);
}
