package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockEntityTest {

    @Nested
    @DisplayName("재고 조회 테스트")
    class StockQueryTest {

        @Test
        @DisplayName("현재 재고 수량 조회 테스트")
        void getCurrentStockTest() {
            // given
            StockEntity stock = createDefaultStock();

            // when
            long currentStock = stock.getCurrentStock();

            // then
            assertThat(currentStock).isEqualTo(100L);
        }

        @Test
        @DisplayName("연결된 상품 조회 테스트")
        void getProductTest() {
            // given
            ProductEntity mockProduct = mock(ProductEntity.class);
            StockEntity stock = createStockWithProduct(mockProduct);

            // when
            ProductEntity result = stock.getProduct();

            // then
            assertThat(result).isEqualTo(mockProduct);
        }

        @Test
        @DisplayName("SKU ID 조회 테스트")
        void getSkuIdTest() {
            // given
            StockEntity stock = createDefaultStock();

            // when
            String skuId = stock.getSkuId();

            // then
            assertThat(skuId).isEqualTo("SKU12345678");
        }
    }

    @Nested
    @DisplayName("재고 수량 변경 테스트")
    class StockModificationTest {

        @Test
        @DisplayName("재고 감소 테스트 - 정상 케이스")
        void decreaseEaSuccessTest() {
            // given
            StockEntity stock = createDefaultStock();
            long decreaseAmount = 30L;

            // when
            StockEntity result = stock.decreaseEa(decreaseAmount);

            // then
            assertThat(result.getEa()).isEqualTo(70L); // 100 - 30 = 70
            assertThat(result).isSameAs(stock); // 체이닝 패턴 확인
        }

        @Test
        @DisplayName("재고 감소 테스트 - 음수 수량으로 인한 예외 발생")
        void decreaseEaWithNegativeAmountTest() {
            // given
            StockEntity stock = createDefaultStock();
            long decreaseAmount = -10L;

            // when & then
            assertThatThrownBy(() -> stock.decreaseEa(decreaseAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 양수여야 합니다");
        }

        @Test
        @DisplayName("재고 감소 테스트 - 재고 부족으로 인한 예외 발생")
        void decreaseEaWithInsufficientStockTest() {
            // given
            StockEntity stock = createDefaultStock(); // ea = 100
            long decreaseAmount = 150L;

            // when & then
            assertThatThrownBy(() -> stock.decreaseEa(decreaseAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }

        @Test
        @DisplayName("재고 증가 테스트 - 정상 케이스")
        void increaseEaSuccessTest() {
            // given
            StockEntity stock = createDefaultStock();
            long increaseAmount = 50L;

            // when
            StockEntity result = stock.increaseEa(increaseAmount);

            // then
            assertThat(result.getEa()).isEqualTo(150L); // 100 + 50 = 150
            assertThat(result).isSameAs(stock); // 체이닝 패턴 확인
        }

        @Test
        @DisplayName("재고 증가 테스트 - 음수 수량으로 인한 예외 발생")
        void increaseEaWithNegativeAmountTest() {
            // given
            StockEntity stock = createDefaultStock();
            long increaseAmount = -10L;

            // when & then
            assertThatThrownBy(() -> stock.increaseEa(increaseAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 양수여야 합니다");
        }

        @Test
        @DisplayName("재고 증가 테스트 - 최대 한도 초과로 인한 예외 발생")
        void increaseEaExceedingLimitTest() {
            // given
            StockEntity stock = createDefaultStock(); // ea = 100
            long increaseAmount = 9900L; // 100 + 9900 = 10000 > MAX_STOCK(9999)

            // when & then
            assertThatThrownBy(() -> stock.increaseEa(increaseAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 최대 한도를 초과합니다");
        }

        @Test
        @DisplayName("재고 수량 직접 설정 테스트 - 정상 케이스")
        void updateEaSuccessTest() {
            // given
            StockEntity stock = createDefaultStock();
            long newAmount = 500L;

            // when
            StockEntity result = stock.updateEa(newAmount);

            // then
            assertThat(result.getEa()).isEqualTo(newAmount);
            assertThat(result).isSameAs(stock); // 체이닝 패턴 확인
        }

        @Test
        @DisplayName("재고 수량 직접 설정 테스트 - 최대 한도 초과로 인한 예외 발생")
        void updateEaExceedingLimitTest() {
            // given
            StockEntity stock = createDefaultStock();
            long newAmount = 10000L; // > MAX_STOCK(9999)

            // when & then
            assertThatThrownBy(() -> stock.updateEa(newAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 최대 한도를 초과합니다");
        }

        @Test
        @DisplayName("상품 엔티티 업데이트 테스트")
        void updateProductTest() {
            // given
            StockEntity stock = createDefaultStock();
            ProductEntity mockProduct = mock(ProductEntity.class);
            String newSkuId = "SKU87654321";
            when(mockProduct.getSkuId()).thenReturn(newSkuId);

            // when
            StockEntity result = stock.updateProduct(mockProduct);

            // then
            assertThat(result.getProduct()).isEqualTo(mockProduct);
            assertThat(result.getSkuId()).isEqualTo(newSkuId);
            assertThat(result).isSameAs(stock); // 체이닝 패턴 확인
        }
    }

    @Nested
    @DisplayName("재고 상태 확인 테스트")
    class StockStatusTest {

        @ParameterizedTest
        @ValueSource(longs = {100, 101, 200})
        @DisplayName("재고 충분 여부 확인 테스트 - 재고 부족")
        void isEnoughStockWithInsufficientStockTest(long requiredAmount) {
            // given
            StockEntity stock = createStockWithEa(100L);

            // when
            boolean result = stock.isEnoughStock(requiredAmount);

            // then
            assertThat(result).isEqualTo(requiredAmount <= 100);
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 50, 99})
        @DisplayName("재고 충분 여부 확인 테스트 - 재고 충분")
        void isEnoughStockWithSufficientStockTest(long requiredAmount) {
            // given
            StockEntity stock = createStockWithEa(100L);

            // when
            boolean result = stock.isEnoughStock(requiredAmount);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(longs = {100, 50, 10})
        @DisplayName("임계치 이하 여부 확인 테스트 - 임계치 이하")
        void isBelowThresholdWithLowStockTest(long threshold) {
            // given
            StockEntity stock = createStockWithEa(10L);

            // when
            boolean result = stock.isBelowThreshold(threshold);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(longs = {5, 1, 0})
        @DisplayName("임계치 이하 여부 확인 테스트 - 임계치 초과")
        void isBelowThresholdWithHighStockTest(long threshold) {
            // given
            StockEntity stock = createStockWithEa(10L);

            // when
            boolean result = stock.isBelowThreshold(threshold);

            // then
            assertThat(result).isEqualTo(threshold >= 10);
        }
    }

    @Nested
    @DisplayName("재고 유효성 검증 테스트")
    class StockValidationTest {

        @Test
        @DisplayName("양수 수량 검증 - 간접 호출 테스트")
        void validatePositiveEaTest() {
            // given
            StockEntity stock = createDefaultStock();

            // when & then - private 메서드를 간접적으로 테스트
            assertThatThrownBy(() -> stock.increaseEa(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 양수여야 합니다");
        }

        @Test
        @DisplayName("최대 재고 한도 검증 - 간접 호출 테스트")
        void validateMaxStockLimitTest() {
            // given
            StockEntity stock = createStockWithEa(9000L);

            // when & then - private 메서드를 간접적으로 테스트
            assertThatThrownBy(() -> stock.increaseEa(1000L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 최대 한도를 초과합니다");
        }
    }

    // 테스트 객체 생성 메서드
    private StockEntity createDefaultStock() {
        ProductEntity mockProduct = mock(ProductEntity.class);
        when(mockProduct.getSkuId()).thenReturn("SKU12345678");

        return StockEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .ea(100L)
                .productEntity(mockProduct)
                .build();
    }

    private StockEntity createStockWithEa(Long ea) {
        ProductEntity mockProduct = mock(ProductEntity.class);
        when(mockProduct.getSkuId()).thenReturn("SKU12345678");

        return StockEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .ea(ea)
                .productEntity(mockProduct)
                .build();
    }

    private StockEntity createStockWithProduct(ProductEntity product) {
        return StockEntity.builder()
                .id(1L)
                .skuId("SKU12345678")
                .ea(100L)
                .productEntity(product)
                .build();
    }
}