package kr.hhplus.be.server.application.order;


import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderFacadeLatchServiceTest {

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private StockJpaRepository stockRepository;

    private final String GALAXY_BOOK_PRO_SKU = "SM-BOOK-PRO";
    private int initialStockCount = 4;


    /**
     * 발생 오류
     * 2025-04-18T05:24:51.452+09:00 ERROR 81970 --- [hhplus] [pool-2-thread-4] o.h.engine.jdbc.spi.SqlExceptionHelper   : Deadlock found when trying to get lock; try restarting transaction
     * 2025-04-18T05:24:51.452+09:00 ERROR 81970 --- [hhplus] [pool-2-thread-2] o.h.engine.jdbc.spi.SqlExceptionHelper   : Deadlock found when trying to get lock; try restarting transaction
     * 2025-04-18T05:24:51.452+09:00  WARN 81970 --- [hhplus] [pool-2-thread-3] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 1213, SQLState: 40001
     * 2025-04-18T05:24:51.452+09:00 ERROR 81970 --- [hhplus] [pool-2-thread-3] o.h.engine.jdbc.spi.SqlExceptionHelper   : Deadlock found when trying to get lock; try restarting transaction
     */
    @Test
    void Galaxy_Book_Pro_동시성_문제_발생() throws InterruptedException {
        // given
        int orderCount = (int) initialStockCount;

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(orderCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(orderCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < orderCount; i++) {
            final long userId = i + 1;

            executorService.submit(() -> {
                try {
                    startLatch.await();
                    try {
                        List<OrderCriteria.Item> items = new ArrayList<>();
                        items.add(new OrderCriteria.Item(GALAXY_BOOK_PRO_SKU, 1));

                        OrderCriteria.Order orderCriteria = new OrderCriteria.Order(userId, items);
                        orderFacadeService.createOrder(orderCriteria);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        List<EnoughStockDTO> availableEa = stockRepository.findSkuIdAndAvailableEa(List.of(GALAXY_BOOK_PRO_SKU));
        System.out.println("availableEa = " + availableEa);

        assertThat(successCount.get()).isNotEqualTo(orderCount);
    }
}