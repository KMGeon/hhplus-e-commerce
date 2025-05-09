package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ProductRepository {
    Page<ProductStockDTO> getProductsWithStockInfoByCategory(String categoryCode, Pageable pageable);
    Page<ProductStockDTO> getProductsWithStockInfo(Pageable pageable);
    long countBySkuIdIn(List<String> skuIds);
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> findAllBySkuIdIn(List<String> skuIds);
    List<HotProductQuery> findHotProductsCache();
    void setHotProductsCacheLimit5(List<HotProductQuery> hotProductsCache);
}