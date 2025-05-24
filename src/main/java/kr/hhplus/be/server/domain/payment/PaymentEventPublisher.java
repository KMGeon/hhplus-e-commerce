package kr.hhplus.be.server.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishSuccess(PaymentEvent.PAYMENT_GATEWAY event) {
        applicationEventPublisher.publishEvent(event);
    }
}