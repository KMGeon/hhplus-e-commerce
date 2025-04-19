package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class OrderItemEntityTest {

    @Test
    @DisplayName("ProductEntity로부터 OrderItemEntity를 생성할 수 있다")
    void createOrderItem() {
        // given
        ProductEntity product = Instancio.of(ProductEntity.class)
                .set(field("skuId"), "SKU-12345")
                .set(field("unitPrice"), 10000L)
                .set(field("category"), CategoryEnum.APPLE)
                .create();

        Long quantity = 3L;

        // when
        OrderItemEntity orderItem = OrderItemEntity.createOrderItem(product, quantity);

        // then
        assertThat(orderItem.getSkuId()).isEqualTo(product.getSkuId());
        assertThat(orderItem.getEa()).isEqualTo(quantity);
        assertThat(orderItem.getUnitPrice()).isEqualTo(product.getUnitPrice());
    }

    @Test
    @DisplayName("총 금액을 계산할 수 있다")
    void getTotalPrice() {
        // given
        OrderItemEntity orderItem = Instancio.of(OrderItemEntity.class)
                .set(field("unitPrice"), 5000L)
                .set(field("ea"), 2L)
                .create();

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(10000L); // 5000 * 2 = 10000
    }

    @Test
    @DisplayName("수량이 0인 경우 총 금액은 0이다")
    void getTotalPrice_zeroQuantity() {
        // given
        OrderItemEntity orderItem = Instancio.of(OrderItemEntity.class)
                .set(field("unitPrice"), 5000L)
                .set(field("ea"), 0L)
                .create();

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L);
    }

    @Test
    @DisplayName("가격이 0인 경우 총 금액은 0이다")
    void getTotalPrice_zeroPrice() {
        // given
        OrderItemEntity orderItem = Instancio.of(OrderItemEntity.class)
                .set(field("unitPrice"), 0L)
                .set(field("ea"), 5L)
                .create();

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L);
    }
}