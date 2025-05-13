package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HotProductCacheManager {

    private final OrderCoreRepository orderCoreRepository;
    private final ProductRepository productRepository;

    public List<HotProductQuery> findHotProductsCache(LocalDateTime current) {

        List<HotProductQuery> hotProducts;
        List<HotProductQuery> cachedHotProducts = productRepository.findHotProductsCache();

        if (cachedHotProducts.isEmpty()) {
            String startDatePath = DatePathProvider.toPath(current.minusDays(3).with(LocalTime.MIN));
            String endDatePath = DatePathProvider.toPath(current.with(LocalTime.MAX));
            hotProducts = orderCoreRepository.findHotProducts(startDatePath, endDatePath);
            productRepository.setHotProductsCacheLimit5(hotProducts);
        } else {
            hotProducts = cachedHotProducts;
        }

        return hotProducts;
    }
}
