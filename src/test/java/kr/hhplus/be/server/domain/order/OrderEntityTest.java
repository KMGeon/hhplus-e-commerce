package kr.hhplus.be.server.domain.order;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class OrderEntityTest {

    @Test
    @DisplayName("주문을 생성할 수 있다")
    void createOrder() {
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
        assertThat(order.getOrderProducts()).isEmpty();
    }

    @Test
    @DisplayName("주문 상태를 CONFIRMED로 변경할 수 있다")
    void orderStatusConfirm() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.PAID)
                .create();

        // when
        order.orderStatusConfirm();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("주문 아이템을 추가하고 총액을 계산할 수 있다")
    void addOrderItems() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getOrderProducts), new ArrayList<>())
                .create();

        List<OrderItemEntity> orderItems = Arrays.asList(
                createOrderItem("상품1", 1000L, 2L),
                createOrderItem("상품2", 3000L, 1L)
        );

        // when
        order.addOrderItems(orderItems);

        // then
        assertThat(order.getOrderProducts()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(order.getTotalEa()).isEqualTo(BigDecimal.valueOf(3));
    }

    @Test
    @DisplayName("유효한 주문 상태가 아닌 경우 결제 검증에서 예외가 발생한다")
    void validatePaymentAvailable_invalidStatus() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.PAID)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().plusMinutes(5))
                .set(field(OrderEntity::getTotalPrice), BigDecimal.valueOf(1000))
                .create();

        // when & then
        assertThatThrownBy(() -> order.validatePaymentAvailable())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 가능한 상태가 아닙니다");
    }

    @Test
    @DisplayName("주문이 만료된 경우 결제 검증에서 예외가 발생한다")
    void validatePaymentAvailable_expired() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.CONFIRMED)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().minusMinutes(1))
                .set(field(OrderEntity::getTotalPrice), BigDecimal.valueOf(1000))
                .create();

        // when & then
        assertThatThrownBy(() -> order.validatePaymentAvailable())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문이 만료되었습니다");
    }

    @Test
    @DisplayName("주문 금액이 유효하지 않은 경우 결제 검증에서 예외가 발생한다")
    void validatePaymentAvailable_invalidAmount() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.CONFIRMED)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().plusMinutes(5))
                .set(field(OrderEntity::getTotalPrice), BigDecimal.ZERO)
                .create();

        // when & then
        assertThatThrownBy(() -> order.validatePaymentAvailable())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 금액이 유효하지 않습니다");
    }

    @Test
    @DisplayName("유효한 주문은 결제 검증을 통과할 수 있다")
    void validatePaymentAvailable_validOrder() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.CONFIRMED)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().plusMinutes(5))
                .set(field(OrderEntity::getTotalPrice), BigDecimal.valueOf(1000))
                .create();

        // when & then (예외가 발생하지 않아야 함)
        order.validatePaymentAvailable();
    }

    @Test
    @DisplayName("할인 금액을 적용하고 최종 금액을 계산할 수 있다")
    void applyDiscount() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getTotalPrice), BigDecimal.valueOf(10000))
                .create();

        order.setDiscountAmount(BigDecimal.valueOf(2000));

        // when
        order.applyDiscount();

        // then
        assertThat(order.getFinalAmount()).isEqualTo(BigDecimal.valueOf(8000));
    }

    @Test
    @DisplayName("할인 금액이 주문 총액보다 큰 경우 최종 금액은 0이 된다")
    void applyDiscount_discountGreaterThanTotal() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getTotalPrice), BigDecimal.valueOf(5000))
                .create();

        order.setDiscountAmount(BigDecimal.valueOf(7000));

        // when
        order.applyDiscount();

        // then
        assertThat(order.getFinalAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("주문 상태를 PAID로 변경할 수 있다")
    void complete() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.CONFIRMED)
                .create();

        // when
        order.complete();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("만료된 주문은 결제 가능 상태 확인에서 예외가 발생한다")
    void isAvailablePaymentState_expired() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().minusMinutes(1))
                .create();

        // when & then
        assertThatThrownBy(() -> order.isAvailablePaymentState())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문이 만료되었습니다");
    }

    @Test
    @DisplayName("CONFIRMED 상태가 아닌 주문은 결제 가능 상태 확인에서 예외가 발생한다")
    void isAvailablePaymentState_notConfirmed() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.PAID)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().plusMinutes(5))
                .create();

        // when & then
        assertThatThrownBy(() -> order.isAvailablePaymentState())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 가능한 상태가 아닙니다");
    }

    @Test
    @DisplayName("유효한 주문은 결제 가능 상태 확인을 통과할 수 있다")
    void isAvailablePaymentState_valid() {
        // given
        OrderEntity order = Instancio.of(OrderEntity.class)
                .set(field(OrderEntity::getStatus), OrderStatus.CONFIRMED)
                .set(field(OrderEntity::getExpireTime), LocalDateTime.now().plusMinutes(5))
                .create();

        // when & then (예외가 발생하지 않아야 함)
        order.isAvailablePaymentState();
    }

    private OrderItemEntity createOrderItem(String skuId, long price, long ea) {
        return OrderItemEntity.builder()
                .skuId(skuId)
                .unitPrice(price)
                .ea(ea)
                .build();
    }
}
