package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.stock.projection.EnoughStockDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Test
    void 재고_충분함_검증_성공() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 5L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 3L);
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2));

        EnoughStockDTO stock1 = createEnoughStockDTO("SKU001", 10);
        EnoughStockDTO stock2 = createEnoughStockDTO("SKU002", 5);

        when(stockRepository.findSkuIdAndAvailableEa(any())).thenReturn(List.of(stock1, stock2));

        // when
        stockService.isEnoughStock(stockCommand);

        // then
        verify(stockRepository).findSkuIdAndAvailableEa(any());
    }

    @Test
    void 동일_상품_다수_주문_재고_검증_성공() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU001", 2L);
        StockCommand.Order.Item item3 = new StockCommand.Order.Item("SKU002", 3L);
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2, item3));

        EnoughStockDTO stock1 = createEnoughStockDTO("SKU001", 10);
        EnoughStockDTO stock2 = createEnoughStockDTO("SKU002", 5);

        when(stockRepository.findSkuIdAndAvailableEa(any())).thenReturn(List.of(stock1, stock2));

        // when
        stockService.isEnoughStock(stockCommand);

        // then
        verify(stockRepository).findSkuIdAndAvailableEa(any());
    }

    @Test
    void 재고_부족시_예외_발생() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 15L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 3L);
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2));

        EnoughStockDTO stock1 = createEnoughStockDTO("SKU001", 10);
        EnoughStockDTO stock2 = createEnoughStockDTO("SKU002", 5);

        when(stockRepository.findSkuIdAndAvailableEa(any())).thenReturn(List.of(stock1, stock2));

        // when
        Exception exception = assertThrows(RuntimeException.class, () -> stockService.isEnoughStock(stockCommand));

        // then
        assertTrue(exception.getMessage().contains("SKU: SKU001의 재고가 부족합니다"));
        verify(stockRepository).findSkuIdAndAvailableEa(any());
    }

    @Test
    void 존재하지_않는_상품_주문시_예외_발생() {
        // given
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 5L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU003", 3L); // SKU003 is not in stock
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2));

        EnoughStockDTO stock1 = createEnoughStockDTO("SKU001", 10);
        EnoughStockDTO stock2 = createEnoughStockDTO("SKU002", 5);

        when(stockRepository.findSkuIdAndAvailableEa(any())).thenReturn(List.of(stock1, stock2));

        // when
        Exception exception = assertThrows(RuntimeException.class, () -> stockService.isEnoughStock(stockCommand));

        // then
        assertTrue(exception.getMessage().contains("SKU: SKU003의 재고가 부족합니다"));
        assertTrue(exception.getMessage().contains("현재재고: 0"));
        verify(stockRepository).findSkuIdAndAvailableEa(any());
    }

    @Test
    void 재고_감소_성공() {
        // given
        long orderId = 12345L;
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 2L);
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2));

        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU001"), eq(3L))).thenReturn(3);
        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU002"), eq(2L))).thenReturn(2);

        // when
        int result = stockService.decreaseStock(orderId, stockCommand);

        // then
        assertEquals(5, result);
        verify(stockRepository).updateStockDecreaseFifo(orderId, "SKU001", 3L);
        verify(stockRepository).updateStockDecreaseFifo(orderId, "SKU002", 2L);
    }

    @Test
    void 재고_일부만_감소_성공() {
        // given
        long orderId = 12345L;
        StockCommand.Order.Item item1 = new StockCommand.Order.Item("SKU001", 3L);
        StockCommand.Order.Item item2 = new StockCommand.Order.Item("SKU002", 2L);
        StockCommand.Order stockCommand = new StockCommand.Order(List.of(item1, item2));

        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU001"), eq(3L))).thenReturn(2); // Only 2 updated
        when(stockRepository.updateStockDecreaseFifo(eq(orderId), eq("SKU002"), eq(2L))).thenReturn(2);

        // when
        int result = stockService.decreaseStock(orderId, stockCommand);

        // then
        assertEquals(4, result); // 2 + 2 = 4
        verify(stockRepository).updateStockDecreaseFifo(orderId, "SKU001", 3L);
        verify(stockRepository).updateStockDecreaseFifo(orderId, "SKU002", 2L);
    }

    private EnoughStockDTO createEnoughStockDTO(String skuId, long ea) {
        return new EnoughStockDTO() {
            @Override
            public String getSkuId() {
                return skuId;
            }

            @Override
            public Long getEa() {
                return ea;
            }
        };
    }
}