package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchAllProductStrategy implements ProductFetchStrategy {
    private final ProductService productService;

    @Override
    public List<ProductEntity> fetch() {
        return productService.getAllProduct();
    }
}