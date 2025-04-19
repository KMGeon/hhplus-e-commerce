package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrderFacadeService orderFacadeService;

    private OrderCriteria.Order validOrderCriteria;
    private StockCommand.Order stockCommand;

    @BeforeEach
    void setUp() {
        validOrderCriteria = new OrderCriteria.Order(
                1L, // userId
                List.of(
                        new OrderCriteria.Item("SKU001", 2),
                        new OrderCriteria.Item("SKU002", 1)
                )
        );

        stockCommand = new StockCommand.Order(
                List.of(
                        new StockCommand.Order.Item("SKU001", 2L),
                        new StockCommand.Order.Item("SKU002", 1L)
                )
        );
    }

    @Test
    void 주문_파사드_순서_테스트() {
        // given
        when(userService.getUserId(anyLong())).thenReturn(1L);
        when(orderService.createOrder(any())).thenReturn(1L);
        when(stockService.decreaseStock(anyLong(), any())).thenReturn(1);

        // when
        orderFacadeService.createOrder(validOrderCriteria);

        // then
        InOrder inOrder = inOrder(userService, productService, stockService, orderService);
        inOrder.verify(userService).getUserId(1L);
        inOrder.verify(productService).validateAllSkuIds(anyList());
        inOrder.verify(stockService).isEnoughStock(any());
        inOrder.verify(orderService).createOrder(any());
        inOrder.verify(stockService).decreaseStock(1L, stockCommand);
    }
}