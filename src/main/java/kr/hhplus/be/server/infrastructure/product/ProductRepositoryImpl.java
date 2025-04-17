package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository repository;

    @Override
    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductEntity> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    public Optional<ProductEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ProductEntity> findAllByIdIn(List<Long> productIds) {
        return repository.findAllByIdIn(productIds);
    }

    @Override
    public List<ProductEntity> findAllBySkuIdIn(List<String> skuIds) {
        return repository.findAllBySkuIdIn(skuIds);
    }

    @Override
    public List<ProductStockDTO> getProductsWithStockInfoByCategory(String categoryCode) {
        return repository.getProductsWithStockInfoByCategory(categoryCode);
    }

    @Override
    public List<ProductStockDTO> getProductsWithStockInfo() {
        return repository.getProductsWithStockInfo();
    }

    @Override
    public long countBySkuIdIn(List<String> skuIds) {
        return repository.countBySkuIdIn(skuIds);
    }


}
