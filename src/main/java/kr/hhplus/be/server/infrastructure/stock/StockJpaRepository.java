package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockJpaRepository extends JpaRepository<StockEntity, Long> {

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


    @Modifying
    @Transactional
    @Query(value = "UPDATE stock SET order_id = :orderId WHERE stock_id = :stockId", nativeQuery = true)
    void updateOrderId(@Param("stockId") Long stockId, @Param("orderId") Long orderId);

    @Query(value = "SELECT MAX(stock_id) FROM stock", nativeQuery = true)
    long findMaxStockId();
}
