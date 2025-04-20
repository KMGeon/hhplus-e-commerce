package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderEntityTest {

    @Test
    void 주문_생성_성공() {
        // given
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        OrderEntity order = OrderEntity.createOrder(userId, now);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getDatePath()).isEqualTo(DatePathProvider.toPath(now));
        assertThat(order.getExpireTime()).isEqualTo(now.plusMinutes(10));
        assertThat(order.getOrderProducts()).isNotNull();
        assertThat(order.getOrderProducts()).isEmpty();
    }

    @Test
    void 주문_상품_추가_성공() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());
        OrderItemEntity item1 = OrderItemEntity.createOrderItem("SKU001", 2L, 1000L);
        OrderItemEntity item2 = OrderItemEntity.createOrderItem("SKU002", 3L, 2000L);
        List<OrderItemEntity> orderItems = Arrays.asList(item1, item2);

        // when
        order.addOrderItems(orderItems);

        // then
        assertThat(order.getOrderProducts()).hasSize(2);
        assertThat(order.getTotalEa()).isEqualByComparingTo(BigDecimal.valueOf(5)); // 2 + 3 = 5
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(8000)); // (2*1000) + (3*2000) = 8000
    }

    @Test
    void 할인_적용_성공() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());
        OrderItemEntity item = OrderItemEntity.createOrderItem("SKU001", 2L, 10000L);
        order.addOrderItems(Arrays.asList(item));
        BigDecimal discountAmount = BigDecimal.valueOf(5000);

        // when
        order.applyDiscount(discountAmount);

        // then
        assertThat(order.getDiscountAmount()).isEqualByComparingTo(discountAmount);
        assertThat(order.getFinalAmount()).isEqualByComparingTo(BigDecimal.valueOf(15000)); // 20000 - 5000 = 15000
    }

    @Test
    void 할인액이_총_금액보다_클_경우_최종_금액은_0() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());
        OrderItemEntity item = OrderItemEntity.createOrderItem("SKU001", 1L, 1000L);
        order.addOrderItems(Arrays.asList(item));
        BigDecimal discountAmount = BigDecimal.valueOf(2000); // 총 금액 1000보다 큰 할인액

        // when
        order.applyDiscount(discountAmount);

        // then
        assertThat(order.getDiscountAmount()).isEqualByComparingTo(discountAmount);
        assertThat(order.getFinalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void 주문_완료_처리_성공() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());

        // when
        order.complete();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void 주문_취소_처리_성공() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void 결제_가능_상태_확인_성공() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());

        // when
        // then
        order.isAvailablePaymentState();
    }

    @Test
    void 만료된_주문은_결제_불가능() {
        // given
        LocalDateTime past = LocalDateTime.now().minusHours(1); // 1시간 전 (만료 시간은 10분이므로 만료됨)
        OrderEntity order = OrderEntity.createOrder(1L, past);

        // when
        // then
        assertThatThrownBy(order::isAvailablePaymentState)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문이 만료되었습니다");
    }

    @Test
    void 확정_상태가_아닌_주문은_결제_불가능() {
        // given
        OrderEntity order = OrderEntity.createOrder(1L, LocalDateTime.now());
        order.cancel(); // 취소 상태로 변경

        // when
        // then
        assertThatThrownBy(order::isAvailablePaymentState)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 가능한 상태가 아닙니다");
    }

    @Test
    void 빌더_패턴으로_주문_생성_성공() {
        // given
        Long userId = 1L;
        Long userCouponId = 100L;
        String datePath = "0421";
        OrderStatus status = OrderStatus.CONFIRMED;
        List<OrderItemEntity> orderProducts = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.valueOf(5000);
        BigDecimal totalEa = BigDecimal.valueOf(2);
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);

        // when
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .datePath(datePath)
                .status(status)
                .orderProducts(orderProducts)
                .totalPrice(totalPrice)
                .totalEa(totalEa)
                .expireTime(expireTime)
                .build();

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(order.getDatePath()).isEqualTo(datePath);
        assertThat(order.getStatus()).isEqualTo(status);
        assertThat(order.getOrderProducts()).isSameAs(orderProducts);
        assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(order.getTotalEa()).isEqualTo(totalEa);
        assertThat(order.getExpireTime()).isEqualTo(expireTime);
    }
}