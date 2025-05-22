package kr.hhplus.be.server.domain.payment;

public interface PaymentRepository{
    PaymentEntity save(PaymentEntity payment);
    PaymentEntity findByOrderId(Long orderId);
} 