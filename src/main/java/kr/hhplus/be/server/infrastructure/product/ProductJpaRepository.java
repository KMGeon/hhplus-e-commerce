package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity,Long> {
    @Query("SELECT p FROM product p WHERE p.category = :category")
    List<ProductEntity> findByCategory(@Param("category") String category);

    @Query("SELECT p FROM product p WHERE p.id IN :productIds")
    List<ProductEntity> findAllByIdIn(@Param("productIds") List<Long> productIds);

    @Query(nativeQuery = true, value = """
            SELECT
                p.product_id    AS productId,
                   MAX(p.product_name)  AS productName,
                   MAX(p.category) AS category,
                   MAX(p.sku_id)        AS skuId,
                   MAX(p.unit_price)    AS unitPrice,
                   COUNT(s.stock_id) AS stockEa
            FROM product p
                     LEFT JOIN
                 (select stock_id, sku_id
                  from stock
                  where order_id is null) as s
             on p.sku_id = s.sku_id
            WHERE p.category = :category
            GROUP BY p.product_id
            """)
    List<ProductStockDTO> getProductsWithStockInfoByCategory(@Param("category") String category);

    @Query(nativeQuery = true, value = """
            SELECT
                p.product_id    AS productId,
                   MAX(p.product_name)  AS productName,
                   MAX(p.category) AS category,
                   MAX(p.sku_id)        AS skuId,
                   MAX(p.unit_price)    AS unitPrice,
                   COUNT(s.stock_id) AS stockEa
            FROM product p
                     LEFT JOIN
                 (select stock_id, sku_id
                  from stock
                  where order_id is null) as s
             on p.sku_id = s.sku_id
            GROUP BY p.product_id
            """)
    List<ProductStockDTO> getProductsWithStockInfo();

    @Query("SELECT COUNT(p) FROM product p WHERE p.skuId IN :skuIds")
    long countBySkuIdIn(@Param("skuIds") List<String> skuIds);

    List<ProductEntity> findAllBySkuIdIn(List<String> skuIds);
}
