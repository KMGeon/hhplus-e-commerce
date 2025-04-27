package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.order.OrderItemJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentUserPointConcurrencyTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private PaymentFacadeService paymentFacadeService;

    private List<StockEntity> testStocks;

    private Long userId = 0L;

    @BeforeEach
    public void setUp() {
        // 기존 데이터 정리
        stockJpaRepository.deleteAll();
        productRepository.deleteAll();
        orderJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        UserEntity newUser = UserEntity.createNewUser();
        userId = userJpaRepository.save(newUser)
                .getId();
        userService.charge(new UserCommand.PointCharge(userId, 10_000L));
        createTestData();


        createOrders();
    }

    @Test
    @DisplayName("""
            [ 상황설명 ] : 5개의 주문을 동시에 결제를 처리하는 상황
            [ 기대결과 ] : 결제 성공한 주문의 총합이 유저의 잔액과 같아야 한다.
            [ 테스트설명 ] : 5개의 주문을 동시에 결제 처리하는 상황을 가정하고, 결제 후 유저의 잔액과 결제된 주문의 총합이 일치하는지 확인한다.
            [ 테스트설명 ] : 결제 후 유저의 잔액과 결제된 주문의 총합이 일치하는지 확인한다.
            """)
    public void 사용자_10개의_주문을_동시에_결제를_처리를_하였을_때_유저의_잔액_동시성_테스트() throws Exception {
        // given
        int threadCount = 5;
        Long userInitPoint = 10_000L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (long i = 1; i <= threadCount; i++) {

            long finalI = i;
            executorService.submit(() -> {
                try {
                    paymentFacadeService.payment(new PaymentCriteria.Pay(userId, finalI, null));
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
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow();
        BigDecimal totalSum = orderJpaRepository.findAll().stream()
                .map(OrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long expectPoint = userInitPoint - totalSum.longValue();
        assertEquals(expectPoint, userEntity.getPoint());
    }


    private void createTestData() {
        // 상품 데이터 먼저 생성
        createProductData();

        // 다양한 SKU ID와 카테고리로 재고 생성
        testStocks = new ArrayList<>();

        // iPhone 15 Pro 재고 10개 (3개는 이미 판매됨)
        createStocks("AP-IP15-PRO", CategoryEnum.APPLE, 10, 3);

        // MacBook Air M2 재고 5개 (1개는 이미 판매됨)
        createStocks("AP-MB-AIR-M2", CategoryEnum.APPLE, 5, 1);

        // Galaxy S24 Ultra 재고 15개 (5개는 이미 판매됨)
        createStocks("SM-S24-ULTRA", CategoryEnum.SAMSUNG, 15, 5);

        // Galaxy Tab S9 재고 7개 (2개는 이미 판매됨)
        createStocks("SM-TAB-S9", CategoryEnum.SAMSUNG, 7, 2);

        // LG Gram 17 재고 6개 (모두 판매 가능)
        createStocks("LG-GRAM-17", CategoryEnum.LG, 6, 0);

        // 모든 재고 저장
        testStocks = stockJpaRepository.saveAll(testStocks);
    }

    private void createProductData() {
        // 상품 데이터 생성
        List<ProductEntity> products = new ArrayList<>();

        products.add(ProductEntity.builder()
                .productName("iPhone 15 Pro")
                .category(CategoryEnum.APPLE)
                .skuId("AP-IP15-PRO")
                .unitPrice(300L)
                .build());

        products.add(ProductEntity.builder()
                .productName("MacBook Air M2")
                .category(CategoryEnum.APPLE)
                .skuId("AP-MB-AIR-M2")
                .unitPrice(301L)
                .build());

        products.add(ProductEntity.builder()
                .productName("Galaxy S24 Ultra")
                .category(CategoryEnum.SAMSUNG)
                .skuId("SM-S24-ULTRA")
                .unitPrice(302L)
                .build());

        products.add(ProductEntity.builder()
                .productName("Galaxy Tab S9")
                .category(CategoryEnum.SAMSUNG)
                .skuId("SM-TAB-S9")
                .unitPrice(303L)
                .build());

        products.add(ProductEntity.builder()
                .productName("LG Gram 17")
                .category(CategoryEnum.LG)
                .skuId("LG-GRAM-17")
                .unitPrice(304L)
                .build());

        productRepository.saveAll(products);
    }

    private void createStocks(String skuId, CategoryEnum category, int totalCount, int soldCount) {
        for (int i = 0; i < totalCount; i++) {
            StockEntity stock = StockEntity.builder()
                    .skuId(skuId)
                    .category(category)
                    .orderId(i < soldCount ? 1000L + i : null)  // 판매된 상품은 주문 ID 설정
                    .build();

            testStocks.add(stock);
        }
    }

    private void createOrders() {
        // 다양한 상품으로 10개의 주문 생성
        List<String> skuIds = List.of(
                "AP-IP15-PRO",    // 300원
                "AP-MB-AIR-M2",    // 301원
                "SM-S24-ULTRA",   // 302원
                "SM-TAB-S9",      // 303원
                "LG-GRAM-17"      // 304원
        );

        for (int i = 0; i < 5; i++) {
            String skuId = skuIds.get(i % skuIds.size());
            List<OrderCriteria.Item> items = List.of(
                    new OrderCriteria.Item(skuId, 1L)
            );
            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(userId, items);
            orderFacadeService.createOrder(orderCriteria);
        }
    }
}
