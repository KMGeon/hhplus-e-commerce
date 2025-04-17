package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Long orderId;
    private Long userId;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        userId = 1L;
        amount = new BigDecimal("10000.00");
    }

    @Test
    @DisplayName("결제가 성공적으로 처리되어야 한다")
    void processPayment_Success() {
        // given
        PaymentEntity mockPayment = mock(PaymentEntity.class);
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(mockPayment);

        // when
        paymentService.processPayment(orderId, userId, amount);

        // then
        verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
    }


    @Test
    @DisplayName("결제 엔티티가 올바르게 생성되어야 한다")
    void createPayment_Success() {
        // when
        PaymentEntity payment = paymentService.createPayment(orderId, userId, amount);

        // then
        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
        assertEquals(userId, payment.getUserId());
        assertEquals(amount, payment.getAmount());
    }

    @Test
    @DisplayName("실패한 결제가 올바르게 저장되어야 한다")
    void failPayment_Success() {
        // given
        String errorMessage = "결제 실패";
        PaymentEntity mockPayment = mock(PaymentEntity.class);
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(mockPayment);

        // when
        PaymentEntity result = paymentService.failPayment(orderId, userId, amount, errorMessage);

        // then
        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
    }
}