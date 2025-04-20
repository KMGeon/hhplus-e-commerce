package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


class StockInventoryTest {


    @Test
    void 주문_정보로부터_재고_인벤토리_생성() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 5L);
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));
        // when
        StockInventory inventory = StockInventory.fromStock(stockCommand);

        // then
        Map<String, Long> skuStocks = inventory.getSkuStocks();
        assertThat(skuStocks).hasSize(2);
        assertThat(skuStocks.get("SKU001")).isEqualTo(3L);
        assertThat(skuStocks.get("SKU002")).isEqualTo(5L);
    }

    @Test
    void 재고_데이터로부터_인벤토리_생성() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 20L, 2000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);

        // when
        StockInventory inventory = StockInventory.fromStockData(stockList);

        // then
        Map<String, Long> skuStocks = inventory.getSkuStocks();
        assertThat(skuStocks).hasSize(2);
        assertThat(skuStocks.get("SKU001")).isEqualTo(10L);
        assertThat(skuStocks.get("SKU002")).isEqualTo(20L);
    }

    @Test
    void 충분한_재고_검증_성공() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 20L, 2000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);
        StockInventory availableInventory = StockInventory.fromStockData(stockList);

        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 5L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 15L);
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));
        StockInventory requestInventory = StockInventory.fromStock(stockCommand);

        // when
        // then
        availableInventory.validateAgainst(requestInventory);
    }

    @Test
    void 재고_부족_검증_실패() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 5L, 2000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);
        StockInventory availableInventory = StockInventory.fromStockData(stockList);

        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 5L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 10L);
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));
        StockInventory requestInventory = StockInventory.fromStock(stockCommand);

        // when
        // then
        assertThatThrownBy(() -> availableInventory.validateAgainst(requestInventory))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("재고 부족")
                .hasMessageContaining("SKU002")
                .hasMessageContaining("요청: 10개, 가용: 5개");
    }

    @Test
    void 가용_수량_조회() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 20L, 2000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);
        StockInventory inventory = StockInventory.fromStockData(stockList);

        // when
        // then
        assertThat(inventory.getAvailableQuantity("SKU001")).isEqualTo(10L);
        assertThat(inventory.getAvailableQuantity("SKU002")).isEqualTo(20L);
        assertThat(inventory.getAvailableQuantity("SKU003")).isEqualTo(0L);
    }

    @Test
    void 충분한_재고_여부_확인() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1);
        StockInventory inventory = StockInventory.fromStockData(stockList);

        // when
        // then
        assertTrue(inventory.isEnoughStock("SKU001", 10L));
        assertTrue(inventory.isEnoughStock("SKU001", 5L));
        assertFalse(inventory.isEnoughStock("SKU001", 15L));
        assertFalse(inventory.isEnoughStock("SKU002", 1L));
    }

    @Test
    void SKU_목록_조회() {
        // given
        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 20L, 2000L);
        EnoughStockDTO stock3 = createStockDTO("SKU003", 30L, 3000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2, stock3);
        StockInventory inventory = StockInventory.fromStockData(stockList);

        // when
        List<String> skuIds = inventory.getSkuIds();

        // then
        assertThat(skuIds).hasSize(3);
        assertThat(skuIds).containsExactlyInAnyOrder("SKU001", "SKU002", "SKU003");
    }

    private EnoughStockDTO createStockDTO(String skuId, Long ea, Long unitPrice) {
        return new EnoughStockDTO() {
            @Override
            public String getSkuId() {
                return skuId;
            }

            @Override
            public Long getEa() {
                return ea;
            }

            @Override
            public Long getUnitPrice() {
                return unitPrice;
            }
        };
    }
}