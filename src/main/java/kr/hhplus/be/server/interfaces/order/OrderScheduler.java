package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderFacadeService orderFacadeService;

    @Scheduled(fixedDelay = 1800000)
    public void republishUnpublishedEvents() {
        orderFacadeService.republishUnpublishedEvents();
    }
}
