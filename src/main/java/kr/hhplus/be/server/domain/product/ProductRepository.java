package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<ProductEntity> findAll();
    List<ProductEntity> findByCategory(String category);
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> findAllByIdIn(List<Long> productIds);
    List<ProductEntity> findAllBySkuIdIn(List<String> skuIds);
    List<ProductStockDTO> getProductsWithStockInfoByCategory(String categoryCode);
    List<ProductStockDTO> getProductsWithStockInfo();

    long countBySkuIdIn(List<String> skuIds);
}