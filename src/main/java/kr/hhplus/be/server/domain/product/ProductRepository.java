package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.Collection;
import java.util.List;

public interface ProductRepository {
    List<ProductInfo.ProductInfoResponse> findProductWithStockByCategoryCode(String categoryCode);
    List<ProductInfo.ProductInfoResponse> findProductWithStock();
    List<ProductEntity> findAll();
    List<ProductEntity> findAllByIdIn(List<Long> productIds);
}