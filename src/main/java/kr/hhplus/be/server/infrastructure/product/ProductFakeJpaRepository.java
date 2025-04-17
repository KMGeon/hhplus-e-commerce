package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductFakeJpaRepository  {
    List<ProductInfo.ProductInfoResponse> findProductWithStockByCategoryCode(String categoryCode){
        return null;
    }

    public List<ProductInfo.ProductInfoResponse> findProductWithStock() {
        return null;
    }

    public List<ProductEntity> findAll() {
        return null;
    }

    public List<ProductEntity> findAllByIdIn(List<Long> productIds) {
        return null;
    }
}