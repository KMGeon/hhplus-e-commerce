package kr.hhplus.be.server.domain.payment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;


    @Transactional
    public void paymentProcessByBoolean(Long orderId, Long userId, BigDecimal amount, boolean isSuccess) {
        PaymentEntity payment = PaymentEntity.create(orderId, userId, amount);
        if (isSuccess) {
            payment.complete();
            paymentEventPublisher.publishSuccess(PaymentEvent.PAYMENT_GATEWAY.of(orderId, amount));
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