package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategy;
import kr.hhplus.be.server.application.product.strategy.ProductFetchStrategyFactory;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.HotProductCacheManager;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    private HotProductCacheManager hotProductCacheManager;

    @Mock
    private ProductFetchStrategy productFetchStrategy;

    private ProductFacadeService productFacadeService;

    @BeforeEach
    void setUp() {
        productFacadeService = new ProductFacadeService(strategyFactory, hotProductCacheManager);
    }

    @Test
    void 카테고리가_있으면_카테고리_상품을_조회한다() {
        // given
        String category = "ELECTRONICS";
        int page = 0;
        int size = 10;

        List<ProductStockDTO> productList = Arrays.asList(
                createProductStockDTO(1L, "노트북", "ELECTRONICS", "NB001", 1000L, 10L),
                createProductStockDTO(2L, "스마트폰", "ELECTRONICS", "SP001", 2000L, 20L)
        );

        ProductInfo.CustomPageImpl<ProductStockDTO> expectedProducts =
                new ProductInfo.CustomPageImpl<>(
                        new PageImpl<>(productList, PageRequest.of(page, size), productList.size())
                );

        when(strategyFactory.getStrategy(category, page, size)).thenReturn(productFetchStrategy);
        when(productFetchStrategy.fetch(page, size)).thenReturn(expectedProducts);

        // when
        Page<ProductStockDTO> actualProducts = productFacadeService.getProducts(category, page, size);

        // then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts.getContent()).hasSize(2);
        assertThat(actualProducts.getContent()).isEqualTo(productList);
        assertThat(actualProducts.getTotalElements()).isEqualTo(2);
        verify(strategyFactory).getStrategy(category, page, size);
        verify(productFetchStrategy).fetch(page, size);
    }

    @Test
    void 카테고리_없으면_전체_조회한다() {
        // given
        String category = null;
        int page = 0;
        int size = 10;

        List<ProductStockDTO> productList = Arrays.asList(
                createProductStockDTO(1L, "노트북", "ELECTRONICS", "NB001", 1000L, 10L),
                createProductStockDTO(2L, "스마트폰", "ELECTRONICS", "SP001", 2000L, 20L),
                createProductStockDTO(3L, "티셔츠", "CLOTHING", "TS001", 3000L, 30L),
                createProductStockDTO(4L, "바지", "CLOTHING", "PT001", 4000L, 40L)
        );

        ProductInfo.CustomPageImpl<ProductStockDTO> expectedProducts =
                new ProductInfo.CustomPageImpl<>(
                        new PageImpl<>(productList, PageRequest.of(page, size), productList.size())
                );

        when(strategyFactory.getStrategy(null, page, size)).thenReturn(productFetchStrategy);
        when(productFetchStrategy.fetch(page, size)).thenReturn(expectedProducts);

        // when
        Page<ProductStockDTO> actualProducts = productFacadeService.getProducts(null, page, size);

        // then
        assertThat(actualProducts).isNotNull();
        assertThat(actualProducts.getContent()).hasSize(4);
        assertThat(actualProducts.getContent()).isEqualTo(productList);
        assertThat(actualProducts.getTotalElements()).isEqualTo(4);
        verify(strategyFactory).getStrategy(null, page, size);
        verify(productFetchStrategy).fetch(page, size);
    }

    private ProductStockDTO createProductStockDTO(Long productId, String productName, String category,
                                                  String skuId, Long unitPrice, Long stockEa) {
        return new ProductStockDTO(productId, productName, category, skuId, unitPrice, stockEa);
    }
}