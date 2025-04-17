package kr.hhplus.be.server.domain.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTest {

        @Test
        void 주문을_정상적으로_생성하고_저장() {
            // given
            Long userId = 1L;
            List<OrderCommand.Item> items = List.of(
                    new OrderCommand.Item(1L, 2L, 1000L),
                    new OrderCommand.Item(2L, 3L, 2000L)
            );
            OrderCommand.Order command = new OrderCommand.Order(userId, items);
            LocalDateTime now = LocalDateTime.of(2023, 5, 10, 14, 0);

            OrderEntity savedOrder = OrderEntity.builder()
                    .id(1L)
                    .userId(userId)
                    .status(OrderStatus.PENDING)
                    .totalPrice(8000L) // (2*1000) + (3*2000) = 8000
                    .totalEa(5L) // 2 + 3 = 5
                    .discountPrice(0L)
                    .paymentPrice(0L)
                    .expireTime(now.plusMinutes(10))
                    .build();

            when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
            when(orderCoreRepository.save(any(OrderItemEntity.class))).thenReturn(null);

            // when
            OrderInfo result = orderService.createOrder(command, now);

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(OrderStatus.PENDING.getDescription());
            assertThat(result.totalPrice()).isEqualTo(8000L);
            assertThat(result.totalEa()).isEqualTo(5L);

            ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderCoreRepository, times(1)).save(orderCaptor.capture());
            OrderEntity capturedOrder = orderCaptor.getValue();

            assertThat(capturedOrder.getUserId()).isEqualTo(userId);
            assertThat(capturedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(capturedOrder.getTotalPrice()).isEqualTo(8000L);
            assertThat(capturedOrder.getTotalEa()).isEqualTo(5L);
            assertThat(capturedOrder.getExpireTime()).isEqualTo(now.plusMinutes(10));

            ArgumentCaptor<OrderItemEntity> orderItemCaptor = ArgumentCaptor.forClass(OrderItemEntity.class);
            verify(orderCoreRepository, times(2)).save(orderItemCaptor.capture());
            List<OrderItemEntity> capturedOrderItems = orderItemCaptor.getAllValues();

            assertThat(capturedOrderItems).hasSize(2);

            OrderItemEntity firstItem = capturedOrderItems.get(0);
            assertThat(firstItem.getProductId()).isEqualTo(1L);
            assertThat(firstItem.getEa()).isEqualTo(2L);
            assertThat(firstItem.getPrice()).isEqualTo(1000L);
            assertThat(firstItem.getOrder()).isEqualTo(capturedOrder);

            OrderItemEntity secondItem = capturedOrderItems.get(1);
            assertThat(secondItem.getProductId()).isEqualTo(2L);
            assertThat(secondItem.getEa()).isEqualTo(3L);
            assertThat(secondItem.getPrice()).isEqualTo(2000L);
            assertThat(secondItem.getOrder()).isEqualTo(capturedOrder);
        }

        @Test
        @DisplayName("""
                Command에 로직이 있는데 가독성을 위해서 서비스로 분리해서 테스트 진행
                """)
        void 주문_항목이_없는_경우에도_주문을_하면_안된다() {
            // given
            Long userId = 1L;
            List<OrderCommand.Item> items = List.of();

            // when
            // then
            Assertions.assertThatThrownBy(() -> new OrderCommand.Order(userId, items))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주문 아이템은 최소 1개 이상이어야 합니다.");
        }

        @Test
        void 총주문_금액과_수량이_계산된_결과를_반환한다() {
            // given
            Long userId = 1L;
            List<OrderCommand.Item> items = List.of(
                    new OrderCommand.Item(1L, 5L, 1000L),  // 5개 * 1000원 = 5000원
                    new OrderCommand.Item(2L, 3L, 2000L),  // 3개 * 2000원 = 6000원
                    new OrderCommand.Item(3L, 1L, 10000L)  // 1개 * 10000원 = 10000원
            );
            OrderCommand.Order command = new OrderCommand.Order(userId, items);
            LocalDateTime now = LocalDateTime.of(2023, 5, 10, 14, 0);

            OrderEntity savedOrder = OrderEntity.builder()
                    .id(1L)
                    .userId(userId)
                    .status(OrderStatus.PENDING)
                    .totalPrice(21000L) // 5000 + 6000 + 10000 = 21000
                    .totalEa(9L) // 5 + 3 + 1 = 9
                    .discountPrice(0L)
                    .paymentPrice(0L)
                    .expireTime(now.plusMinutes(10))
                    .build();

            when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
            when(orderCoreRepository.save(any(OrderItemEntity.class))).thenReturn(null);

            // when
            OrderInfo result = orderService.createOrder(command, now);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalPrice()).isEqualTo(21000L);
            assertThat(result.totalEa()).isEqualTo(9L);

            ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
            verify(orderCoreRepository, times(1)).save(orderCaptor.capture());
            OrderEntity capturedOrder = orderCaptor.getValue();

            assertThat(capturedOrder.getTotalPrice()).isEqualTo(21000L);
            assertThat(capturedOrder.getTotalEa()).isEqualTo(9L);
        }
    }
}