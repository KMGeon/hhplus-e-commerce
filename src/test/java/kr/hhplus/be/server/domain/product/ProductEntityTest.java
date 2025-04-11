package kr.hhplus.be.server.domain.product;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProductEntityTest {

    @Nested
    @DisplayName("상품 정보 관리 테스트")
    class ProductInfoManagementTest {

        @Test
        @DisplayName("상품명 업데이트 테스트")
        void updateProductNameTest() {
            // given
            ProductEntity product = createDefaultProduct();
            String newProductName = "업데이트된 상품명";

            // when
            ProductEntity updatedProduct = product.updateProductName(newProductName);

            // then
            assertThat(updatedProduct.getProductName()).isEqualTo(newProductName);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("SKU ID 업데이트 테스트")
        void updateSkuIdTest() {
            // given
            ProductEntity product = createDefaultProduct();
            String newSkuId = "SKU12345678";

            // when
            ProductEntity updatedProduct = product.updateSkuId(newSkuId);

            // then
            assertThat(updatedProduct.getSkuId()).isEqualTo(newSkuId);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("상품 정보 전체 업데이트 테스트 - 정상 케이스")
        void updateProductInfoSuccessTest() {
            // given
            ProductEntity product = createDefaultProduct();
            String newProductName = "새 상품명";
            String newSkuId = "SKU87654321";
            CategoryEnum newCategory = CategoryEnum.APPLE;
            Long newPrice = 15000L;

            // when
            ProductEntity updatedProduct = product.updateProductInfo(
                    newProductName, newSkuId, newCategory, newPrice);

            // then
            assertThat(updatedProduct.getProductName()).isEqualTo(newProductName);
            assertThat(updatedProduct.getSkuId()).isEqualTo(newSkuId);
            assertThat(updatedProduct.getCategory()).isEqualTo(newCategory.getCategoryCode());
            assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("상품 정보 전체 업데이트 테스트 - 카테고리 null 예외 발생")
        void updateProductInfoWithNullCategoryTest() {
            // given
            ProductEntity product = createDefaultProduct();

            // when & then
            assertThatThrownBy(() -> product.updateProductInfo(
                    "새 상품명", "SKU87654321", null, 15000L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다");
        }
    }

    @Nested
    @DisplayName("상품 카테고리 관리 테스트")
    class ProductCategoryManagementTest {

        @ParameterizedTest
        @EnumSource(CategoryEnum.class)
        @DisplayName("카테고리 업데이트 테스트 - 모든 유효 카테고리")
        void updateCategoryWithValidCategoryTest(CategoryEnum category) {
            // given
            ProductEntity product = createDefaultProduct();

            // when
            ProductEntity updatedProduct = product.updateCategory(category);

            // then
            assertThat(updatedProduct.getCategory()).isEqualTo(category.getCategoryCode());
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("카테고리 업데이트 테스트 - null 카테고리 예외 발생")
        void updateCategoryWithNullCategoryTest() {
            // given
            ProductEntity product = createDefaultProduct();

            // when & then
            assertThatThrownBy(() -> product.updateCategory(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다");
        }

        @Test
        @DisplayName("카테고리 코드 조회 테스트")
        void getCategoryCodeTest() {
            // given
            ProductEntity product = createProductWithCategory(CategoryEnum.LG);

            // when
            String categoryCode = product.getCategoryCode();

            // then
            assertThat(categoryCode).isEqualTo(CategoryEnum.LG.getCategoryCode());
        }

        @Test
        @DisplayName("카테고리 이름 조회 테스트")
        void getCategoryNameTest() {
            // given
            ProductEntity product = createProductWithCategory(CategoryEnum.DELL);

            // when
            String categoryName = product.getCategoryName();

            // then
            assertThat(categoryName).isEqualTo(CategoryEnum.DELL.getDescription());
        }
    }

    @Nested
    @DisplayName("상품 가격 관리 테스트")
    class ProductPriceManagementTest {

        @Test
        @DisplayName("가격 업데이트 테스트")
        void updatePriceTest() {
            // given
            ProductEntity product = createDefaultProduct();
            Long newPrice = 15000L;

            // when
            ProductEntity updatedProduct = product.updatePrice(newPrice);

            // then
            assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("가격 조정 테스트 - 인상")
        void adjustPriceIncreaseTest() {
            // given
            ProductEntity product = createProductWithPrice(10000L);
            long amount = 2000L;

            // when
            ProductEntity updatedProduct = product.adjustPrice(amount);

            // then
            assertThat(updatedProduct.getPrice()).isEqualTo(12000L);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }

        @Test
        @DisplayName("가격 조정 테스트 - 인하")
        void adjustPriceDecreaseTest() {
            // given
            ProductEntity product = createProductWithPrice(10000L);
            long amount = -2000L;

            // when
            ProductEntity updatedProduct = product.adjustPrice(amount);

            // then
            assertThat(updatedProduct.getPrice()).isEqualTo(8000L);
            assertThat(updatedProduct).isSameAs(product); // 동일 객체 체이닝 확인
        }
    }

    @Nested
    @DisplayName("카테고리 유효성 검증 테스트")
    class CategoryValidationTest {

        @Test
        @DisplayName("validateCategory 메서드 - null 카테고리 예외 발생")
        void validateCategoryWithNullTest() {
            // given
            ProductEntity product = createDefaultProduct();

            // when & then - private 메서드를 간접적으로 테스트
            assertThatThrownBy(() -> product.updateCategory(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("카테고리는 필수입니다");
        }

        @ParameterizedTest
        @EnumSource(CategoryEnum.class)
        @DisplayName("validateCategory 메서드 - 유효한 카테고리")
        void validateCategoryWithValidCategoryTest(CategoryEnum category) {
            // given
            ProductEntity product = createDefaultProduct();

            // when & then - private 메서드를 간접적으로 테스트
            assertDoesNotThrow(() -> product.updateCategory(category));
        }
    }

    // 테스트 객체 생성 메서드
    private ProductEntity createDefaultProduct() {
        return ProductEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .productName("테스트 상품")
                .category(CategoryEnum.DELL)
                .price(10000L)
                .build();
    }

    private ProductEntity createProductWithCategory(CategoryEnum category) {
        return ProductEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .productName("테스트 상품")
                .category(category)
                .price(10000L)
                .build();
    }

    private ProductEntity createProductWithPrice(Long price) {
        return ProductEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .productName("테스트 상품")
                .category(CategoryEnum.DELL)
                .price(price)
                .build();
    }
}