package kr.hhplus.be.server.application.product.strategy.impl;

import kr.hhplus.be.server.application.product.strategy.FetchAllProductStrategy;
import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFetchStrategyFactory {
    private final FetchAllProductStrategy allStrategy;
    private final FetchProductByCategoryStrategy categoryStrategy;

    public ProductFetchStrategy getStrategy(Character category) {
        if (category == null) {
            return allStrategy;
        } else {
            categoryStrategy.setCategory(category);
            return categoryStrategy;
        }
    }
}