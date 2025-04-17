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
        stockJpaRepository.deleteAll();

        createTestData();
    }

    private void createTestData() {
        testStocks = new ArrayList<>();
        createStocks("AP-IP15-PRO", CategoryEnum.APPLE, 10, 3);
        createStocks("AP-MB-AIR-M2", CategoryEnum.APPLE, 5, 1);
        createStocks("SM-S24-ULTRA", CategoryEnum.SAMSUNG, 15, 5);
        createStocks("SM-TAB-S9", CategoryEnum.SAMSUNG, 7, 2);
        createStocks("LG-GRAM-17", CategoryEnum.LG, 6, 0);
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