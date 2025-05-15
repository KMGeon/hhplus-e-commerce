package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.order.projection.OrderItemProductQuery;
import kr.hhplus.be.server.domain.vo.RankingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderCoreRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final HotProductQueryRepository hotProductQueryRepository;
    private final OrderCacheRepository orderCacheRepository;

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
    public List<HotProductQuery> findHotProducts(String startPath, String endPath) {
        return hotProductQueryRepository.findHotProducts(startPath, endPath);
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
    public void addDailySummeryRanking(String key, RankingItem value, Long score) {
        orderCacheRepository.addDailySummeryRanking(key, value, score);
    }

    @Override
    public List<OrderItemProductQuery> findOrderItemsWithProductInfo(Long orderId) {
        return orderItemJpaRepository.findOrderItemsWithProductInfo(orderId);
    }

    @Override
    public OrderItemEntity save(OrderItemEntity entity) {
        return orderItemJpaRepository.save(entity);
    }

}
