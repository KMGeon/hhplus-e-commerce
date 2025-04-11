package kr.hhplus.be.server.domain.product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Optional<StockEntity> findByProductEntityId(Long productId);
    List<StockEntity> findAllByProductIdIn(@Param("productIds") List<Long> productIds);
}
