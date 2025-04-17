package kr.hhplus.be.server.domain.payment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void processPayment(Long orderId, Long userId, BigDecimal amount) {
        try {
            PaymentEntity payment = createPayment(orderId, userId, amount);
            payment.complete();
            paymentRepository.save(payment);
        } catch (Exception e) {
            failPayment(orderId, userId, amount, e.getMessage());
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public PaymentEntity createPayment(Long orderId, Long userId, BigDecimal amount) {
        return PaymentEntity.create(
                orderId,
                userId,
                amount
        );
    }

    public PaymentEntity failPayment(Long orderId, Long userId, BigDecimal amount, String errorMessage) {
        PaymentEntity payment = PaymentEntity.create(
                orderId,
                userId,
                amount
        );
        payment.fail();
        return paymentRepository.save(payment);
    }

}