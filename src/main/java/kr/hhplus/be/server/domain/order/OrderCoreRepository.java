package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.order.projection.OrderItemProductQuery;
import kr.hhplus.be.server.domain.vo.RankingItem;

import java.util.List;


public interface OrderCoreRepository {
    OrderEntity save(OrderEntity order);
    OrderItemEntity save(OrderItemEntity entity);
    OrderEntity findById(Long id);
    List<HotProductQuery> findHotProducts(String startPath, String endPath);
    void updateExpireOrderStatus(List<Long> expiredOrderIds);
    List<Long> findExpiredOrderIds();
    void addDailySummeryRanking(String key, RankingItem value, Long score);
    List<OrderItemProductQuery> findOrderItemsWithProductInfo(Long orderId);

}
