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

    @Query(nativeQuery = true, value= "select get_lock(:key, 5000)")
    Integer getLock(@Param("key") String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(@Param("key") String key);

    @Query(nativeQuery = true, value = """
            SELECT a.sku_id AS skuId,
                   COUNT(*) AS ea,
                   MAX(b.unit_price) as unitPrice
            FROM stock as a
                     inner join product as b
                                on a.sku_id = b.sku_id
            WHERE a.order_id IS NULL
              AND a.sku_id IN :skuIds
            GROUP BY a.sku_id
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
                            FOR UPDATE
                        ) AS selected ON s.stock_id = selected.stock_id
                        SET s.order_id = :orderId;
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


    @Modifying
    @Query(nativeQuery = true, value = """
            update stock s
            set s.order_id = null
            where s.order_id = :orderId
            """)
    void restoreStockByOrderId(@Param("orderId") Long orderId);
}
