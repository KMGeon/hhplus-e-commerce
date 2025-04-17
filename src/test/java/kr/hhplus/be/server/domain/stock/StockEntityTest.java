package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.domain.product.CategoryEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockEntityTest {

    @Test
    @DisplayName("재고 엔티티 빌더를 통한 생성 테스트")
    void testCreateStockEntityWithBuilder() {
        // given
        String skuId = "SKU123456";
        CategoryEnum category = CategoryEnum.APPLE;
        Long orderId = 1000L;

        // when
        StockEntity stock = StockEntity.builder()
                .skuId(skuId)
                .category(category)
                .orderId(orderId)
                .build();

        // then
        assertNotNull(stock);
        assertEquals(skuId, stock.getSkuId());
        assertEquals(category, stock.getCategory());
        assertEquals(orderId, stock.getOrderId());
    }

    @Test
    @DisplayName("재고 엔티티 빌더 - orderId가 null인 경우 테스트")
    void testCreateStockEntityWithNullOrderId() {
        // given
        String skuId = "SKU789012";
        CategoryEnum category = CategoryEnum.SAMSUNG;

        // when
        StockEntity stock = StockEntity.builder()
                .skuId(skuId)
                .category(category)
                .build();

        // then
        assertNotNull(stock);
        assertEquals(skuId, stock.getSkuId());
        assertEquals(category, stock.getCategory());
        assertNull(stock.getOrderId());
    }

    @Test
    @DisplayName("setOrderId 메소드 테스트 - null에서 값 설정")
    void testSetOrderId_fromNull() {
        // given
        StockEntity stock = StockEntity.builder()
                .skuId("SKU123")
                .category(CategoryEnum.LG)
                .build();
        assertNull(stock.getOrderId());

        // when
        Long orderId = 500L;
        stock.setOrderId(orderId);

        // then
        assertEquals(orderId, stock.getOrderId());
    }

    @Test
    @DisplayName("setOrderId 메소드 테스트 - 기존 값에서 새 값으로 변경")
    void testSetOrderId_fromExistingValue() {
        // given
        Long originalOrderId = 100L;
        StockEntity stock = StockEntity.builder()
                .skuId("SKU456")
                .category(CategoryEnum.SONY)
                .orderId(originalOrderId)
                .build();
        assertEquals(originalOrderId, stock.getOrderId());

        // when
        Long newOrderId = 200L;
        stock.setOrderId(newOrderId);

        // then
        assertEquals(newOrderId, stock.getOrderId());
    }

    @Test
    @DisplayName("setOrderId 메소드 테스트 - 기존 값에서 null로 변경")
    void testSetOrderId_toNull() {
        // given
        Long originalOrderId = 300L;
        StockEntity stock = StockEntity.builder()
                .skuId("SKU789")
                .category(CategoryEnum.DELL)
                .orderId(originalOrderId)
                .build();
        assertEquals(originalOrderId, stock.getOrderId());

        // when
        stock.setOrderId(null);

        // then
        assertNull(stock.getOrderId());
    }
}