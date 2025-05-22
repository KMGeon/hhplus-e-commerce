package kr.hhplus.be.server.domain.payment;

import java.math.BigDecimal;

public class PaymentEvent {
    public record PAYMENT_GATEWAY(Long orderId, BigDecimal amount) {
        public static PAYMENT_GATEWAY of(Long orderId, BigDecimal amount) {
            return new PAYMENT_GATEWAY(orderId, amount);
        }
    }
}
