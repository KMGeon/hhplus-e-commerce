package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.CouponStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import kr.hhplus.be.server.infrastructure.order.OrderItemJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;



public class IntegrationTest  extends ApplicationContext{

    @Autowired
    OrderFacadeService orderFacadeService;

    @Autowired
    private CouponFacadeService couponFacadeService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentFacadeService paymentFacadeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserCouponJpaRepository couponJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private ProductJpaRepository productRepository;

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
        userId = userJpaRepository.save(newUser).getId();
        createTestData();
    }


    @Test
    @DisplayName("""
             1. 유저가 포인트를 충전
             2. 쿠폰을 발급하고
             3. 주문을 생성한다.
             4. 이후 주문을 결제한다. (쿠폰을 사용한다.)
            """)
    public void 시나리오_1() throws Exception {
        // 유저가 포인트를 충전한다.
        userService.charge(new UserCommand.PointCharge(userId, 10000L));

        // 쿠폰을 만든다.
        couponService.save(new CouponCommand.Create("생일 축하해요 쿠폰", "FIXED_AMOUNT", 10L, 1000L));
        // 유저가 쿠폰을 발급한다
        couponFacadeService.publishCoupon(new CouponCriteria.PublishCriteria(userId, 1L));


        // 총 주문 금액 계산
        List<OrderCriteria.Item> items = List.of(
                new OrderCriteria.Item("LG-GRAM-17", 1L),    // 104
                new OrderCriteria.Item("SM-TAB-S9", 2L),     // 103 * 2 = 206
                new OrderCriteria.Item("AP-MB-AIR-M2", 3L)   // 101 * 3 = 303
        );


        // 유저가 주문을 생성한다.
        Long orderId = orderFacadeService.createOrder(new OrderCriteria.Order(userId, items));

        // 유저가 주문을 결제한다.
        paymentFacadeService.payment(new PaymentCriteria.Pay(userId, orderId, 1L));

        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow();
        UserCouponEntity userCouponEntity = couponJpaRepository.findById(1L)
                .orElseThrow();
        OrderEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow();

        // 결제 후 검증
        assertEquals(9187, userEntity.getPoint());
        assertEquals(CouponStatus.USED, userCouponEntity.getCouponStatus(), "쿠폰 상태는 USED여야 함");
        assertEquals(OrderStatus.PAID, orderEntity.getStatus(), "주문 상태는 PAID여야 함");
        assertEquals(new BigDecimal("1813.00"), orderEntity.getTotalPrice());
        assertEquals(new BigDecimal("1000.00"), orderEntity.getDiscountAmount());
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
}
