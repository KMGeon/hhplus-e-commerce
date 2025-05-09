package kr.hhplus.be.server.application.product.strategy.impl;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchProductByCategoryStrategy implements ProductFetchStrategy {
    private final ProductService productService;
    private String category;

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public List<ProductStockDTO> fetch() {
        return productService.getProductByCategoryCode(category);
    }
}
