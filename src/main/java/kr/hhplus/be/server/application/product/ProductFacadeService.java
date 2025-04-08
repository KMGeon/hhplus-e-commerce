package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.strategy.impl.ProductFetchStrategyFactory;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacadeService {
    private final ProductFetchStrategyFactory strategyFactory;
    private final ProductService productService;


    public ProductEntity getProductById(Long id) {
        return productService.getProduct(id);
    }

    public List<ProductEntity> getProducts(Character category) {
        return strategyFactory.getStrategy(category)
                .fetch();
    }
}
