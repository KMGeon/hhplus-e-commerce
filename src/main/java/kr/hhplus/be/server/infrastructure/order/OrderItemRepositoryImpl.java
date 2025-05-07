package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderCoreRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderEntity save(OrderEntity order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public OrderEntity findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
    }

    @Override
    public List<HotProductDTO> findHotProducts(String startPath, String endPath) {
        return orderJpaRepository.findHotProducts(startPath, endPath);
    }

    @Override
    public void updateExpireOrderStatus(List<Long> expiredOrderIds) {
        orderJpaRepository.updateOrderStatusByIds(expiredOrderIds);
    }

    @Override
    public List<Long> findExpiredOrderIds() {
        return orderJpaRepository.findExpiredOrderIds();
    }

    @Override
    public OrderItemEntity save(OrderItemEntity entity) {
        return orderItemJpaRepository.save(entity);
    }

}
