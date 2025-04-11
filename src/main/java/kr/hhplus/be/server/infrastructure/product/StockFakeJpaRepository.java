package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.StockEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StockFakeJpaRepository {
    Optional<StockEntity> findByProductEntityId(Long productId){
        return Optional.empty();
    }

    List<StockEntity> findAllByProductIdIn(List<Long> productIds){
        return List.of();
    }
}