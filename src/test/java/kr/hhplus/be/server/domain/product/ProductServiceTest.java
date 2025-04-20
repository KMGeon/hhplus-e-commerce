package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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
        List<ProductStockDTO> expectedProducts = Arrays.asList(
                createProductStockDTO(1L, "제품1", "FOOD", "SKU001", 1000L, 10L),
                createProductStockDTO(2L, "제품2", "FOOD", "SKU002", 2000L, 20L)
        );

        when(productRepository.getProductsWithStockInfoByCategory(categoryCode)).thenReturn(expectedProducts);

        // when
        List<ProductStockDTO> result = productService.getProductByCategoryCode(categoryCode);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedProducts);
        verify(productRepository, times(1)).getProductsWithStockInfoByCategory(categoryCode);
    }

    @Test
    void 전체_상품목록_조회() {
        // given
        List<ProductStockDTO> expectedProducts = Arrays.asList(
                createProductStockDTO(1L, "제품1", "FOOD", "SKU001", 1000L, 10L),
                createProductStockDTO(2L, "제품2", "FOOD", "SKU002", 2000L, 20L),
                createProductStockDTO(3L, "제품3", "DRINK", "SKU003", 3000L, 30L)
        );

        when(productRepository.getProductsWithStockInfo()).thenReturn(expectedProducts);

        // when
        List<ProductStockDTO> result = productService.getAllProduct();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(expectedProducts);
        verify(productRepository, times(1)).getProductsWithStockInfo();
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

        // when
// then
        assertThatThrownBy(() -> productService.checkProductSkuIds(item1, item2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("잘못된 SKU ID가 포함되어 있습니다");

        verify(productRepository, times(1)).countBySkuIdIn(Arrays.asList("SKU001", "INVALID_SKU"));
    }

    private ProductStockDTO createProductStockDTO(Long productId, String productName,
                                                  String category, String skuId,
                                                  Long unitPrice, Long stockEa) {
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

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ProductStockDTO that = (ProductStockDTO) o;
                return getProductId().equals(that.getProductId()) &&
                        getProductName().equals(that.getProductName()) &&
                        getCategory().equals(that.getCategory()) &&
                        getSkuId().equals(that.getSkuId()) &&
                        getUnitPrice().equals(that.getUnitPrice()) &&
                        getStockEa().equals(that.getStockEa());
            }
        };
    }
}