package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.projection.HotProductDTO;

import java.util.List;
import java.util.Optional;

public interface OrderCoreRepository {
    OrderEntity save(OrderEntity order);
    OrderItemEntity save(OrderItemEntity entity);
    OrderEntity findById(Long id);
    List<HotProductDTO> findHotProducts(String startPath, String endPath);
    long updateExpireOrderStatus();
}
