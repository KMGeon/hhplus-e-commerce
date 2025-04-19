package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    @Query(nativeQuery = true, value = """
            SELECT oi.sku_id AS skuId, 
                   SUM(oi.ea) AS orderCount
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            WHERE o.date_path BETWEEN :startPath AND :endPath
            GROUP BY oi.sku_id
            ORDER BY SUM(oi.ea) DESC
            LIMIT 5
            """)
    List<HotProductDTO> findHotProducts(@Param("startPath") String startPath,
                                        @Param("endPath") String endPath);
}
