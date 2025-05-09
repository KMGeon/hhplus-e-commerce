package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.HotProductCacheManager;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotProductScheduler {

    private final ProductRepository productRepository;
    private final HotProductCacheManager hotProductCacheManager;

    @Scheduled(cron = "0 0 0 */3 * *")
    public void refetchHotProductList() {
        productRepository.deleteHotProductsCache();
        hotProductCacheManager.findHotProductsCache(LocalDateTime.now());
    }
}
