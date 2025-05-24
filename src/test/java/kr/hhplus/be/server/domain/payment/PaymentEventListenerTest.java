package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class PaymentEventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockitoBean
    private MockPaymentGateway mockPaymentGateway;

    @MockitoBean
    private PaymentService paymentService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public Executor executor() {
            return new SyncTaskExecutor();
        }
    }

    @Test
    void 결제_성공_이벤트_처리시_외부_결제_게이트웨이_호출() throws Exception {
        // Given
        Long orderId = 1L;
        BigDecimal amount = new BigDecimal("10000");
        PaymentEvent.PAYMENT_GATEWAY event = PaymentEvent.PAYMENT_GATEWAY.of(orderId, amount);

        doNothing().when(mockPaymentGateway).sendMockPayment(orderId, amount);

        // When
        applicationEventPublisher.publishEvent(event);

        // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)이므로 트랜잭션 커밋이 필요
        TestTransaction.flagForCommit();
        TestTransaction.end();

        // Then
        verify(mockPaymentGateway, times(1)).sendMockPayment(orderId, amount);
        verify(paymentService, never()).rollbackPaymentHistory(any(), any());
    }

    @Test
    void 결제_게이트웨이_실패시_롤백_처리() throws Exception {
        // Given
        Long orderId = 2L;
        BigDecimal amount = new BigDecimal("20000");
        PaymentEvent.PAYMENT_GATEWAY event = PaymentEvent.PAYMENT_GATEWAY.of(orderId, amount);

        String errorMessage = "결제 실패";
        RuntimeException paymentException = new RuntimeException(errorMessage);

        doThrow(paymentException).when(mockPaymentGateway).sendMockPayment(orderId, amount);

        // When
        applicationEventPublisher.publishEvent(event);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        // Then
        verify(mockPaymentGateway, times(1)).sendMockPayment(orderId, amount);
        verify(paymentService, times(1)).rollbackPaymentHistory(orderId, errorMessage);
    }
}