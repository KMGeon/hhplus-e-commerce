package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.domain.stock.StockRepository;
import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository repository;

    @Override
    public List<EnoughStockDTO> findSkuIdAndAvailableEa(List<String> skuIds) {
        return repository.findSkuIdAndAvailableEa(skuIds);
    }

    @Override
    public int updateStockDecreaseFifo(long orderId, String skuId, long ea) {
        return repository.updateStockDecreaseFifo(orderId, skuId, ea);
    }

    @Override
    public void restoreStockByOrderId(Long orderId) {
        repository.restoreStockByOrderId(orderId);
    }

    @Override
    public Integer getLock(String key) {
        return repository.getLock(key);
    }

    @Override
    public void releaseLock(String key) {
        repository.releaseLock(key);
    }
}
