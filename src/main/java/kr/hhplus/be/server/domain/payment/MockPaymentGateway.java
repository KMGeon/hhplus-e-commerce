package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockPaymentGateway {

    private final Random random = new Random();
    private final Map<String, PaymentResult> store = new ConcurrentHashMap<>();

    public void sendMockPayment(Long orderId, BigDecimal amount) throws InterruptedException {
        String txId = "TXN_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
            Thread.sleep(random.nextInt(2000) + 1000);
        boolean success = random.nextDouble() < 0.95;
        store.put(txId, new PaymentResult(txId, orderId, amount, success, LocalDateTime.now()));

            if (!success)
                throw new RuntimeException("결제 실패");
    }

    public void clearStore() {
        store.clear();
    }

    public PaymentResult getPaymentByOrderId(Long orderId) {
        return store.values().stream()
                .filter(result -> result.orderId.equals(orderId))
                .findFirst()
                .orElse(null);
    }
    public int getStoreSize() {
        return store.size();
    }

    public static class PaymentResult {
        public final String transactionId;
        public final Long orderId;
        public final BigDecimal amount;
        public final boolean success;
        public final LocalDateTime processedAt;

        public PaymentResult(String transactionId, Long orderId, BigDecimal amount,
                             boolean success, LocalDateTime processedAt) {
            this.transactionId = transactionId;
            this.orderId = orderId;
            this.amount = amount;
            this.success = success;
            this.processedAt = processedAt;
        }
    }
}