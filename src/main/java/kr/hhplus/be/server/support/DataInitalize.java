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


    @Bean
    public CommandLineRunner initProducts(
            ProductJpaRepository productRepository,
            StockJpaRepository stockRepository
    ) {
        return args -> {
            logger.info("상품 및 재고 초기 데이터 생성 시작");

            // 애플 제품
            createProductWithStock(productRepository, stockRepository,
                    "iPhone 15 Pro", CategoryEnum.APPLE, "A-0001-0001", 150L, 10);

            createProductWithStock(productRepository, stockRepository,
                    "MacBook Air M2", CategoryEnum.APPLE, "A-0001-0002", 1800L, 5);

            createProductWithStock(productRepository, stockRepository,
                    "iPad Pro 12.9", CategoryEnum.APPLE, "A-0001-0003", 1300L, 8);

            // 삼성 제품
            createProductWithStock(productRepository, stockRepository,
                    "Galaxy S24 Ultra", CategoryEnum.SAMSUNG, "S-0001-0001", 1450L, 15);

            createProductWithStock(productRepository, stockRepository,
                    "Galaxy Tab S9", CategoryEnum.SAMSUNG, "S-0001-0002", 950L, 7);

            createProductWithStock(productRepository, stockRepository,
                    "Galaxy Book Pro", CategoryEnum.SAMSUNG, "S-0001-0003", 1650L, 4);

            // LG 제품
            createProductWithStock(productRepository, stockRepository,
                    "LG Gram 17", CategoryEnum.LG, "S-0001-0004", 1750L, 6);

            createProductWithStock(productRepository, stockRepository,
                    "LG OLED TV C3", CategoryEnum.LG, "S-0001-0005", 2500L, 3);

            // 소니 제품
            createProductWithStock(productRepository, stockRepository,
                    "Sony WH-1000XM5", CategoryEnum.SONY, "S-0002-0001", 450L, 12);

            createProductWithStock(productRepository, stockRepository,
                    "Sony PlayStation 5", CategoryEnum.SONY, "S-0002-0002", 650L, 8);

            // 델 제품
            createProductWithStock(productRepository, stockRepository,
                    "Dell XPS 15", CategoryEnum.DELL, "D-0001-0001", 2200L, 5);

            createProductWithStock(productRepository, stockRepository,
                    "Dell Alienware m16", CategoryEnum.DELL, "D-0001-0002", 3200L, 2);

            simulateSoldItems(stockRepository);

            logger.info("상품 및 재고 초기 데이터 생성 완료");
        };
    }

    private void createProductWithStock(
            ProductJpaRepository productRepository,
            StockJpaRepository stockRepository,
            String productName,
            CategoryEnum category,
            String skuId,
            Long price,
            int stockCount
    ) {
        // 상품 생성 및 저장
        ProductEntity product = ProductEntity.builder()
                .productName(productName)
                .category(category)
                .skuId(skuId)
                .unitPrice(price)
                .build();

        productRepository.save(product);

        List<StockEntity> stockEntities = new ArrayList<>();
        for (int i = 0; i < stockCount; i++) {
            StockEntity stock = StockEntity.builder()
                    .category(category)
                    .skuId(skuId)
                    .orderId(null)
                    .build();

            stockEntities.add(stock);
        }

        stockRepository.saveAll(stockEntities);

        logger.info("상품 [{}] 생성 완료. 재고 {} 개 추가됨", productName, stockCount);
    }

    private void simulateSoldItems(StockJpaRepository stockRepository) {
        List<StockEntity> allStocks = stockRepository.findAll();
        int totalStocks = allStocks.size();
        int soldCount = (int) (totalStocks * 0.3);

        for (int i = 0; i < soldCount && i < totalStocks; i++) {
            StockEntity stock = allStocks.get(i);
            Long fakeOrderId = 1000L + i;
            stock.setOrderId(fakeOrderId);
            stockRepository.save(stock);
        }

        logger.info("판매 시뮬레이션 완료: 총 {} 개의 재고 중 {} 개가 판매됨", totalStocks, soldCount);
    }
}