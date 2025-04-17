package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategyFactory;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductFacadeServiceTest {

    @Mock
    private ProductFetchStrategyFactory strategyFactory;

    @Mock
    private ProductFetchStrategy productFetchStrategy;

    private ProductFacadeService productFacadeService;

    @BeforeEach
    void setUp() {
        productFacadeService = new ProductFacadeService(strategyFactory);
    }

    @Test
    void 카테고리가_있으면_카테고리_상품을_조회한다() {
        // given
        String category = "ELECTRONICS";
        List<ProductStockDTO> expectedProducts = Arrays.asList(
                createProductStockDTO(1L, "노트북", "ELECTRONICS", "NB001", 1000L, 10L),
                createProductStockDTO(2L, "스마트폰", "ELECTRONICS", "SP001", 2000L, 20L)
        );

        when(strategyFactory.getStrategy(category)).thenReturn(productFetchStrategy);
        when(productFetchStrategy.fetch()).thenReturn(expectedProducts);

        // when
        List<ProductStockDTO> actualProducts = productFacadeService.getProducts(category);

        // then
        assertThat(actualProducts).hasSize(2);
        assertThat(actualProducts).isEqualTo(expectedProducts);
        verify(strategyFactory).getStrategy(category);
        verify(productFetchStrategy).fetch();
    }

    @Test
    void 카테고리_없으면_전체_조회한다() {
        // given
        String category = null;
        List<ProductStockDTO> expectedProducts = Arrays.asList(
                createProductStockDTO(1L, "노트북", "ELECTRONICS", "NB001", 1000L, 10L),
                createProductStockDTO(2L, "스마트폰", "ELECTRONICS", "SP001", 2000L, 20L),
                createProductStockDTO(3L, "티셔츠", "CLOTHING", "TS001", 3000L, 30L),
                createProductStockDTO(4L, "바지", "CLOTHING", "PT001", 4000L, 40L)
        );

        when(strategyFactory.getStrategy(category)).thenReturn(productFetchStrategy);
        when(productFetchStrategy.fetch()).thenReturn(expectedProducts);

        // when
        List<ProductStockDTO> actualProducts = productFacadeService.getProducts(category);

        // then
        assertThat(actualProducts).hasSize(4);
        assertThat(actualProducts).isEqualTo(expectedProducts);
        verify(strategyFactory).getStrategy(category);
        verify(productFetchStrategy).fetch();
    }

    private ProductStockDTO createProductStockDTO(Long productId, String productName, String category, 
                                                String skuId, Long unitPrice, Long stockEa) {
        return new ProductStockDTO() {
            @Override
            public Long getProductId() {
                return productId;
            }

            @Override
            public String getProductName() {
                return productName;
            }

            @Override
            public String getCategory() {
                return category;
            }

            @Override
            public String getSkuId() {
                return skuId;
            }

            @Override
            public Long getUnitPrice() {
                return unitPrice;
            }

            @Override
            public Long getStockEa() {
                return stockEa;
            }
        };
    }
}