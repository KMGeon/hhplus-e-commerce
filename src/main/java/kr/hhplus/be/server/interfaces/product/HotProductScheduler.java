package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotProductScheduler {

    private final OrderCoreRepository orderCoreRepository;
    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0 0 */3 * *")
    public void refetchHotProductList() {
        LocalDateTime current = LocalDateTime.now();
        String startDatePath = DatePathProvider.toPath(current.minusDays(3).with(LocalTime.MIN));
        String endDatePath = DatePathProvider.toPath(current.with(LocalTime.MAX));
        List<HotProductQuery> rtn = orderCoreRepository.findHotProducts(startDatePath, endDatePath);
        productRepository.setHotProductsCacheLimit5(rtn);
    }
}
