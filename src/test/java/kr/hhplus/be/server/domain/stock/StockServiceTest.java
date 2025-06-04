package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    void 충분한_재고_확인_성공() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 5L);
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));

        List<String> skuIds = Arrays.asList("SKU001", "SKU002");

        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 20L, 2000L);
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);

        when(stockRepository.findSkuIdAndAvailableEa(anyList())).thenReturn(stockList);

        // when
        List<StockInfo.Stock> result = stockService.checkEaAndProductInfo(stockCommand);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).skuId()).isEqualTo("SKU001");
        assertThat(result.get(0).ea()).isEqualTo(10L);
        assertThat(result.get(0).unitPrice()).isEqualTo(1000L);

        assertThat(result.get(1).skuId()).isEqualTo("SKU002");
        assertThat(result.get(1).ea()).isEqualTo(20L);
        assertThat(result.get(1).unitPrice()).isEqualTo(2000L);

        verify(stockRepository, times(1)).findSkuIdAndAvailableEa(anyList());
    }

    @Test
    void 재고_부족_시_예외_발생() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 15L); // 재고 초과
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));

        List<String> skuIds = Arrays.asList("SKU001", "SKU002");

        EnoughStockDTO stock1 = createStockDTO("SKU001", 10L, 1000L);
        EnoughStockDTO stock2 = createStockDTO("SKU002", 10L, 2000L); // 요청 15개보다 적은 10개
        List<EnoughStockDTO> stockList = Arrays.asList(stock1, stock2);

        when(stockRepository.findSkuIdAndAvailableEa(anyList())).thenReturn(stockList);

        // when
        // then
        assertThatThrownBy(() -> stockService.checkEaAndProductInfo(stockCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("재고 부족")
                .hasMessageContaining("SKU002")
                .hasMessageContaining("요청: 15개, 가용: 10개");

        verify(stockRepository, times(1)).findSkuIdAndAvailableEa(anyList());
    }

    @Test
    void 재고_감소_처리_성공() {
        // given
        Long orderId = 1000L;
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 5L);
        StockCommand.Order stockCommand = new StockCommand.Order(Arrays.asList(item1, item2));

        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU001"), eq(3L))).thenReturn(3);
        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU002"), eq(5L))).thenReturn(5);

        // when
        stockService.decreaseStockLock(orderId, stockCommand);

        // then
        verify(stockRepository, times(1)).updateStockDecreaseFifo(eq(orderId), eq("SKU001"), eq(3L));
        verify(stockRepository, times(1)).updateStockDecreaseFifo(eq(orderId), eq("SKU002"), eq(5L));
    }

    @Test
    void 재고_복원_처리_성공() {
        // given
        Long orderId = 1000L;
        doNothing().when(stockRepository).restoreStockByOrderIds(List.of(orderId));

        // when
        stockService.restoreStock(List.of(orderId));

        // then
        verify(stockRepository, times(1)).restoreStockByOrderIds(List.of(orderId));
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