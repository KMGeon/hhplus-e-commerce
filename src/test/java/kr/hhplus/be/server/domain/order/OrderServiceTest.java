package kr.hhplus.be.server.domain.order;


import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderCoreRepository orderCoreRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문_생성_성공() {
        Long userId = 1L;
        List<OrderCommand.Product> products = Arrays.asList(
                new OrderCommand.Product("SKU001", 2L, 1000L),
                new OrderCommand.Product("SKU002", 3L, 2000L)
        );

        OrderEntity mockEntity = mock(OrderEntity.class);
        when(mockEntity.getId()).thenReturn(100L);
        when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(mockEntity);

        Long orderId = orderService.createOrder(userId, products);

        assertThat(orderId).isEqualTo(100L);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderCoreRepository).save(orderCaptor.capture());

        OrderEntity capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getUserId()).isEqualTo(userId);
        assertThat(capturedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(capturedOrder.getOrderProducts()).hasSize(2);
    }

    @Test
    void 주문_결제_가능_상태_확인() {
        Long orderId = 100L;
        OrderEntity mockOrder = mock(OrderEntity.class);
        when(mockOrder.getId()).thenReturn(orderId);
        when(mockOrder.getTotalPrice()).thenReturn(BigDecimal.valueOf(5000));

        when(orderCoreRepository.findById(orderId)).thenReturn(mockOrder);

        OrderInfo.OrderPaymentInfo paymentInfo = orderService.isAvailableOrder(orderId);

        assertThat(paymentInfo.orderId()).isEqualTo(orderId);
        verify(mockOrder).isAvailablePaymentState();
    }

    @Test
    void 주문_취소_처리() {
        Long orderId = 100L;
        OrderEntity mockOrder = mock(OrderEntity.class);
        when(orderCoreRepository.findById(orderId)).thenReturn(mockOrder);

        orderService.restoreOrderStatusCancel(orderId);

        verify(mockOrder).cancel();
    }

    @Test
    void 할인_적용_후_주문_완료_처리() {
        Long orderId = 100L;
        BigDecimal discountAmount = BigDecimal.valueOf(1000);
        BigDecimal finalAmount = BigDecimal.valueOf(4000);

        OrderEntity mockOrder = mock(OrderEntity.class);
        when(mockOrder.getFinalAmount()).thenReturn(finalAmount);
        when(orderCoreRepository.findById(orderId)).thenReturn(mockOrder);

        BigDecimal result = orderService.applyToDisCount(orderId, discountAmount);

        assertThat(result).isEqualTo(finalAmount);
        verify(mockOrder).applyDiscount(discountAmount);
        verify(mockOrder).complete();
    }

    @Test
    void 인기_상품_조회() {
        String startPath = "0418";
        String endPath = "0421";

        List<HotProductDTO> mockProducts = Arrays.asList(
                createHotProductDTO("SKU001", 10L),
                createHotProductDTO("SKU002", 8L)
        );

        when(orderCoreRepository.findHotProducts(any(), any())).thenReturn(mockProducts);

        List<HotProductDTO> result = orderService.getHotProducts();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(mockProducts);

        ArgumentCaptor<String> startPathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> endPathCaptor = ArgumentCaptor.forClass(String.class);
        verify(orderCoreRepository).findHotProducts(startPathCaptor.capture(), endPathCaptor.capture());
    }

    private HotProductDTO createHotProductDTO(String skuId, Long orderCount) {
        return new HotProductDTO() {
            @Override
            public String getSkuId() {
                return skuId;
            }

            @Override
            public Long getOrderCount() {
                return orderCount;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                HotProductDTO other = (HotProductDTO) obj;
                return getSkuId().equals(other.getSkuId()) &&
                        getOrderCount().equals(other.getOrderCount());
            }
        };
    }
}