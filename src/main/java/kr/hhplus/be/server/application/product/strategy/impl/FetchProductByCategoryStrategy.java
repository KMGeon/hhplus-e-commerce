package kr.hhplus.be.server.application.product.strategy.impl;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchProductByCategoryStrategy implements ProductFetchStrategy {
    private final ProductService productService;
    private char category;

    public void setCategory(char category) {
        this.category = category;
    }

    @Override
    public List<ProductEntity> fetch() {
        return productService.getAllProductByCategoryCode(category);
    }
}
