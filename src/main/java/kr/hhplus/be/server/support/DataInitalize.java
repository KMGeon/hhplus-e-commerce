package kr.hhplus.be.server.support;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class DataInitalize {

    private static final Logger logger = LoggerFactory.getLogger(DataInitalize.class);
    private static final int PRODUCT_COUNT = 1000; // 1,000개 상품
    private static final int STOCK_PER_PRODUCT_MIN = 80; // 최소 80개 재고
    private static final int STOCK_PER_PRODUCT_MAX = 120; // 최대 120개 재고
    private static final double SOLD_RATIO = 0.3; // 30%의 재고는 판매된 상태로 설정
    private static final Random random = new Random();
    private static final int BATCH_SIZE = 1000; // 배치 사이즈 증가

    @Bean
    public CommandLineRunner initUsers(
            UserJpaRepository userRepository,
            CouponJpaRepository couponJpaRepository
    ) {
        return args -> {
            UserEntity defaultUser = UserEntity.createNewUser();
            userRepository.save(defaultUser);

            List<UserEntity> additionalUsers = IntStream.range(0, 30)
                    .mapToObj(i -> UserEntity.createNewUser())
                    .collect(Collectors.toList());

            userRepository.saveAll(additionalUsers);
            logger.info("사용자 초기 데이터 생성 완료: 총 {} 명", additionalUsers.size() + 1);

            CouponEntity coupon = CouponEntity.createCoupon("생일기념 쿠폰", "FIXED_AMOUNT", 10, 1000, LocalDateTime.now());
            couponJpaRepository.save(coupon);
        };
    }

            // 10명의 추가 사용자 생성
            List<UserEntity> additionalUsers = IntStream.range(0, 10)
                    .mapToObj(i -> UserEntity.createNewUser())
                    .collect(Collectors.toList());

            userRepository.saveAll(additionalUsers);
            logger.info("사용자 초기 데이터 생성 완료: 총 {} 명", additionalUsers.size() + 1);
        };
    }
//
//    @Bean
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public CommandLineRunner initProducts(
//            ProductJpaRepository productRepository,
//            StockJpaRepository stockRepository
//    ) {
//        return args -> {
//            logger.info("상품 및 재고 초기 데이터 생성 시작 - 목표: 약 10만건 재고");
//
//            // 카테고리별 SKU 접두사 맵
//            final String[] categoryPrefixes = {
//                    "AP-", // APPLE
//                    "SM-", // SAMSUNG
//                    "LG-", // LG
//                    "SN-", // SONY
//                    "DL-"  // DELL
//            };
//
//            // 상품명 샘플 (카테고리별)
//            final String[][] productNameSamples = {
//                    // APPLE
//                    {"iPhone", "MacBook", "iPad", "Apple Watch", "AirPods", "iMac", "Mac mini"},
//                    // SAMSUNG
//                    {"Galaxy S", "Galaxy Note", "Galaxy Tab", "Galaxy Book", "Galaxy Watch", "QLED TV"},
//                    // LG
//                    {"Gram", "OLED TV", "UltraGear", "XBOOM", "UltraFine", "StanbyME"},
//                    // SONY
//                    {"PlayStation", "Bravia", "Walkman", "Xperia", "WH-1000XM", "Alpha Camera"},
//                    // DELL
//                    {"XPS", "Alienware", "Inspiron", "Precision", "Latitude", "Vostro"}
//            };
//
//            // 모델 번호/버전 샘플
//            final String[] modelSamples = {
//                    "10", "11", "12", "13", "14", "15", "16", "S", "Plus", "Pro", "Ultra", "Max", "Air", "Mini"
//            };
//
//            // 가격 범위
//            final long minPrice = 300L;
//            final long maxPrice = 5000L;
//
//            AtomicInteger productCounter = new AtomicInteger(0);
//            AtomicInteger totalStockCounter = new AtomicInteger(0);
//
//            // 모든 카테고리에 대해 상품 생성
//            CategoryEnum[] categories = CategoryEnum.values();
//            for (int i = 0; i < PRODUCT_COUNT; i++) {
//                // 카테고리를 순환하면서 할당
//                CategoryEnum category = categories[i % categories.length];
//                int categoryIndex = i % categories.length;
//
//                // 제품명 생성
//                String[] names = productNameSamples[categoryIndex];
//                String baseName = names[random.nextInt(names.length)];
//                String model = modelSamples[random.nextInt(modelSamples.length)];
//                String productName = baseName + " " + model;
//
//                // SKU ID 생성
//                String prefix = categoryPrefixes[categoryIndex];
//                String skuId = prefix + baseName.substring(0, Math.min(3, baseName.length())).toUpperCase() + "-"
//                        + model + "-" + random.nextInt(1000);
//
//                // 가격 생성 (minPrice와 maxPrice 사이의 랜덤 값)
//                long price = minPrice + (long)(random.nextDouble() * (maxPrice - minPrice));
//
//                // 상품 생성 및 저장
//                ProductEntity product = ProductEntity.builder()
//                        .productName(productName)
//                        .category(category)
//                        .skuId(skuId)
//                        .unitPrice(price)
//                        .build();
//
//                productRepository.save(product);
//
//                // 해당 상품의 재고 수량 결정
//                int stockCount = STOCK_PER_PRODUCT_MIN + random.nextInt(STOCK_PER_PRODUCT_MAX - STOCK_PER_PRODUCT_MIN + 1);
//                totalStockCounter.addAndGet(stockCount);
//
//                // 대량의 재고 생성 시 메모리 효율을 위해 배치 처리
//                for (int batch = 0; batch < stockCount; batch += BATCH_SIZE) {
//                    int batchSize = Math.min(BATCH_SIZE, stockCount - batch);
//                    List<StockEntity> stockBatch = new ArrayList<>(batchSize);
//
//                    for (int j = 0; j < batchSize; j++) {
//                        StockEntity stock = StockEntity.builder()
//                                .category(category)
//                                .skuId(skuId)
//                                .orderId(null)  // 판매되지 않은 상태로 초기화
//                                .build();
//
//                        stockBatch.add(stock);
//                    }
//
//                    stockRepository.saveAll(stockBatch);
//                }
//
//                // 처리 상황 로깅
//                int current = productCounter.incrementAndGet();
//                if (current % 10 == 0 || current == PRODUCT_COUNT) {
//                    logger.info("상품 데이터 생성 진행 중: {}/{} (총 재고: 약 {}개)",
//                            current, PRODUCT_COUNT, totalStockCounter.get());
//                }
//            }
//
//            // 약 30%의 재고를 판매된 상태로 변경 (대량 데이터를 위한 최적화)
//            simulateSoldItems(stockRepository);
//
//            logger.info("상품 및 재고 초기 데이터 생성 완료: 총 {}개 상품, 약 {}개 재고",
//                    PRODUCT_COUNT, totalStockCounter.get());
//        };
//    }
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void simulateSoldItems(StockJpaRepository stockRepository) {
//        // 전체 재고 ID 개수 파악
//        long totalStockCount = stockRepository.count();
//        int soldCount = (int) (totalStockCount * SOLD_RATIO);
//
//        logger.info("판매 시뮬레이션 시작: 총 {}개 재고 중 약 {}개 판매 처리 예정", totalStockCount, soldCount);
//
//        // 대량 데이터 처리를 위해 ID 범위 기반으로 처리
//        // ID가 순차적으로 증가한다고 가정
//        long maxStockId = stockRepository.findMaxStockId();
//        long minStockId = 1; // 또는 stockRepository.findMinStockId() 구현
//
//        // 판매할 ID들을 랜덤하게 선택할 범위 계산
//        List<Long> selectedIds = new ArrayList<>(soldCount);
//        Random random = new Random();
//
//        // 랜덤 ID 생성 (중복 가능성 있음, 실제로는 Set 사용 또는 SQL로 직접 처리하는 것이 좋음)
//        for (int i = 0; i < soldCount; i++) {
//            long randomId = minStockId + random.nextInt((int)(maxStockId - minStockId + 1));
//            selectedIds.add(randomId);
//        }
//
//        int batchSize = 1000; // 더 큰 배치 사이즈
//        int processed = 0;
//
//        for (int i = 0; i < selectedIds.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, selectedIds.size());
//            List<Long> batch = selectedIds.subList(i, end);
//
//            // 각 배치를 트랜잭션으로 처리
//            updateBatchWithTransaction(stockRepository, batch, processed);
//
//            processed += batch.size();
//
//            if (processed % 10000 == 0 || processed >= soldCount) {
//                logger.info("판매 시뮬레이션 진행 중: {}/{}", processed, soldCount);
//            }
//        }
//
//        logger.info("판매 시뮬레이션 완료: 총 {}개의 재고 중 {}개가 판매됨", totalStockCount, processed);
//    }
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void updateBatchWithTransaction(StockJpaRepository stockRepository, List<Long> stockIds, int offset) {
//        for (int j = 0; j < stockIds.size(); j++) {
//            Long stockId = stockIds.get(j);
//            Long fakeOrderId = 1000L + offset + j;
//            try {
//                if (stockRepository.existsById(stockId)) {
//                    stockRepository.updateOrderId(stockId, fakeOrderId);
//                }
//            } catch (Exception e) {
//                logger.warn("ID {} 업데이트 중 오류 발생: {}", stockId, e.getMessage());
//            }
//        }
//    }
}