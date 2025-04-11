package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.StockEntity;
import kr.hhplus.be.server.domain.product.StockRepository;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {
    private final StockFakeJpaRepository stockFakeJpaRepository;


    @Override
    public Optional<StockEntity> findByProductEntityId(Long productId) {
        return stockFakeJpaRepository.findByProductEntityId(productId);
    }

    @Override
    public List<StockEntity> findAllByProductIdIn(List<Long> productIds) {
        return stockFakeJpaRepository.findAllByProductIdIn(productIds);
    }
}
