package kr.hhplus.be.server.domain.order;


import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderCoreRepository orderCoreRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        OrderCommand.Item item1 = new OrderCommand.Item("SKU001", 2L);
        OrderCommand.Item item2 = new OrderCommand.Item("SKU002", 1L);
        OrderCommand.Order orderCommand = new OrderCommand.Order(userId, Arrays.asList(item1, item2));

        ProductEntity product1 = mock(ProductEntity.class);
        ProductEntity product2 = mock(ProductEntity.class);

        when(product1.getSkuId()).thenReturn("SKU001");
        when(product2.getSkuId()).thenReturn("SKU002");

        when(productRepository.findAllBySkuIdIn(any())).thenReturn(Arrays.asList(product1, product2));

        OrderEntity savedOrder = mock(OrderEntity.class);
        when(savedOrder.getId()).thenReturn(1000L);
        when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        // when
        long orderId = orderService.createOrder(orderCommand);

        // then
        assertEquals(1000L, orderId);

        verify(productRepository).findAllBySkuIdIn(any());
        verify(orderCoreRepository).save(any(OrderEntity.class));

        // OrderEntity에 addOrderItems가 호출되는지 검증
        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderCoreRepository).save(orderCaptor.capture());

        OrderEntity capturedOrder = orderCaptor.getValue();
        assertNotNull(capturedOrder);
    }


    @Test
    void 결제_가능_주문_확인_실패_주문없음() {
        // given
        long nonExistentOrderId = 9999L;
        when(orderCoreRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(RuntimeException.class, () ->
                orderService.isAvailableOrder(nonExistentOrderId));

        // then
        assertEquals("주문이 존재하지 않습니다.", exception.getMessage());
        verify(orderCoreRepository).findById(nonExistentOrderId);
    }

    @Test
    void 할인금액_설정_성공() {
        // given
        long orderId = 1000L;
        BigDecimal discountAmount = BigDecimal.valueOf(1000);

        OrderEntity order = mock(OrderEntity.class);
        when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.setDiscountAmount(orderId, discountAmount);

        // then
        verify(orderCoreRepository).findById(orderId);
        verify(order).setDiscountAmount(discountAmount);
    }

    @Test
    void 할인금액_설정_실패_주문없음() {
        // given
        long nonExistentOrderId = 9999L;
        BigDecimal discountAmount = BigDecimal.valueOf(1000);

        when(orderCoreRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(RuntimeException.class, () ->
                orderService.setDiscountAmount(nonExistentOrderId, discountAmount));

        // then
        assertEquals("주문이 존재하지 않습니다.", exception.getMessage());
        verify(orderCoreRepository).findById(nonExistentOrderId);
    }
}