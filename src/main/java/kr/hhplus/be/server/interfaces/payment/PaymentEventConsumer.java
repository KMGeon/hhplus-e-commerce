package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.payment.MockPaymentGateway;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.event.PaymentEvent;
import kr.hhplus.be.server.domain.support.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.domain.support.EventType.Topic.PAYMENT_INTERNAL_API;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final MockPaymentGateway sendService;
    private final PaymentService paymentService;


    @KafkaListener(
            topics = PAYMENT_INTERNAL_API,
            groupId = "payment-group-id"
    )
    public void handleCouponIssueEvent(String jsonMessage) {
        Event<PaymentEvent.PaymentSendInternalPayload> event = Event.fromJson(jsonMessage, PaymentEvent.PaymentSendInternalPayload.class);
        PaymentEvent.PaymentSendInternalPayload payload = event.getPayload();
        try {
            sendService.sendMockPayment(payload.orderId(), payload.amount());
        } catch (Exception e) {
            log.error("PaymentEventPublisher.error:{}", e.getMessage(), e);
            paymentService.rollbackPaymentHistory(payload.orderId(), e.getMessage());
        }
    }
}
