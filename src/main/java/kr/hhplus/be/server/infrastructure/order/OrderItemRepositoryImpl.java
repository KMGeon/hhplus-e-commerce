package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderCoreRepository {

    private final OrderJpaFakeRepository userJpaFakeRepository;
    private final OrderItemJpaFakeRepository orderItemJpaFakeRepository;

    @Override
    public OrderEntity save(OrderEntity order) {
        return userJpaFakeRepository.save(order);
    }

    @Override
    public OrderItemEntity save(OrderItemEntity entity) {
        return orderItemJpaFakeRepository.save(entity);
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        return orderItemJpaFakeRepository.findById(id);
    }
}
