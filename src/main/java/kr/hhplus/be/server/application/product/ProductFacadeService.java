package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategyFactory;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacadeService {
    private final ProductFetchStrategyFactory strategyFactory;

    public List<ProductInfo.ProductInfoResponse> getProducts(String category) {
        return strategyFactory.getStrategy(category)
                .fetch();
    }
}

