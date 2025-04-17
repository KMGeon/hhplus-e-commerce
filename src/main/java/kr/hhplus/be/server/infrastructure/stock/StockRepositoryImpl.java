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
    public Long getCountByProductId(Long productId) {
        return repository.getCountByProductId(productId);
    }

    @Override
    public List<EnoughStockDTO> findSkuIdAndAvailableEa(List<String> skuIds) {
        return repository.findSkuIdAndAvailableEa(skuIds);
    }

    @Override
    public int updateStockDecreaseFifo(long orderId, String skuId, long ea) {
        return repository.updateStockDecreaseFifo(orderId, skuId, ea);
    }
}
