package kr.hhplus.be.server.interfaces.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void republishUnpublishedEvents() {
        orderService.updateExpireOrderStatus();
    }
}
