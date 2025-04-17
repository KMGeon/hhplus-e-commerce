package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaFakeRepository paymentJpaFakeRepository;
    @Override
    public PaymentEntity save(PaymentEntity payment) {
        return paymentJpaFakeRepository.save(payment);
    }
}
