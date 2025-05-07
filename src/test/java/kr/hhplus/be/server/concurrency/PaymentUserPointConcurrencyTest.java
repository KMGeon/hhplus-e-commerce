package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


public class PaymentUserPointConcurrencyTest extends ApplicationContext {

    @BeforeEach
    public void setUp() {
        // 상품 정보를 상수로 정의
        final List<OrderCriteria.Item> O1 = List.of(
                new OrderCriteria.Item("A-0001-0001", 1),
                new OrderCriteria.Item("A-0001-0002", 2)
        );

        final List<OrderCriteria.Item> O2 = List.of(
                new OrderCriteria.Item("D-0001-0001", 1),
                new OrderCriteria.Item("D-0001-0002", 2)
        );

        final List<OrderCriteria.Item> O3 = List.of(
                new OrderCriteria.Item("S-0001-0001", 1),
                new OrderCriteria.Item("S-0001-0002", 2)
        );

        final List<OrderCriteria.Item> O4 = List.of(
                new OrderCriteria.Item("L-0001-0001", 1),
                new OrderCriteria.Item("L-0001-0002", 2)
        );

        final List<OrderCriteria.Item> O5 = List.of(
                new OrderCriteria.Item("Y-0001-0001", 1),
                new OrderCriteria.Item("Y-0001-0002", 2)
        );

        // 주문 생성
        createOrderForUser(O1);
        createOrderForUser(O2);
        createOrderForUser(O3);
        createOrderForUser(O4);
        createOrderForUser(O5);
    }

    private void createOrderForUser(List<OrderCriteria.Item> items) {
        OrderCriteria.Order order = new OrderCriteria.Order(EXIST_USER, items);
        orderFacadeService.createOrder(order);
    }

    @Test
    @DisplayName("""
            [ 상황설명 ] : 5개의 주문을 동시에 결제를 처리하는 상황
            [ 기대결과 ] : 결제 성공한 주문의 총합이 유저의 잔액과 같아야 한다.
                            - 총 5개의 주문 > 각 주문 당 가격 : 6200.00 * 5
                            >>> 100_000 - 31_000 = 69_000
            [ 테스트설명 ] : 5개의 주문을 동시에 결제 처리하는 상황을 가정하고, 결제 후 유저의 잔액과 결제된 주문의 총합이 일치하는지 확인한다.
            [ 테스트설명 ] : 결제 후 유저의 잔액과 결제된 주문의 총합이 일치하는지 확인한다.
            
            
            """)
    public void 사용자_10개의_주문을_동시에_결제를_처리를_하였을_때_유저의_잔액_동시성_테스트() throws Exception {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (long i = 1; i <= threadCount; i++) {

            long finalI = i;
            executorService.submit(() -> {
                try {
                    paymentFacadeService.payment(new PaymentCriteria.Pay(EXIST_USER, finalI, null));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        UserEntity userEntity = userRepository.findById(EXIST_USER);

        Assertions.assertEquals(69_000L, userEntity.getPoint());
    }
}
