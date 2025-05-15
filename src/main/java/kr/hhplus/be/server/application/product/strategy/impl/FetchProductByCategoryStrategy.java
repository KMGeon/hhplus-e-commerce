package kr.hhplus.be.server.application.product.strategy.impl;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchProductByCategoryStrategy implements ProductFetchStrategy {
    private final ProductService productService;
    @Setter
    private String category;

    @Override
    public Page<ProductStockDTO> fetch(int page, int size) {
        return productService.getProductByCategoryCode(category, page, size);
    }
}
