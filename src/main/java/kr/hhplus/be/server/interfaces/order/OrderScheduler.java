package kr.hhplus.be.server.interfaces.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderFacadeService orderFacadeService;

    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void republishUnpublishedEvents() {
        List<Long> expireIds = orderFacadeService.republishUnpublishedEvents();
        log.info("expired orderId: {}", expireIds);
    }
}
