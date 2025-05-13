package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;

import java.util.List;


public interface OrderCoreRepository {
    OrderEntity save(OrderEntity order);
    OrderItemEntity save(OrderItemEntity entity);
    OrderEntity findById(Long id);
    List<HotProductQuery> findHotProducts(String startPath, String endPath);
    void updateExpireOrderStatus(List<Long> expiredOrderIds);
    List<Long> findExpiredOrderIds();

}
