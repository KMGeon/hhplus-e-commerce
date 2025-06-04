package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.stock.StockInfo;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

    @Test
    void 주문_생성_성공() {
        Long userId = 1L;
        List<OrderCriteria.Item> items = Arrays.asList(
                new OrderCriteria.Item("SKU001", 2L),
                new OrderCriteria.Item("SKU002", 3L)
        );
        OrderCriteria.Order criteria = new OrderCriteria.Order(userId, items);

        UserInfo.User user = new UserInfo.User(userId, 1000L);
        when(userService.getUser(userId)).thenReturn(user);

        doNothing().when(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));

        List<StockInfo.Stock> stocks = Arrays.asList(
                new StockInfo.Stock("SKU001", 10L, 1000L),
                new StockInfo.Stock("SKU002", 20L, 2000L)
        );
        when(stockService.checkEaAndProductInfo(any(StockCommand.Order.class))).thenReturn(stocks);

        Long orderId = 100L;
        when(orderService.createOrder(eq(userId), any())).thenReturn(orderId);
        doNothing().when(stockService).decreaseStockLock(anyLong(), any(StockCommand.Order.class));

        orderFacadeService.createOrder(criteria);


        InOrder inOrder = inOrder(userService, productService, stockService, orderService);
        inOrder.verify(userService).getUser(userId);
        inOrder.verify(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));
        inOrder.verify(stockService).checkEaAndProductInfo(any(StockCommand.Order.class));
        inOrder.verify(orderService).createOrder(eq(userId), any());
        inOrder.verify(stockService).decreaseStockLock(eq(orderId), any(StockCommand.Order.class));
    }

    @Test
    void 사용자_존재하지_않음_예외_발생() {
        Long userId = 999L;
        List<OrderCriteria.Item> items = Arrays.asList(
                new OrderCriteria.Item("SKU001", 2L)
        );
        OrderCriteria.Order criteria = new OrderCriteria.Order(userId, items);

        when(userService.getUser(userId)).thenThrow(new RuntimeException("해당 유저가 존재하지 않습니다"));

        assertThatThrownBy(() -> orderFacadeService.createOrder(criteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");

        verify(userService).getUser(userId);
        verify(productService, never()).checkProductSkuIds(any());
        verify(stockService, never()).checkEaAndProductInfo(any());
        verify(orderService, never()).createOrder(any(), any());
        verify(stockService, never()).decreaseStockLock(any(), any());
    }

    @Test
    void 상품_존재하지_않음_예외_발생() {
        Long userId = 1L;
        List<OrderCriteria.Item> items = Arrays.asList(
                new OrderCriteria.Item("INVALID_SKU", 2L)
        );
        OrderCriteria.Order criteria = new OrderCriteria.Order(userId, items);

        UserInfo.User user = new UserInfo.User(userId, 1000L);
        when(userService.getUser(userId)).thenReturn(user);

        doThrow(new RuntimeException("잘못된 SKU ID가 포함되어 있습니다"))
                .when(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));

        assertThatThrownBy(() -> orderFacadeService.createOrder(criteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("잘못된 SKU ID가 포함되어 있습니다");

        InOrder inOrder = inOrder(userService, productService);
        inOrder.verify(userService).getUser(userId);
        inOrder.verify(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));

        verify(stockService, never()).checkEaAndProductInfo(any());
        verify(orderService, never()).createOrder(any(), any());
        verify(stockService, never()).decreaseStockLock(any(), any());
    }

    @Test
    void 재고_부족_예외_발생() {
        Long userId = 1L;
        List<OrderCriteria.Item> items = Arrays.asList(
                new OrderCriteria.Item("SKU001", 100L) // 재고보다 많은 수량
        );
        OrderCriteria.Order criteria = new OrderCriteria.Order(userId, items);

        UserInfo.User user = new UserInfo.User(userId, 1000L);
        when(userService.getUser(userId)).thenReturn(user);

        doNothing().when(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));

        when(stockService.checkEaAndProductInfo(any(StockCommand.Order.class)))
                .thenThrow(new RuntimeException("재고 부족: SKU SKU001 (요청: 100개, 가용: 10개)"));

        assertThatThrownBy(() -> orderFacadeService.createOrder(criteria))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("재고 부족");

        InOrder inOrder = inOrder(userService, productService, stockService);
        inOrder.verify(userService).getUser(userId);
        inOrder.verify(productService).checkProductSkuIds(any(OrderCriteria.Item[].class));
        inOrder.verify(stockService).checkEaAndProductInfo(any(StockCommand.Order.class));

        verify(orderService, never()).createOrder(any(), any());
        verify(stockService, never()).decreaseStockLock(any(), any());
    }
}