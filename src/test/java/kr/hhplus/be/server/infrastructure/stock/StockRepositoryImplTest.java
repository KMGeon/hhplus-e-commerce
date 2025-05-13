package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StockRepositoryImplTest extends ApplicationContext {

    @Autowired
    private StockRepositoryImpl stockRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    private List<StockEntity> testStocks;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        stockJpaRepository.deleteAll();
        productRepository.deleteAll(); // product 테이블도 초기화

        // 테스트 데이터 생성
        createTestData();
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
                .unitPrice(1500000L)
                .build());

        products.add(ProductEntity.builder()
                .productName("MacBook Air M2")
                .category(CategoryEnum.APPLE)
                .skuId("AP-MB-AIR-M2")
                .unitPrice(1800000L)
                .build());

        products.add(ProductEntity.builder()
                .productName("Galaxy S24 Ultra")
                .category(CategoryEnum.SAMSUNG)
                .skuId("SM-S24-ULTRA")
                .unitPrice(1450000L)
                .build());

        products.add(ProductEntity.builder()
                .productName("Galaxy Tab S9")
                .category(CategoryEnum.SAMSUNG)
                .skuId("SM-TAB-S9")
                .unitPrice(950000L)
                .build());

        products.add(ProductEntity.builder()
                .productName("LG Gram 17")
                .category(CategoryEnum.LG)
                .skuId("LG-GRAM-17")
                .unitPrice(1750000L)
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

    @Test
    @DisplayName("SKU ID 목록에 대한 가용 재고 수량을 조회할 수 있다")
    void findSkuIdAndAvailableEa() {
        // given
        List<String> skuIds = Arrays.asList("SM-S24-ULTRA", "LG-GRAM-17");

        // when
        List<EnoughStockDTO> availableStocks = stockRepository.findSkuIdAndAvailableEa(skuIds);

        // then
        assertThat(availableStocks).hasSize(2);

        // 각 SKU 별 가용 재고 확인
        EnoughStockDTO samsungStock = availableStocks.stream()
                .filter(stock -> stock.getSkuId().equals("SM-S24-ULTRA"))
                .findFirst()
                .orElseThrow();

        EnoughStockDTO lgStock = availableStocks.stream()
                .filter(stock -> stock.getSkuId().equals("LG-GRAM-17"))
                .findFirst()
                .orElseThrow();

        assertThat(samsungStock.getEa()).isEqualTo(10); // 15개 중 5개는 판매됨
        assertThat(lgStock.getEa()).isEqualTo(6);  // 6개 모두 판매 가능
    }

    @Test
    @Transactional
    void 주문_시_선입선출로_재고를_감소를_시킨다() {
        // given
        String skuId = "AP-IP15-PRO";
        long orderId = 9999L;
        long purchaseQuantity = 3;

        List<EnoughStockDTO> beforePurchase = stockRepository.findSkuIdAndAvailableEa(List.of(skuId));
        long beforeAvailableStock = beforePurchase.get(0).getEa();

        // when
        int updatedRows = stockRepository.updateStockDecreaseFifo(orderId, skuId, purchaseQuantity);

        // then
        assertThat(updatedRows).isEqualTo(purchaseQuantity);

        // 재고 감소 확인
        List<EnoughStockDTO> afterPurchase = stockRepository.findSkuIdAndAvailableEa(List.of(skuId));
        long afterAvailableStock = afterPurchase.get(0).getEa();

        assertThat(afterAvailableStock).isEqualTo(beforeAvailableStock - purchaseQuantity);
    }

    @Test
    @Transactional
    void 재고가_부족하면_가용한_만큼_업데이트_처리함() {
        // given
        String skuId = "AP-MB-AIR-M2";
        long orderId = 9999L;
        long purchaseQuantity = 10;

        List<EnoughStockDTO> beforePurchase = stockRepository.findSkuIdAndAvailableEa(List.of(skuId));
        long beforeAvailableStock = beforePurchase.get(0).getEa();

        // when
        int updatedRows = stockRepository.updateStockDecreaseFifo(orderId, skuId, purchaseQuantity);

        // then
        assertThat(updatedRows).isEqualTo(beforeAvailableStock);
        assertThat(updatedRows).isLessThan((int) purchaseQuantity);

        // 모든 재고가 소진되었는지 확인
        List<EnoughStockDTO> afterPurchase = stockRepository.findSkuIdAndAvailableEa(List.of(skuId));

        // 재고가 모두 소진되었으므로 결과가 비어있거나 수량이 0이어야 함
        assertThat(afterPurchase.isEmpty() || afterPurchase.get(0).getEa() == 0).isTrue();
    }
}