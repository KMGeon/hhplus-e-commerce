package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentEntityTest {

    @Test
    @DisplayName("결제 엔티티 생성 성공 테스트")
    void create_success() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        BigDecimal amount = new BigDecimal("15000.00");

        // when
        PaymentEntity payment = PaymentEntity.create(orderId, userId, amount);

        // then
        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
        assertEquals(userId, payment.getUserId());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    @DisplayName("결제 완료 상태 변경 성공 테스트")
    void complete_success() {
        // given
        PaymentEntity payment = PaymentEntity.create(1L, 100L, new BigDecimal("15000.00"));

        // when
        payment.complete();

        // then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    @DisplayName("결제 실패 상태 변경 성공 테스트")
    void fail_success() {
        // given
        PaymentEntity payment = PaymentEntity.create(1L, 100L, new BigDecimal("15000.00"));

        // when
        payment.fail();

        // then
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    @DisplayName("결제 상태 전이 테스트: PENDING -> COMPLETED -> FAILED")
    void statusTransition_success() {
        // given
        PaymentEntity payment = PaymentEntity.create(1L, 100L, new BigDecimal("15000.00"));
        assertEquals(PaymentStatus.PENDING, payment.getStatus());

        // when & then - 완료 상태로 변경
        payment.complete();
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());

        // when & then - 실패 상태로 변경
        payment.fail();
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }
}