package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    @DisplayName("상품 엔티티 빌더를 통한 생성 테스트")
    void testCreateProductEntityWithBuilder() {
        // given
        String skuId = "SKU123456";
        String productName = "맥북 프로 16인치";
        Long unitPrice = 3000000L;
        CategoryEnum category = CategoryEnum.APPLE;

        // when
        ProductEntity product = ProductEntity.builder()
                .skuId(skuId)
                .productName(productName)
                .unitPrice(unitPrice)
                .category(category)
                .build();

        // then
        assertNotNull(product);
        assertEquals(skuId, product.getSkuId());
        assertEquals(productName, product.getProductName());
        assertEquals(unitPrice, product.getUnitPrice());
        assertEquals(category.getCategoryCode(), product.getCategory());
        assertEquals(category.getCategoryCode(), product.getCategoryCode());
    }

    @Test
    @DisplayName("getCategory 메소드 테스트")
    void testGetCategory() {
        // given
        ProductEntity product = createSampleProduct();
        CategoryEnum category = CategoryEnum.SAMSUNG;

        // when
        String categoryCode = product.getCategory();

        // then
        assertEquals(category.getCategoryCode(), categoryCode);
    }

    @Test
    @DisplayName("getCategoryCode 메소드 테스트")
    void testGetCategoryCode() {
        // given
        ProductEntity product = createSampleProduct();
        CategoryEnum category = CategoryEnum.SAMSUNG;

        // when
        String categoryCode = product.getCategoryCode();

        // then
        assertEquals(category.getCategoryCode(), categoryCode);
    }


    // 테스트에 사용할 샘플 상품 생성 헬퍼 메소드
    private ProductEntity createSampleProduct() {
        return ProductEntity.builder()
                .skuId("SKU456789")
                .productName("갤럭시 S22")
                .unitPrice(1200000L)
                .category(CategoryEnum.SAMSUNG)
                .build();
    }
}