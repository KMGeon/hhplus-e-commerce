package kr.hhplus.be.server.domain.payment;


import kr.hhplus.be.server.domain.support.OutboxEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제_성공_처리시_결제상태가_완료됨() {
        // Given
        Long orderId = 1L;
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("10000");

        PaymentEntity mockPayment = mock(PaymentEntity.class);
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(mockPayment);
        doNothing().when(outboxEventPublisher).publish(any(), any());

        // When
        paymentService.paymentProcessByBoolean(orderId, userId, amount, true);

        // Then
        verify(paymentRepository).save(any(PaymentEntity.class));
        verify(outboxEventPublisher, times(1)).publish(any(), any());
        verify(paymentEventPublisher).publishSuccess(any(PaymentEvent.PAYMENT_GATEWAY.class));
    }
    @Test
    void 결제_실패_처리시_결제상태가_실패됨() {
        // given
        Long orderId = 1L;
        Long userId = 100L;
        BigDecimal amount = BigDecimal.valueOf(10000);
        boolean isSuccess = false;

        // when
        paymentService.paymentProcessByBoolean(orderId, userId, amount, isSuccess);

        // then
        ArgumentCaptor<PaymentEntity> paymentCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        PaymentEntity capturedPayment = paymentCaptor.getValue();
        assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
        assertThat(capturedPayment.getUserId()).isEqualTo(userId);
        assertThat(capturedPayment.getAmount()).isEqualTo(amount);
        assertThat(capturedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}