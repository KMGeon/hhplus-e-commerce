package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategyFactory;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.HotProductCacheManager;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacadeService {
    private final ProductFetchStrategyFactory strategyFactory;
    private final HotProductCacheManager hotProductCacheManager;

    @Transactional(readOnly = true)
    public Page<ProductStockDTO> getProducts(String category, int page, int size) {
        return strategyFactory.getStrategy(category, page, size)
                .fetch(page, size);
    }

    @Transactional(readOnly = true)
    public List<kr.hhplus.be.server.domain.order.projection.HotProductQuery> getHotProducts() {
        return hotProductCacheManager.findHotProductsCache(LocalDateTime.now());
    }
}

