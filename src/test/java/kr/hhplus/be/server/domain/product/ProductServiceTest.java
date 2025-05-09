package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void 카테고리별_상품목록_조회() {
        // given
        String categoryCode = "FOOD";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<ProductStockDTO> productList = Arrays.asList(
                createProductStockDTO(1L, "제품1", "FOOD", "SKU001", 1000L, 10L),
                createProductStockDTO(2L, "제품2", "FOOD", "SKU002", 2000L, 20L)
        );

        PageImpl<ProductStockDTO> expectedPage = new PageImpl<>(
                productList, pageable, productList.size()
        );

        when(productRepository.getProductsWithStockInfoByCategory(categoryCode, pageable)).thenReturn(expectedPage);

        // when
        ProductInfo.CustomPageImpl<ProductStockDTO> result = productService.getProductByCategoryCode(categoryCode, page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).isEqualTo(productList);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(productRepository, times(1)).getProductsWithStockInfoByCategory(categoryCode, pageable);
    }

    @Test
    void 전체_상품목록_조회() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<ProductStockDTO> productList = Arrays.asList(
                createProductStockDTO(1L, "제품1", "FOOD", "SKU001", 1000L, 10L),
                createProductStockDTO(2L, "제품2", "FOOD", "SKU002", 2000L, 20L),
                createProductStockDTO(3L, "제품3", "DRINK", "SKU003", 3000L, 30L)
        );

        PageImpl<ProductStockDTO> expectedPage = new PageImpl<>(
                productList, pageable, productList.size()
        );

        when(productRepository.getProductsWithStockInfo(pageable)).thenReturn(expectedPage);

        // when
        ProductInfo.CustomPageImpl<ProductStockDTO> result = productService.getAllProduct(page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).isEqualTo(productList);
        assertThat(result.getTotalElements()).isEqualTo(3);
        verify(productRepository, times(1)).getProductsWithStockInfo(pageable);
    }

    @Test
    void 유효한_SKU_ID_검증_성공() {
        // given
        OrderCriteria.Item item1 = new OrderCriteria.Item("SKU001", 1);
        OrderCriteria.Item item2 = new OrderCriteria.Item("SKU002", 2);

        when(productRepository.countBySkuIdIn(Arrays.asList("SKU001", "SKU002"))).thenReturn(2L);

        // when
        productService.checkProductSkuIds(item1, item2);

        // then
        verify(productRepository, times(1)).countBySkuIdIn(Arrays.asList("SKU001", "SKU002"));
    }

    @Test
    void 유효하지_않은_SKU_ID_검증_실패() {
        // given
        OrderCriteria.Item item1 = new OrderCriteria.Item("SKU001", 1);
        OrderCriteria.Item item2 = new OrderCriteria.Item("INVALID_SKU", 2);

        when(productRepository.countBySkuIdIn(Arrays.asList("SKU001", "INVALID_SKU"))).thenReturn(1L);

        // when & then
        assertThatThrownBy(() -> productService.checkProductSkuIds(item1, item2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("잘못된 SKU ID가 포함되어 있습니다");

        verify(productRepository, times(1)).countBySkuIdIn(Arrays.asList("SKU001", "INVALID_SKU"));
    }

    private ProductStockDTO createProductStockDTO(Long productId, String productName,
                                                  String category, String skuId,
                                                  Long unitPrice, Long stockEa) {
        return new ProductStockDTO(productId, productName, category, skuId, unitPrice, stockEa);
    }
}