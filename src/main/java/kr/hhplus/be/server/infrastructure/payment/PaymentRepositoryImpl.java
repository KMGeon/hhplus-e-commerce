package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository repository;
    @Override
    public PaymentEntity save(PaymentEntity payment) {
        return repository.save(payment);
    }

    @Override
    public PaymentEntity findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }
}
