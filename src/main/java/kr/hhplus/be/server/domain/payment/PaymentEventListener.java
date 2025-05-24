package kr.hhplus.be.server.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final MockPaymentGateway sendService;
    private final PaymentService paymentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentEvent.PAYMENT_GATEWAY event) {
        try {
            sendService.sendMockPayment(event.orderId(), event.amount());
        } catch (Exception e) {
            log.error("PaymentEventPublisher.error:{}", e.getMessage(), e);
            paymentService.rollbackPaymentHistory(event.orderId(), e.getMessage());
        }
    }
}