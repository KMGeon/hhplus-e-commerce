package kr.hhplus.be.server.concurrency;


import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.order.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderFacadeServiceConcurrencyTest  extends ApplicationContext {
    private static final int THREAD_COUNT = 5;

    @Autowired
    private OrderFacadeService orderFacadeService;


    @Test
    @DisplayName("""
            유저가 5개의 주문을 한번에 요청한다
            
            [ 동시성 포인트 ]
            - 공유자원 Stock 재고에서 정합성이 유지되어야 한다.
            
            [ 해결 방법 ]
            - 1차 : Join Update에서 비관적 락을 통해 정합성을 유지한다.
            - 2차 : 트래픽이 증가를 하였을 때 Lock을 DB에서 Redis로 변경하여 AOP 분산락
            """)
    public void concurrency_ifCreateOrder_thread_5_should_return_good() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            var param = new ArrayList<OrderCriteria.Item>();

            for (int j=1; j<9; j++){
                String concat = "A-0001-000".concat(String.valueOf(j));
                var item = new OrderCriteria.Item(concat, 1L);
                param.add(item);
            }

            OrderCriteria.Order request = new OrderCriteria.Order(1L, param);
            executorService.submit(() -> {
                try {
                    orderFacadeService.createOrder(request);
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

        List<OrderEntity> getOrders = orderJpaRepository.findAll();

        assertEquals(successCount.get(), THREAD_COUNT, "");
        assertEquals(getOrders.size(), THREAD_COUNT,"");

    }
}