package kr.hhplus.be.server.infrastructure.order;

import io.lettuce.core.dynamic.annotation.Param;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.domain.order.projection.OrderItemProductQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query("SELECT oi.id as orderItemId, " +
            "oi.skuId as skuId, " +
            "oi.ea as ea, " +
            "oi.unitPrice as unitPrice, " +
            "p.productName as productName " +
            "FROM OrderItemEntity oi " +
            "JOIN product p ON oi.skuId = p.skuId " +
            "WHERE oi.order.id = :orderId")
    List<OrderItemProductQuery> findOrderItemsWithProductInfo(@Param("orderId") Long orderId);
}
