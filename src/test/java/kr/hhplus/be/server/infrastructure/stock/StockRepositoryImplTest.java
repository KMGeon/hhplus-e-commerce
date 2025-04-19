package kr.hhplus.be.server.infrastructure.stock;


import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.stock.StockEntity;
import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StockRepositoryImpl.class)
class StockRepositoryImplTest {

    @Autowired
    private StockRepositoryImpl stockRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    private List<StockEntity> testStocks;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        stockJpaRepository.deleteAll();

        // 테스트 데이터 생성
        createTestData();
    }

    private void createTestData() {
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
        List<String> skuIds = Arrays.asList("AP-IP15-PRO", "SM-S24-ULTRA", "LG-GRAM-17");

        // when
        List<EnoughStockDTO> availableStocks = stockRepository.findSkuIdAndAvailableEa(skuIds);

        // then
        assertThat(availableStocks).hasSize(3);

    }

    @Test
    @DisplayName("주문 시 FIFO 방식으로 재고를 감소시킬 수 있다")
    void updateStockDecreaseFifo() {
        // given
        String skuId = "AP-IP15-PRO";
        long orderId = 9999L;
        long purchaseQuantity = 3;

        List<EnoughStockDTO> beforePurchase = stockRepository.findSkuIdAndAvailableEa(List.of(skuId));

        // when
        int updatedRows = stockRepository.updateStockDecreaseFifo(orderId, skuId, purchaseQuantity);

        // then
        assertThat(updatedRows).isEqualTo(purchaseQuantity);
    }

    @Test
    @DisplayName("재고가 부족한 경우 실제로 가용한 만큼만 업데이트된다")
    void updateStockDecreaseFifoWithInsufficientStock() {
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
    }
}