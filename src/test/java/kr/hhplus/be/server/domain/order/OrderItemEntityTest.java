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

class OrderItemEntityTest {

    @Test
    void 주문_상품_생성_성공() {
        // given
        String skuId = "SKU001";
        Long ea = 3L;
        Long unitPrice = 10000L;

        // when
        OrderItemEntity orderItem = OrderItemEntity.createOrderItem(skuId, ea, unitPrice);

        // then
        assertThat(orderItem.getSkuId()).isEqualTo(skuId);
        assertThat(orderItem.getEa()).isEqualTo(ea);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    void 주문_상품_총_가격_계산_성공() {
        // given
        OrderItemEntity orderItem = OrderItemEntity.createOrderItem("SKU001", 3L, 10000L);

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(30000L); // 3 * 10000 = 30000
    }

    @Test
    void 주문_상품_수량_0인_경우_총_가격_0() {
        // given
        OrderItemEntity orderItem = OrderItemEntity.createOrderItem("SKU001", 0L, 10000L);

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L);
    }

    @Test
    void 주문_상품_단가_0인_경우_총_가격_0() {
        // given
        OrderItemEntity orderItem = OrderItemEntity.createOrderItem("SKU001", 5L, 0L);

        // when
        long totalPrice = orderItem.getTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L);
    }

    @Test
    void 빌더패턴으로_주문_상품_생성_성공() {
        // given
        String skuId = "SKU002";
        Long ea = 2L;
        Long unitPrice = 5000L;

        // when
        OrderItemEntity orderItem = OrderItemEntity.builder()
                .skuId(skuId)
                .ea(ea)
                .unitPrice(unitPrice)
                .build();

        // then
        assertThat(orderItem.getSkuId()).isEqualTo(skuId);
        assertThat(orderItem.getEa()).isEqualTo(ea);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderItem.getTotalPrice()).isEqualTo(10000L); // 2 * 5000 = 10000
    }

    @Test
    void 동일_주문_상품_여러_개_생성() {
        // given
        String skuId = "SKU003";

        // when
        OrderItemEntity item1 = OrderItemEntity.createOrderItem(skuId, 1L, 1000L);
        OrderItemEntity item2 = OrderItemEntity.createOrderItem(skuId, 2L, 1000L);

        // then
        assertThat(item1.getSkuId()).isEqualTo(item2.getSkuId());
        assertThat(item1.getEa()).isNotEqualTo(item2.getEa());
        assertThat(item1.getTotalPrice()).isEqualTo(1000L);
        assertThat(item2.getTotalPrice()).isEqualTo(2000L);
    }
}