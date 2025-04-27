package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.config.ApplicationContext;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StockConcurrencyTest extends ApplicationContext {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private OrderFacadeService orderFacadeService;

    @Autowired
    private UserService userService;

    private List<StockEntity> testStocks;
    private Long userId = 0L;

    // 테스트 SKU 목록
    private final List<String> TEST_SKU_IDS = List.of(
            "AP-IP15-PRO",
            "AP-MB-AIR-M2",
            "SM-S24-ULTRA",
            "SM-TAB-S9",
            "LG-GRAM-17"
    );

    private static final int THREAD_COUNT = 10;

    @BeforeEach
    public void setUp() {
        // 기존 데이터 정리
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        stockJpaRepository.deleteAll();
        productRepository.deleteAll();
        userJpaRepository.deleteAll();

        // 사용자 생성 및 포인트 충전
        UserEntity newUser = UserEntity.createNewUser();
        userId = userJpaRepository.save(newUser).getId();
        userService.charge(new UserCommand.PointCharge(userId, 100_000L));

        // 테스트 데이터 생성
        createTestData();
    }

    @Test
    public void 모든_order_Id가_있다() throws Exception {
        // 스레드 풀 및 카운트다운 래치 생성
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 동시에 여러 주문 생성 (각 주문은 모든 SKU를 1개씩 포함)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 SKU를 포함하는 주문 생성
                    List<OrderCriteria.Item> items = new ArrayList<>();
                    for (String skuId : TEST_SKU_IDS) {
                        items.add(new OrderCriteria.Item(skuId, 1L));
                    }

                    OrderCriteria.Order orderCriteria = new OrderCriteria.Order(userId, items);
                    Long orderId = orderFacadeService.createOrder(orderCriteria);
                    if (orderId != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executorService.shutdown();

        // 총 주문 수가 적절한지 확인
        assertEquals(THREAD_COUNT, successCount.get() + failureCount.get(),
                "총 처리된 주문 수가 스레드 수와 일치해야 합니다");

        List<String> list = stockJpaRepository.findAll().stream()
                .map(StockEntity::getSkuId)
                .toList();

        assertFalse(list.contains(null), "리스트에 null 값이 포함되어 있습니다");
        assertTrue(list.stream().allMatch(Objects::nonNull), "모든 skuId는 null이 아니어야 합니다");
        assertThat(list).doesNotContainNull();
    }

    @Test
    public void LG_GRAM_17_은_모두_orderId가_있다() throws Exception {
        // 스레드 풀 및 카운트다운 래치 생성
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // 성공/실패 카운터
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 동시에 여러 주문 생성 (각 주문은 모든 SKU를 1개씩 포함)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 SKU를 포함하는 주문 생성
                    List<OrderCriteria.Item> items = new ArrayList<>();
                    items.add(new OrderCriteria.Item("LG-GRAM-17", 5L));

                    OrderCriteria.Order orderCriteria = new OrderCriteria.Order(userId, items);
                    Long orderId = orderFacadeService.createOrder(orderCriteria);
                    if (orderId != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("주문 실패: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executorService.shutdown();

        // 최종 재고 확인 및 검증
        System.out.println("성공한 주문 수: " + successCount.get());
        System.out.println("실패한 주문 수: " + failureCount.get());


        assertEquals(THREAD_COUNT, successCount.get() + failureCount.get(),
                "총 처리된 주문 수가 스레드 수와 일치해야 합니다");
        List<StockEntity> lgGramStocks = stockJpaRepository.findAll().stream()
                .filter(stock -> "LG-GRAM-17".equals(stock.getSkuId()))
                .toList();
        boolean allLgGramOrderIdsNotNull = lgGramStocks.stream()
                .allMatch(stock -> stock.getOrderId() != null);
        assertTrue(allLgGramOrderIdsNotNull, "모든 LG-GRAM-17의 orderId는 null이 아니어야 합니다");
        assertThat(lgGramStocks).isNotEmpty()
                .allMatch(stock -> stock.getOrderId() != null, "모든 LG-GRAM-17의 orderId는 null이 아니어야 합니다");
    }

    private void createTestData() {
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

        // 재고 데이터 생성 - 수정: 정확히 INITIAL_STOCK_PER_SKU만큼만 생성
        testStocks = new ArrayList<>();
        for (String skuId : TEST_SKU_IDS) {
            CategoryEnum category = getCategoryForSku(skuId);

            for (int i = 1; i <= 10; i++) {
                StockEntity stock = StockEntity.builder()
                        .skuId(skuId)
                        .category(category)
                        .orderId(null)
                        .build();
                testStocks.add(stock);
            }
        }
        stockJpaRepository.saveAll(testStocks);
    }

    // SKU ID에 따른 카테고리 반환하는 헬퍼 메서드 추가
    private CategoryEnum getCategoryForSku(String skuId) {
        if (skuId.startsWith("AP")) {
            return CategoryEnum.APPLE;
        } else if (skuId.startsWith("SM")) {
            return CategoryEnum.SAMSUNG;
        } else if (skuId.startsWith("LG")) {
            return CategoryEnum.LG;
        }
        return CategoryEnum.APPLE; // 기본값
    }
}