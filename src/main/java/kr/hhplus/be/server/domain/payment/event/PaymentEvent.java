package kr.hhplus.be.server.domain.payment.event;

import kr.hhplus.be.server.domain.support.EventPayload;
import lombok.Builder;

import java.math.BigDecimal;

public class PaymentEvent {
    @Builder
    public record PaymentSendInternalPayload(Long orderId, BigDecimal amount) implements EventPayload {
    }
}
