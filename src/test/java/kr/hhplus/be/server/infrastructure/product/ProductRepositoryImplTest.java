package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProductRepositoryImpl.class)
class ProductRepositoryImplTest {

    @Autowired
    private ProductRepositoryImpl productRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    private List<ProductEntity> testProducts;
    private List<StockEntity> testStocks;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        stockJpaRepository.deleteAll();
        productJpaRepository.deleteAll();

        // 테스트 데이터 생성
        createTestData();
    }

    private void createTestData() {
        // 카테고리별 상품 생성
        testProducts = new ArrayList<>();
        testStocks = new ArrayList<>();

        // 애플 상품 생성
        createProductWithStock("iPhone 15 Pro", CategoryEnum.APPLE, "AP-IP15-PRO", 1500000L, 10);
        createProductWithStock("MacBook Air M2", CategoryEnum.APPLE, "AP-MB-AIR-M2", 1800000L, 5);

        // 삼성 상품 생성
        createProductWithStock("Galaxy S24 Ultra", CategoryEnum.SAMSUNG, "SM-S24-ULTRA", 1450000L, 15);
        createProductWithStock("Galaxy Tab S9", CategoryEnum.SAMSUNG, "SM-TAB-S9", 950000L, 7);

        // LG 상품 생성
        createProductWithStock("LG Gram 17", CategoryEnum.LG, "LG-GRAM-17", 1750000L, 6);

        // 소니 상품 생성
        createProductWithStock("Sony WH-1000XM5", CategoryEnum.SONY, "SN-WH-1000XM5", 450000L, 12);

        // 델 상품 생성
        createProductWithStock("Dell XPS 15", CategoryEnum.DELL, "DL-XPS-15", 2200000L, 5);

        // 모든 상품 저장
        testProducts = productJpaRepository.saveAll(testProducts);

        // 모든 재고 저장
        testStocks = stockJpaRepository.saveAll(testStocks);

        // 일부 재고 판매 처리 (약 30%)
        simulateSoldItems();
    }

    private void createProductWithStock(
            String productName,
            CategoryEnum category,
            String skuId,
            Long price,
            int stockCount
    ) {
        // 상품 생성
        ProductEntity product = ProductEntity.builder()
                .productName(productName)
                .category(category)
                .skuId(skuId)
                .unitPrice(price)
                .build();

        testProducts.add(product);

        // 해당 상품의 재고 생성
        for (int i = 0; i < stockCount; i++) {
            StockEntity stock = StockEntity.builder()
                    .category(category)
                    .skuId(skuId)
                    .orderId(null)  // 판매되지 않은 상태
                    .build();

            testStocks.add(stock);
        }
    }

    private void simulateSoldItems() {
        // 약 30%의 재고를 판매된 상태로 변경
        int totalStocks = testStocks.size();
        int soldCount = (int) (totalStocks * 0.3);

        for (int i = 0; i < soldCount && i < totalStocks; i++) {
            StockEntity stock = testStocks.get(i);
            // 임의의 주문 번호 생성
            Long fakeOrderId = 1000L + i;
            stock.setOrderId(fakeOrderId);
        }

        // 변경된 재고 저장
        testStocks = stockJpaRepository.saveAll(testStocks);
    }

    @Test
    @DisplayName("ID로 상품을 찾을 수 있다")
    void findById() {
        // given
        ProductEntity expectedProduct = testProducts.get(0);

        // when
        Optional<ProductEntity> foundProduct = productRepository.findById(expectedProduct.getId());

        // then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(expectedProduct.getId());
        assertThat(foundProduct.get().getProductName()).isEqualTo(expectedProduct.getProductName());
        assertThat(foundProduct.get().getSkuId()).isEqualTo(expectedProduct.getSkuId());
    }

    @Test
    @DisplayName("SKU ID 목록으로 상품을 찾을 수 있다")
    void findAllBySkuIdIn() {
        // given
        List<String> skuIds = Arrays.asList("AP-IP15-PRO", "SM-S24-ULTRA");

        // when
        List<ProductEntity> products = productRepository.findAllBySkuIdIn(skuIds);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(2);
        assertThat(products.stream().map(ProductEntity::getSkuId).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(skuIds);
    }

    @Test
    @DisplayName("카테고리별 상품과 재고 정보를 조회할 수 있다")
    void getProductsWithStockInfoByCategory() {
        // given
        String categoryCode = CategoryEnum.APPLE.getCategoryCode();

        // when
        List<ProductStockDTO> productsWithStock = productRepository.getProductsWithStockInfoByCategory(categoryCode);

        // then
        assertThat(productsWithStock).isNotEmpty();
        assertThat(productsWithStock.stream()
                .map(ProductStockDTO::getCategory)
                .distinct()
                .collect(Collectors.toList()))
                .containsExactly(categoryCode);

        // 애플 카테고리의 상품 수 확인
        long appleProductCount = testProducts.stream()
                .filter(p -> p.getCategory().equals(CategoryEnum.APPLE.getCategoryCode()))
                .count();
        assertThat(productsWithStock).hasSize((int) appleProductCount);
    }

    @Test
    @DisplayName("모든 상품과 재고 정보를 조회할 수 있다")
    void getProductsWithStockInfo() {
        // when
        List<ProductStockDTO> productsWithStock = productRepository.getProductsWithStockInfo();

        // then
        assertThat(productsWithStock).isNotEmpty();
        assertThat(productsWithStock).hasSize(testProducts.size());

        // 카테고리별 상품 수 확인
        for (CategoryEnum category : CategoryEnum.values()) {
            String categoryCode = category.getCategoryCode();
            long categoryProductCount = testProducts.stream()
                    .filter(p -> p.getCategory().equals(categoryCode))
                    .count();

            long dtoCount = productsWithStock.stream()
                    .filter(dto -> dto.getCategory().equals(categoryCode))
                    .count();

            assertThat(dtoCount).isEqualTo(categoryProductCount);
        }
    }

    @Test
    @DisplayName("SKU ID 목록에 해당하는 상품의 개수를 반환한다")
    void countBySkuIdIn() {
        // given
        List<String> skuIds = Arrays.asList("AP-IP15-PRO", "SM-S24-ULTRA", "NON-EXISTENT-SKU");

        // when
        long count = productRepository.countBySkuIdIn(skuIds);

        // then
        assertThat(count).isEqualTo(2); // 존재하는 SKU ID만 카운트
    }
}