package kr.hhplus.be.server.domain.payment;


import kr.hhplus.be.server.domain.support.EventType;
import kr.hhplus.be.server.domain.support.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final OutboxEventPublisher outboxEventPublisher;


    @Transactional
    public void paymentProcessByBoolean(Long orderId, Long userId, BigDecimal amount, boolean isSuccess) {
        PaymentEntity payment = PaymentEntity.create(orderId, userId, amount);
        if (isSuccess) {
            payment.complete();
            paymentEventPublisher.publishSuccess(PaymentEvent.PAYMENT_GATEWAY.of(orderId, amount));
            outboxEventPublisher.publish(
                    EventType.PAYMENT_INTERNAL_API,
                    kr.hhplus.be.server.domain.payment.event.PaymentEvent.PaymentSendInternalPayload.builder()
                            .orderId(orderId)
                            .amount(amount)
                            .build());
        } else {
            payment.fail();
        }
        paymentRepository.save(payment);
    }

    @Transactional
    public void rollbackPaymentHistory(Long orderId, String errorMessage) {
        paymentRepository.findByOrderId(orderId).rollback(errorMessage);
    }
}