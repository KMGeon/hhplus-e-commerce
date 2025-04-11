package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderItemJpaFakeRepository {

    public OrderItemEntity save(OrderItemEntity order) {
        return null;
    }

    public Optional<OrderEntity> findById(Long id) {
        return null;
    }
}
