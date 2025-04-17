package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockJpaRepository extends JpaRepository<StockEntity, Long> {

    @Query(nativeQuery = true, value = """
            select count(a.stock_id)
                        from(
            select stock_id
                            from stock
                            where order_id is not null) as a
            """)
    Long getCountByProductId(Long productId);

    @Query(nativeQuery = true, value = """
            SELECT 
                sku_id AS skuId, 
                COUNT(*) AS ea 
            FROM 
                stock 
            WHERE 
                order_id IS NULL 
                AND sku_id IN :skuIds 
            GROUP BY 
                sku_id
            """)
    List<EnoughStockDTO> findSkuIdAndAvailableEa(@Param("skuIds") List<String> skuIds);

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE stock s
            JOIN (
                SELECT stock_id
                FROM stock
                WHERE sku_id = :skuId
                AND order_id IS NULL
                ORDER BY created_at ASC
                LIMIT :quantity
            ) AS selected ON s.stock_id = selected.stock_id
            SET s.order_id = :orderId
            """)
    int updateStockDecreaseFifo(
            @Param("orderId") long orderId,
            @Param("skuId") String skuId,
            @Param("quantity") long quantity
    );
}
