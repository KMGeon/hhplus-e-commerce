//package kr.hhplus.be.server.support;
//
//import jakarta.persistence.EntityManagerFactory;
//import kr.hhplus.be.server.domain.coupon.CouponEntity;
//import kr.hhplus.be.server.domain.order.OrderEntity;
//import kr.hhplus.be.server.domain.order.OrderItemEntity;
//import kr.hhplus.be.server.domain.product.CategoryEnum;
//import kr.hhplus.be.server.domain.product.ProductEntity;
//import kr.hhplus.be.server.domain.stock.StockEntity;
//import kr.hhplus.be.server.domain.user.UserEntity;
//import org.hibernate.SessionFactory;
//import org.hibernate.StatelessSession;
//import org.hibernate.Transaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.IntStream;
//
//@Configuration
//public class DataInitalize {
//
//    private static final Logger logger = LoggerFactory.getLogger(DataInitalize.class);
//    private static final Random random = new Random();
//    private static final int BATCH_SIZE = 1000; // 배치 크기 증가
//    private static final int STOCK_BATCH_COMMIT_SIZE = 5000; // 5_000개 단위로 커밋
//    private static final int TARGET_STOCK_COUNT = 10000; // 10만개 재고
//
//    @Bean
//    public CommandLineRunner initData(EntityManagerFactory entityManagerFactory) {
//        return args -> {
//            logger.info("Stateless Session을 사용한 데이터 초기화 시작");
//
//            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//
//            try (StatelessSession statelessSession = sessionFactory.openStatelessSession()) {
//                Transaction tx = statelessSession.beginTransaction();
//
//                try {
//                    // 사용자 초기화
//                    initUsers(statelessSession);
//
//                    // 상품 및 재고 초기화
//                    Map<String, ProductEntity> productMap = initProducts(statelessSession);
//
//                    tx.commit();
//                    logger.info("기본 데이터 초기화 완료");
//
//                    // 100만개 재고 생성 (별도 트랜잭션으로 처리)
//                    createMillionStocks(statelessSession, productMap);
//
//                    // 주문 및 주문 아이템 초기화 (별도 트랜잭션)
//                    Transaction orderTx = statelessSession.beginTransaction();
//                    try {
//                        initOrders(statelessSession, productMap);
//                        orderTx.commit();
//                    } catch (Exception e) {
//                        orderTx.rollback();
//                        logger.error("주문 초기화 중 오류 발생", e);
//                        throw e;
//                    }
//
//                    logger.info("Stateless Session을 사용한 데이터 초기화 완료");
//                } catch (Exception e) {
//                    tx.rollback();
//                    logger.error("데이터 초기화 중 오류 발생", e);
//                    throw e;
//                }
//            }
//        };
//    }
//
//    private void initUsers(StatelessSession session) {
//        logger.info("사용자 초기 데이터 생성 시작");
//
//        // 기본 사용자 생성
//        UserEntity defaultUser = UserEntity.createNewUser();
//        session.insert(defaultUser);
//
//        // 추가 사용자 30명 생성
//        IntStream.range(0, 30).forEach(i -> {
//            UserEntity user = UserEntity.createNewUser();
//            session.insert(user);
//        });
//
//        // 쿠폰 생성
//        CouponEntity coupon = CouponEntity.createCoupon("생일기념 쿠폰", "FIXED_AMOUNT", 10, 1000, LocalDateTime.now());
//        session.insert(coupon);
//
//        logger.info("사용자 초기 데이터 생성 완료: 총 31명");
//    }
//
//    private Map<String, ProductEntity> initProducts(StatelessSession session) {
//        logger.info("상품 초기 데이터 생성 시작");
//
//        Map<String, ProductEntity> productMap = new HashMap<>();
//
//        // 애플 제품
//        productMap.put("A-0001-0001", createProduct(session,
//                "iPhone 15 Pro", CategoryEnum.APPLE, "A-0001-0001", 150L));
//
//        productMap.put("A-0001-0002", createProduct(session,
//                "MacBook Air M2", CategoryEnum.APPLE, "A-0001-0002", 180L));
//
//        productMap.put("A-0001-0003", createProduct(session,
//                "iPad Pro 12.9", CategoryEnum.APPLE, "A-0001-0003", 130L));
//
//        // 삼성 제품
//        productMap.put("S-0001-0001", createProduct(session,
//                "Galaxy S24 Ultra", CategoryEnum.SAMSUNG, "S-0001-0001", 1450L));
//
//        productMap.put("S-0001-0002", createProduct(session,
//                "Galaxy Tab S9", CategoryEnum.SAMSUNG, "S-0001-0002", 950L));
//
//        productMap.put("S-0001-0003", createProduct(session,
//                "Galaxy Book Pro", CategoryEnum.SAMSUNG, "S-0001-0003", 1650L));
//
//        // LG 제품
//        productMap.put("L-0001-0001", createProduct(session,
//                "LG Gram 17", CategoryEnum.LG, "L-0001-0001", 170L));
//
//        productMap.put("L-0001-0002", createProduct(session,
//                "LG OLED TV C3", CategoryEnum.LG, "L-0001-0002", 250L));
//
//        // 소니 제품
//        productMap.put("SO-0001-0001", createProduct(session,
//                "Sony WH-1000XM5", CategoryEnum.SONY, "SO-0001-0001", 450L));
//
//        productMap.put("SO-0001-0002", createProduct(session,
//                "Sony PlayStation 5", CategoryEnum.SONY, "SO-0001-0002", 650L));
//
//        // 델 제품
//        productMap.put("D-0001-0001", createProduct(session,
//                "Dell XPS 15", CategoryEnum.DELL, "D-0001-0001", 220L));
//
//        productMap.put("D-0001-0002", createProduct(session,
//                "Dell Alienware m16", CategoryEnum.DELL, "D-0001-0002", 320L));
//
//        // 추가 상품 - 카테고리별 다양한 제품 추가
//        addAdditionalProducts(session, productMap);
//
//        logger.info("상품 초기 데이터 생성 완료: 총 {}개", productMap.size());
//        return productMap;
//    }
//
//    private void addAdditionalProducts(StatelessSession session, Map<String, ProductEntity> productMap) {
//        logger.info("추가 상품 생성 시작");
//
//        // 카테고리별로 더 많은 제품 추가 (100개 이상의 다양한 제품)
//        // 애플 제품 추가
//        for (int i = 4; i <= 30; i++) {
//            String skuId = String.format("A-0001-%04d", i);
//            String productName = "Apple Product " + i;
//            productMap.put(skuId, createProduct(session,
//                    productName, CategoryEnum.APPLE, skuId, 500000L + (random.nextInt(20) * 100000L)));
//        }
//
//        // 삼성 제품 추가
//        for (int i = 4; i <= 30; i++) {
//            String skuId = String.format("S-0001-%04d", i);
//            String productName = "Samsung Product " + i;
//            productMap.put(skuId, createProduct(session,
//                    productName, CategoryEnum.SAMSUNG, skuId, 400000L + (random.nextInt(20) * 100000L)));
//        }
//
//        // LG 제품 추가
//        for (int i = 3; i <= 25; i++) {
//            String skuId = String.format("L-0001-%04d", i);
//            String productName = "LG Product " + i;
//            productMap.put(skuId, createProduct(session,
//                    productName, CategoryEnum.LG, skuId, 350000L + (random.nextInt(25) * 100000L)));
//        }
//
//        // 소니 제품 추가
//        for (int i = 3; i <= 20; i++) {
//            String skuId = String.format("SO-0001-%04d", i);
//            String productName = "Sony Product " + i;
//            productMap.put(skuId, createProduct(session,
//                    productName, CategoryEnum.SONY, skuId, 300000L + (random.nextInt(15) * 100000L)));
//        }
//
//        // 델 제품 추가
//        for (int i = 3; i <= 15; i++) {
//            String skuId = String.format("D-0001-%04d", i);
//            String productName = "Dell Product " + i;
//            productMap.put(skuId, createProduct(session,
//                    productName, CategoryEnum.DELL, skuId, 800000L + (random.nextInt(30) * 100000L)));
//        }
//
//        logger.info("추가 상품 생성 완료");
//    }
//
//    private void createMillionStocks(StatelessSession session, Map<String, ProductEntity> productMap) {
//        logger.info("100만개 재고 생성 시작");
//
//        String[] skuIds = productMap.keySet().toArray(new String[0]);
//        int totalBatches = TARGET_STOCK_COUNT / BATCH_SIZE;
//        AtomicInteger totalStocksCreated = new AtomicInteger(0);
//
//        // 배치 단위로 처리하며 주기적으로 트랜잭션 커밋
//        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
//            // 50,000개마다 새 트랜잭션 시작
//            if (batchIndex % (STOCK_BATCH_COMMIT_SIZE / BATCH_SIZE) == 0) {
//                Transaction batchTx = session.beginTransaction();
//                try {
//                    logger.info("새 배치 트랜잭션 시작: {}번째 배치", batchIndex);
//
//                    // 배치 처리 블록 생략 (아래 코드에서 처리)
//
//                } catch (Exception e) {
//                    batchTx.rollback();
//                    logger.error("배치 처리 중 오류 발생", e);
//                    throw e;
//                }
//            }
//
//            // 배치 단위로 재고 생성
//            for (int i = 0; i < BATCH_SIZE; i++) {
//                // 랜덤 상품 선택
//                String randomSkuId = skuIds[random.nextInt(skuIds.length)];
//                ProductEntity product = productMap.get(randomSkuId);
//
//                StockEntity stock = StockEntity.builder()
//                        .category(CategoryEnum.fromCategoryCode(product.getCategory()))
//                        .skuId(product.getSkuId())
//                        .orderId(null)
//                        .build();
//
//                session.insert(stock);
//
//                int created = totalStocksCreated.incrementAndGet();
//                if (created % 10000 == 0) {
//                    logger.info("재고 {}개 생성 완료 ({}%)", created, (created * 100 / TARGET_STOCK_COUNT));
//                }
//            }
//
//            // 50,000개마다 커밋
//            if ((batchIndex + 1) % (STOCK_BATCH_COMMIT_SIZE / BATCH_SIZE) == 0 ||
//                    (batchIndex + 1) == totalBatches) {
//                session.getTransaction().commit();
//                logger.info("배치 트랜잭션 커밋: 총 {}개 처리됨", totalStocksCreated.get());
//            }
//        }
//
//        logger.info("100만개 재고 생성 완료: 총 {}개", totalStocksCreated.get());
//
//        // 판매 상태 시뮬레이션
//        simulateSoldItems(session);
//    }
//
//    private ProductEntity createProduct(
//            StatelessSession session,
//            String productName,
//            CategoryEnum category,
//            String skuId,
//            Long price
//    ) {
//        // 상품 생성 및 저장
//        ProductEntity product = ProductEntity.builder()
//                .productName(productName)
//                .category(category)
//                .skuId(skuId)
//                .unitPrice(price)
//                .build();
//
//        session.insert(product);
//        return product;
//    }
//
//    private void simulateSoldItems(StatelessSession session) {
//        logger.info("판매된 재고 시뮬레이션 시작");
//
//        // 약 5%의 재고만 판매 상태로 변경 (100만개 중 약 5만개)
//        int targetSoldCount = TARGET_STOCK_COUNT / 20;
//        int batchSize = 5000; // 한 번에 처리할 레코드 수
//        AtomicInteger totalSoldItems = new AtomicInteger(0);
//
//        Transaction soldTx = session.beginTransaction();
//        try {
//            while (totalSoldItems.get() < targetSoldCount) {
//                // 배치로 재고 데이터 가져오기
//                @SuppressWarnings("unchecked")
//                List<Long> stockIds = session.createNativeQuery(
//                                "SELECT stock_id FROM stock WHERE order_id IS NULL ORDER BY RAND() LIMIT :limit")
//                        .setParameter("limit", batchSize)
//                        .list();
//
//                if (stockIds.isEmpty()) {
//                    break; // 더 이상 처리할 데이터가 없음
//                }
//
//                // 배치를 판매된 상태로 변경
//                for (Long id : stockIds) {
//                    Long fakeOrderId = 2000L + totalSoldItems.get(); // 기존 주문 ID와 겹치지 않게 설정
//
//                    // 판매 상태로 업데이트
//                    session.createNativeQuery("UPDATE stock SET order_id = :orderId WHERE stock_id = :id")
//                            .setParameter("orderId", fakeOrderId)
//                            .setParameter("id", id)
//                            .executeUpdate();
//
//                    int soldCount = totalSoldItems.incrementAndGet();
//                    if (soldCount >= targetSoldCount) {
//                        break;
//                    }
//
//                    if (soldCount % 10000 == 0) {
//                        logger.info("판매 상태 처리: {}개 완료 ({}%)", soldCount, soldCount * 100 / targetSoldCount);
//                    }
//                }
//            }
//
//            soldTx.commit();
//            logger.info("판매 시뮬레이션 완료: 총 {}개가 판매됨", totalSoldItems.get());
//        } catch (Exception e) {
//            soldTx.rollback();
//            logger.error("판매 상태 시뮬레이션 중 오류 발생", e);
//            throw e;
//        }
//    }
//
//    private void initOrders(StatelessSession session, Map<String, ProductEntity> productMap) {
//        logger.info("주문 및 주문 아이템 초기 데이터 생성 시작");
//
//        // 주문 수 증가 (500개)
//        int orderCount = 500;
//
//        // 사용자 ID는 1~31 범위로 가정
//        for (int i = 1; i <= orderCount; i++) {
//            Long userId = (long) (random.nextInt(31) + 1);
//            LocalDateTime orderTime = LocalDateTime.now().minusDays(random.nextInt(30));
//
//            // 주문 생성
//            OrderEntity order = OrderEntity.createOrder(userId, orderTime);
//
//            // 주문 상태 랜덤 설정 (80%는 PAID, 20%는 CONFIRMED)
//            if (random.nextDouble() < 0.8) {
//                order.complete();
//            }
//
//            session.insert(order);
//
//            // ID 가져오기 (Stateless Session에서는 ID를 직접 조회해야 함)
//            Number orderId = (Number) session.createNativeQuery("SELECT LAST_INSERT_ID()").uniqueResult();
//            Long orderIdLong = orderId.longValue();
//
//            order = OrderEntity.builder()
//                    .id(orderIdLong)
//                    .userId(userId)
//                    .status(order.getStatus())
//                    .datePath(order.getDatePath())
//                    .expireTime(order.getExpireTime())
//                    .build();
//
//            // 주문 아이템 생성 (1~5개)
//            int itemCount = random.nextInt(5) + 1;
//            BigDecimal totalPrice = BigDecimal.ZERO;
//            BigDecimal totalEa = BigDecimal.ZERO;
//
//            for (int j = 0; j < itemCount; j++) {
//                // 랜덤 상품 선택
//                String[] skuIds = productMap.keySet().toArray(new String[0]);
//                String randomSkuId = skuIds[random.nextInt(skuIds.length)];
//                ProductEntity product = productMap.get(randomSkuId);
//
//                // 수량 1~5개
//                Long quantity = (long) (random.nextInt(5) + 1);
//
//                OrderItemEntity orderItem = OrderItemEntity.createOrderItem(
//                        product.getSkuId(),
//                        quantity,
//                        product.getUnitPrice()
//                );
//
//                session.insert(orderItem);
//
//                // 주문 아이템 ID 가져오기
//                Number orderItemId = (Number) session.createNativeQuery("SELECT LAST_INSERT_ID()").uniqueResult();
//                Long orderItemIdLong = orderItemId.longValue();
//
//                // Native SQL 쿼리로 관계 설정
//                session.createNativeQuery("UPDATE order_items SET order_id = :orderId WHERE order_item_id = :orderItemId")
//                        .setParameter("orderId", orderIdLong)
//                        .setParameter("orderItemId", orderItemIdLong)
//                        .executeUpdate();
//
//                totalPrice = totalPrice.add(BigDecimal.valueOf(orderItem.getTotalPrice()));
//                totalEa = totalEa.add(BigDecimal.valueOf(orderItem.getEa()));
//            }
//
//            // 주문 총액 업데이트
//            BigDecimal discountAmount = BigDecimal.ZERO;
//            if (random.nextDouble() < 0.3) { // 30% 확률로 할인 적용
//                discountAmount = totalPrice.multiply(BigDecimal.valueOf(0.1)); // 10% 할인
//            }
//
//            BigDecimal finalAmount = totalPrice.subtract(discountAmount);
//
//            session.createNativeQuery(
//                            "UPDATE orders SET total_price = :totalPrice, total_ea = :totalEa, " +
//                                    "discount_amount = :discountAmount, final_amount = :finalAmount " +
//                                    "WHERE order_id = :orderId")
//                    .setParameter("totalPrice", totalPrice)
//                    .setParameter("totalEa", totalEa)
//                    .setParameter("discountAmount", discountAmount)
//                    .setParameter("finalAmount", finalAmount)
//                    .setParameter("orderId", orderIdLong)
//                    .executeUpdate();
//
//            if (i % 50 == 0) {
//                logger.info("주문 생성 진행: {}/{}개 완료", i, orderCount);
//            }
//        }
//
//        logger.info("주문 및 주문 아이템 초기 데이터 생성 완료: 총 {}개 주문", orderCount);
//    }
//}