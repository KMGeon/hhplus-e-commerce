package kr.hhplus.be.server.domain.order;

import java.util.Optional;

public interface OrderCoreRepository {
    OrderEntity save(OrderEntity order);
    OrderItemEntity save(OrderItemEntity entity);
    Optional<OrderEntity> findById(Long id);
}
