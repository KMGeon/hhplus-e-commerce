package kr.hhplus.be.server.application.product.strategy.impl;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchAllProductStrategy implements ProductFetchStrategy {
    private final ProductService productService;

    @Override
    public Page<ProductStockDTO> fetch(int page, int size) {
        return productService.getAllProduct(page, size);
    }
}