package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<ProductStockDTO> getProductsWithStockInfoByCategory(String categoryCode);
    List<ProductStockDTO> getProductsWithStockInfo();
    long countBySkuIdIn(List<String> skuIds);


    Optional<ProductEntity> findById(Long id);

    List<ProductEntity> findAllBySkuIdIn(List<String> skuIds);

}