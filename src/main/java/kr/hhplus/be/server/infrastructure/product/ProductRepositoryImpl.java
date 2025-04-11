package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductFakeJpaRepository productFakeJpaRepository;

    @Override
    public List<ProductInfo.ProductInfoResponse> findProductWithStockByCategoryCode(String categoryCode) {
        return productFakeJpaRepository.findProductWithStockByCategoryCode(categoryCode);
    }

    @Override
    public List<ProductInfo.ProductInfoResponse> findProductWithStock() {
        return productFakeJpaRepository.findProductWithStock();
    }

    @Override
    public List<ProductEntity> findAll() {
        return productFakeJpaRepository.findAll();
    }

    @Override
    public List<ProductEntity> findAllByIdIn(List<Long> productIds) {
        return productFakeJpaRepository.findAllByIdIn(productIds);
    }
}
