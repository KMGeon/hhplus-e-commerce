package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private ProductService productService;

    @Nested
    @DisplayName("""
            [ 기능 ] : 상품 조회
            [ 검증 ]
                - 카테고리가 있으면 특정 카테고리 상품을 조회한다.
                - 카테고리가 없으면 전체 상품을 조회한다. 
            """)
    class 상품_조회 {

        @Test
        public void 전체_상품_조회() throws Exception {
            // given
            final int size = 10;
            List<ProductInfo.ProductInfoResponse> expectRtn = Instancio.ofList(ProductInfo.ProductInfoResponse.class)
                    .size(size)
                    .create();

            when(productRepository.findProductWithStock())
                    .thenReturn(expectRtn);

            // when
            List<ProductInfo.ProductInfoResponse> actualRtn = productService.getAllProduct();

            // then
            assertThat(actualRtn).isNotNull();
            assertEquals(size, actualRtn.size(), "");
        }

        @Test
        public void 카테고리_상품_조회() throws Exception {
            // given
            final String category = CategoryEnum.APPLE.getCategoryCode();
            final int size = 10;

            List<ProductInfo.ProductInfoResponse> expectRtn = Instancio.ofList(ProductInfo.ProductInfoResponse.class)
                    .size(size)
                    .set(Select.field(ProductInfo.ProductInfoResponse::category), category)
                    .create();


            when(productRepository.findProductWithStockByCategoryCode(eq(category)))
                    .thenReturn(expectRtn);

            // when
            List<ProductInfo.ProductInfoResponse> actualRtn = productService.getProductByCategoryCode(category);

            // then
            assertThat(actualRtn).isNotNull();
            assertEquals(size, actualRtn.size(), "");
        }
    }


    @Nested
    @DisplayName("""
            [ 기능 ] : 상품 ID가 올바른지 검증한다.
            [ 검증 ]
                - Product ID가 올바르면 True를 반환
                - Product ID가 올바르지 않으면 False를 반환
            """)
    class 상품코드_검증 {


        @Test
        public void 상품코드_조회_성공() throws Exception {
            // given
            List<OrderCommand.Item> items = List.of(
                    new OrderCommand.Item(1L, 1L, 1000L),
                    new OrderCommand.Item(2L, 1L, 1000L),
                    new OrderCommand.Item(3L, 1L, 1000L),
                    new OrderCommand.Item(4L, 1L, 1000L),
                    new OrderCommand.Item(5L, 1L, 1000L)
            );

            List<ProductEntity> expect = Instancio.ofList(ProductEntity.class)
                    .size(items.size())
                    .create();

            when(productRepository.findAllByIdIn(any()))
                    .thenReturn(expect);

            // when
            boolean result = productService.validateProducts(items);

            // then
            assertEquals(true, result,"5개의 Input과 5개의 Output이 같으면 성공");
        }

        @Test
        public void 상품코드_조회_중복_상품코드() throws Exception{
            // given
            List<OrderCommand.Item> items = List.of(
                    new OrderCommand.Item(1L, 1L, 1000L),
                    new OrderCommand.Item(2L, 1L, 1000L),
                    new OrderCommand.Item(3L, 1L, 1000L),
                    new OrderCommand.Item(4L, 1L, 1000L),
                    new OrderCommand.Item(4L, 1L, 1000L)
            );

            List<ProductEntity> expect = Instancio.ofList(ProductEntity.class)
                    .size(items.size())
                    .create();

            when(productRepository.findAllByIdIn(any()))
                    .thenReturn(expect);

            // when
            boolean result = productService.validateProducts(items);

            // then
            assertEquals(false, result,"상품 ID는 Set로 중복을 제거한 후 비교해야 한다.");
        }


        @Test
        public void 상품코드_조회_ID_불일치() throws Exception{
            // given
            List<OrderCommand.Item> items = List.of(
                    new OrderCommand.Item(1L, 1L, 1000L),
                    new OrderCommand.Item(2L, 1L, 1000L),
                    new OrderCommand.Item(3L, 1L, 1000L),
                    new OrderCommand.Item(4L, 1L, 1000L),
                    new OrderCommand.Item(5L, 1L, 1000L)
            );

            List<ProductEntity> expect = Instancio.ofList(ProductEntity.class)
                    .size(3)
                    .create();

            when(productRepository.findAllByIdIn(any()))
                    .thenReturn(expect);

            // when
            boolean result = productService.validateProducts(items);

            // then
            assertEquals(false, result,"");
        }
    }

    @Nested
    @DisplayName("""
            [ 기능 ] : 재고 관리 테스트
            [ 검증 ]
                - 모든 재고가 충분한지 확인한다.
                - 일부 상품의 재고를 파악한다.
            """)
    class StockManagementTest {

        @Nested
        @DisplayName("재고 가용성 확인 테스트")
        class StockAvailabilityTest {

            @Test
            void 모든_상품_재고_충분() {
                // given
                List<OrderCommand.Item> items = List.of(
                        new OrderCommand.Item(1L, 2L, 1000L),
                        new OrderCommand.Item(2L, 3L, 2000L)
                );

                List<StockEntity> stocks = new ArrayList<>();
                stocks.add(createStockEntity(1L, 10L)); // 재고 10개
                stocks.add(createStockEntity(2L, 15L)); // 재고 15개

                when(stockRepository.findAllByProductIdIn(anyList()))
                        .thenReturn(stocks);

                // when
                boolean result = productService.checkStockAvailability(items);

                // then
                assertTrue(result, "모든 상품의 재고가 충분하면 true를 반환해야 한다");
                verify(stockRepository, times(1)).findAllByProductIdIn(anyList());
            }

            @Test
            void 일부_상품_재고_부족() {
                // given
                List<OrderCommand.Item> items = List.of(
                        new OrderCommand.Item(1L, 10L, 1000L),
                        new OrderCommand.Item(2L, 20L, 2000L)
                );

                List<StockEntity> stocks = new ArrayList<>();
                stocks.add(createStockEntity(1L, 15L));
                stocks.add(createStockEntity(2L, 5L));

                when(stockRepository.findAllByProductIdIn(anyList()))
                        .thenReturn(stocks);

                // when
                boolean result = productService.checkStockAvailability(items);

                // then
                assertFalse(result, "일부 상품의 재고가 부족하면 false를 반환해야 한다");
                verify(stockRepository, times(1)).findAllByProductIdIn(anyList());
            }
        }

        @Nested
        @DisplayName("""
            [ 기능 ] : 재고 감소를 시킨다.
            [ 검증 ]
                - 검증된 상품, 재고를 감소시킨다.
            """)
        class StockDecreaseTest {

            @Test
            void 주문에_포함된_모든상품의_재고감소() {
                // given
                List<OrderCommand.Item> items = List.of(
                        new OrderCommand.Item(1L, 2L, 1000L),
                        new OrderCommand.Item(2L, 3L, 2000L)
                );
                OrderCommand.Order order = new OrderCommand.Order(1L, items);

                StockEntity stock1 = createStockEntity(1L, 10L);
                StockEntity stock2 = createStockEntity(2L, 15L);
                List<StockEntity> stocks = List.of(stock1, stock2);

                when(stockRepository.findAllByProductIdIn(anyList()))
                        .thenReturn(stocks);

                // when
                productService.decreaseStock(order);

                // then
                verify(stockRepository, times(1)).findAllByProductIdIn(anyList());
                assertEquals(8L, stock1.getEa(), "첫 번째 상품의 재고가 2개 감소해야 한다");
                assertEquals(12L, stock2.getEa(), "두 번째 상품의 재고가 3개 감소해야 한다");
            }
        }
    }

    private StockEntity createStockEntity(Long productId, Long ea) {
        ProductEntity product = ProductEntity.builder()
                .id(productId)
                .skuId("SKU" + productId)
                .productName("상품 " + productId)
                .category(CategoryEnum.APPLE)
                .price(10000L)
                .build();

        return StockEntity.builder()
                .id(productId)
                .ea(ea)
                .skuId("SKU" + productId)
                .productEntity(product)
                .build();
    }
}