package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository repository;
    private final ProductCacheRepository productCacheRepository;

    @Override
    public Optional<ProductEntity> findById(Long id) {
        return repository.findById(id);
    }


    @Override
    public List<ProductEntity> findAllBySkuIdIn(List<String> skuIds) {
        return repository.findAllBySkuIdIn(skuIds);
    }

    @Override
    public Page<ProductStockDTO> getProductsWithStockInfoByCategory(String categoryCode, Pageable pageable) {
        return repository.getProductsWithStockInfoByCategory(categoryCode, pageable);
    }

    @Override
    public Page<ProductStockDTO> getProductsWithStockInfo(Pageable pageable) {
        return repository.getProductsWithStockInfo(pageable);
    }

    @Override
    public long countBySkuIdIn(List<String> skuIds) {
        return repository.countBySkuIdIn(skuIds);
    }

    @Override
    public List<HotProductQuery> findHotProductsCache() {
        return productCacheRepository.findHotProductsCacheLimit5();
    }

    @Override
    public void setHotProductsCacheLimit5(List<HotProductQuery> hotProductsCache) {
        productCacheRepository.setHotProductsCacheLimit5(hotProductsCache);
    }

}
