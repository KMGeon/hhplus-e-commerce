package kr.hhplus.be.server.domain.payment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void paymentProcessByBoolean(Long orderId, Long userId, BigDecimal amount, boolean isSuccess) {
        PaymentEntity payment = PaymentEntity.create(orderId, userId, amount);
        if (isSuccess) {
            payment.complete();
        } else {
            payment.fail();
        }
        paymentRepository.save(payment);
    }
}