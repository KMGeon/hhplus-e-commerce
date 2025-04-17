package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.application.product.strategy.impl.FetchAllProductStrategy;
import kr.hhplus.be.server.application.product.strategy.impl.FetchProductByCategoryStrategy;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFetchStrategyFactory {
    private final FetchAllProductStrategy allStrategy;
    private final FetchProductByCategoryStrategy categoryStrategy;

    public ProductFetchStrategy getStrategy(String categoryCode) {
        System.out.println("categoryCode = " + categoryCode);
        return CategoryEnum.validateCategoryCode(categoryCode)
                .map(category -> {
                    categoryStrategy.setCategory(category.getCategoryCode());
                    return (ProductFetchStrategy) categoryStrategy;
                })
                .orElse(allStrategy);
    }
}