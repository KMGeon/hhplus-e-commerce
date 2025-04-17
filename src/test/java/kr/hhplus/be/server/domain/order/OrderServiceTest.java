//package kr.hhplus.be.server.domain.order;
//
//import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
//import kr.hhplus.be.server.domain.user.UserEntity;
//import kr.hhplus.be.server.domain.user.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//    @Mock
//    private OrderCoreRepository orderCoreRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    @Nested
//    @DisplayName("주문 생성 테스트")
//    class CreateOrderTest {
//
//        @Test
//        void 주문을_정상적으로_생성하고_저장() {
//            // given
//            Long userId = 1L;
//            List<OrderCommand.Item> items = List.of(
//                    new OrderCommand.Item(1L, 2L, 1000L),
//                    new OrderCommand.Item(2L, 3L, 2000L)
//            );
//            OrderCommand.Order command = new OrderCommand.Order(userId, items);
//            LocalDateTime now = LocalDateTime.of(2023, 5, 10, 14, 0);
//
//            OrderEntity savedOrder = OrderEntity.builder()
//                    .id(1L)
//                    .status(OrderStatus.CONFIRMED)
//                    .totalPrice(8000L)
//                    .totalEa(5L)
//                    .discountPrice(0L)
//                    .paymentPrice(0L)
//                    .expireTime(now.plusMinutes(10))
//                    .build();
//
//            UserEntity user = UserEntity.createNewUser();
//            when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
//            when(userRepository.findById(any())).thenReturn(Optional.of(user));
//
//            // when
//            OrderInfo.OrderCreateInfo result = orderService.createOrder(command, now);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED.getDescription());
//            assertThat(result.totalPrice()).isEqualTo(8000L);
//            assertThat(result.totalEa()).isEqualTo(5L);
//        }
//
//        @Test
//        @DisplayName("""
//                Command에 로직이 있는데 가독성을 위해서 서비스로 분리해서 테스트 진행
//                """)
//        void 주문_항목이_없는_경우에도_주문을_하면_안된다() {
//            // given
//            Long userId = 1L;
//            List<OrderCommand.Item> items = List.of();
//
//            // when
//            // then
//            Assertions.assertThatThrownBy(() -> new OrderCommand.Order(userId, items))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessage("주문 아이템은 최소 1개 이상이어야 합니다.");
//        }
//
//        @Test
//        void 총주문_금액과_수량이_계산된_결과를_반환한다() {
//            // given
//            Long userId = 1L;
//            List<OrderCommand.Item> items = List.of(
//                    new OrderCommand.Item(1L, 5L, 1000L),  // 5개 * 1000원 = 5000원
//                    new OrderCommand.Item(2L, 3L, 2000L),  // 3개 * 2000원 = 6000원
//                    new OrderCommand.Item(3L, 1L, 10000L)  // 1개 * 10000원 = 10000원
//            );
//            OrderCommand.Order command = new OrderCommand.Order(userId, items);
//            LocalDateTime now = LocalDateTime.of(2023, 5, 10, 14, 0);
//
//            OrderEntity savedOrder = OrderEntity.builder()
//                    .id(1L)
//                    .status(OrderStatus.CONFIRMED)
//                    .totalPrice(21000L) // 5000 + 6000 + 10000 = 21000
//                    .totalEa(9L) // 5 + 3 + 1 = 9
//                    .discountPrice(0L)
//                    .paymentPrice(0L)
//                    .expireTime(now.plusMinutes(10))
//                    .build();
//
//            UserEntity user = UserEntity.createNewUser();
//            when(orderCoreRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
//            when(userRepository.findById(any())).thenReturn(Optional.of(user));
//
//            // when
//            OrderInfo.OrderCreateInfo result = orderService.createOrder(command, now);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.totalPrice()).isEqualTo(21000L);
//            assertThat(result.totalEa()).isEqualTo(9L);
//
//            ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
//            verify(orderCoreRepository, times(1)).save(orderCaptor.capture());
//            OrderEntity capturedOrder = orderCaptor.getValue();
//
//            assertThat(capturedOrder.getTotalPrice()).isEqualTo(21000L);
//            assertThat(capturedOrder.getTotalEa()).isEqualTo(9L);
//        }
//
//        @Test
//        void 사용자가_존재하지_않는_경우_예외가_발생한다() {
//            // given
//            Long userId = 999L;
//            List<OrderCommand.Item> items = List.of(
//                    new OrderCommand.Item(1L, 2L, 1000L)
//            );
//            OrderCommand.Order command = new OrderCommand.Order(userId, items);
//            LocalDateTime now = LocalDateTime.of(2023, 5, 10, 14, 0);
//
//            when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> orderService.createOrder(command, now))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("해당 유저가 존재하지 않습니다.");
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 유효성 검증 테스트")
//    class ValidOrderTest {
//
//        @Test
//        void 유효한_주문인_경우_true를_반환한다() {
//            // given
//            Long orderId = 1L;
//            OrderEntity order = OrderEntity.builder()
//                    .id(orderId)
//                    .status(OrderStatus.CONFIRMED)
//                    .expireTime(LocalDateTime.now().plusMinutes(5))
//                    .build();
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));
//
//            // when
//            boolean isValid = orderService.isValidOrder(orderId);
//
//            // then
//            assertThat(isValid).isTrue();
//        }
//
//        @Test
//        void 주문이_존재하지_않는_경우_false를_반환한다() {
//            // given
//            Long orderId = 999L;
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.empty());
//
//            // when
//            boolean isValid = orderService.isValidOrder(orderId);
//
//            // then
//            assertThat(isValid).isFalse();
//        }
//
//        @Test
//        void 주문이_만료된_경우_false를_반환한다() {
//            // given
//            Long orderId = 1L;
//            OrderEntity expiredOrder = OrderEntity.builder()
//                    .id(orderId)
//                    .status(OrderStatus.CONFIRMED)
//                    .expireTime(LocalDateTime.now().minusMinutes(5))
//                    .build();
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(expiredOrder));
//
//            // when
//            boolean isValid = orderService.isValidOrder(orderId);
//
//            // then
//            assertThat(isValid).isFalse();
//        }
//    }
//
//    @Nested
//    @DisplayName("쿠폰 적용 테스트")
//    class ApplyCouponTest {
//
//        @Test
//        void 쿠폰을_정상적으로_적용한다() {
//            // given
//            Long orderId = 1L;
//            Long userId = 1L;
//            Long couponId = 10L;
//            long discountRate = 10; // 10% 할인
//
//            OrderEntity order = mock(OrderEntity.class);
//            UserEntity user = mock(UserEntity.class);
//            UserCouponEntity userCoupon = mock(UserCouponEntity.class);
//            UserCouponEntity coupon = mock(UserCouponEntity.class);
//
//            List<UserCouponEntity> userCoupons = new ArrayList<>();
//            userCoupons.add(userCoupon);
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserCoupons()).thenReturn(userCoupons);
////            when(userCoupon.getCoupon()).thenReturn(coupon);
////            when(coupon.getId()).thenReturn(couponId);
////            when(coupon.getDiscountRate()).thenReturn(discountRate);
//            when(userCoupon.isAvailable()).thenReturn(true);
//
//            // when
//            orderService.applyCoupon(orderId, couponId, userId);
//
//            // then
//            verify(order).applyCoupon(couponId, discountRate);
//            verify(userCoupon).use();
//            verify(orderCoreRepository).save(order);
//        }
//
//        @Test
//        void 주문이_존재하지_않는_경우_예외가_발생한다() {
//            // given
//            Long orderId = 999L;
//            Long userId = 1L;
//            Long couponId = 10L;
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> orderService.applyCoupon(orderId, couponId, userId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("주문이 존재하지 않습니다.");
//        }
//
//        @Test
//        void 사용자가_존재하지_않는_경우_예외가_발생한다() {
//            // given
//            Long orderId = 1L;
//            Long userId = 999L;
//            Long couponId = 10L;
//
//            OrderEntity order = mock(OrderEntity.class);
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));
//            when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> orderService.applyCoupon(orderId, couponId, userId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("해당 유저가 존재하지 않습니다.");
//        }
//
//        @Test
//        void 사용_가능한_쿠폰이_없는_경우_예외가_발생한다() {
//            // given
//            Long orderId = 1L;
//            Long userId = 1L;
//            Long couponId = 10L;
//
//            OrderEntity order = mock(OrderEntity.class);
//            UserEntity user = mock(UserEntity.class);
//            List<UserCouponEntity> userCoupons = new ArrayList<>();
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserCoupons()).thenReturn(userCoupons);
//
//            // when & then
//            assertThatThrownBy(() -> orderService.applyCoupon(orderId, couponId, userId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("사용 가능한 쿠폰이 존재하지 않습니다.");
//        }
//
//        @Test
//        void 유효하지_않은_쿠폰인_경우_예외가_발생한다() {
//            // given
//            Long orderId = 1L;
//            Long userId = 1L;
//            Long couponId = 10L;
//            Long differentCouponId = 20L;
//
//            OrderEntity order = mock(OrderEntity.class);
//            UserEntity user = mock(UserEntity.class);
//            UserCouponEntity userCoupon = mock(UserCouponEntity.class);
//
//            List<UserCouponEntity> userCoupons = new ArrayList<>();
//            userCoupons.add(userCoupon);
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(order));
//            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//            when(user.getUserCoupons()).thenReturn(userCoupons);
//            when(userCoupon.isAvailable()).thenReturn(true);
//
//            // when & then
//            assertThatThrownBy(() -> orderService.applyCoupon(orderId, couponId, userId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("사용 가능한 쿠폰이 존재하지 않습니다.");
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 조회 테스트")
//    class GetOrderTest {
//
//        @Test
//        void 주문을_정상적으로_조회한다() {
//            // given
//            Long orderId = 1L;
//            OrderEntity mockOrder = OrderEntity.builder()
//                    .id(orderId)
//                    .status(OrderStatus.CONFIRMED)
//                    .build();
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
//
//            // when
//            OrderEntity result = orderService.getOrder(orderId);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.getId()).isEqualTo(orderId);
//            assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
//        }
//
//        @Test
//        void 주문이_존재하지_않는_경우_예외가_발생한다() {
//            // given
//            Long orderId = 999L;
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> orderService.getOrder(orderId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("주문이 존재하지 않습니다.");
//        }
//    }
//
//    @Nested
//    @DisplayName("결제 완료 테스트")
//    class CompletePaymentTest {
//
//        @Test
//        void 결제를_정상적으로_완료한다() {
//            // given
//            Long orderId = 1L;
//            OrderEntity mockOrder = mock(OrderEntity.class);
//
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
//
//            // when
//            orderService.completePayment(orderId);
//
//            // then
//            verify(mockOrder).changeStatus(OrderStatus.PAID);
//            verify(orderCoreRepository).save(mockOrder);
//        }
//
//        @Test
//        void 주문이_존재하지_않는_경우_예외가_발생한다() {
//            // given
//            Long orderId = 999L;
//            when(orderCoreRepository.findById(orderId)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> orderService.completePayment(orderId))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessage("주문이 존재하지 않습니다.");
//        }
//    }
//}