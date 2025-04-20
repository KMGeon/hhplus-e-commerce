//package kr.hhplus.be.server.domain.product;
//
//import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
//import kr.hhplus.be.server.domain.stock.StockRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceTest {
//
//    @InjectMocks
//    private ProductService productService;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private StockRepository stockRepository;
//
//    @Test
//    void 카테고리별_상품조회_성공() {
//        // given
//        String categoryCode = "APPLE";
//        List<ProductStockDTO> expectedProducts = Arrays.asList(
//                createMockProductStockDTO("AP-IP15-PRO", "iPhone 15 Pro", 10),
//                createMockProductStockDTO("AP-MB-AIR-M2", "MacBook Air M2", 5)
//        );
//
//        when(productRepository.getProductsWithStockInfoByCategory(categoryCode)).thenReturn(expectedProducts);
//
//        // when
//        List<ProductStockDTO> result = productService.getProductByCategoryCode(categoryCode);
//
//        // then
//        assertEquals(expectedProducts.size(), result.size());
//        assertEquals(expectedProducts, result);
//        verify(productRepository).getProductsWithStockInfoByCategory(categoryCode);
//    }
//
//    @Test
//    void 카테고리별_상품조회_빈결과() {
//        // given
//        String nonExistentCategory = "NONEXISTENT";
//        when(productRepository.getProductsWithStockInfoByCategory(nonExistentCategory))
//                .thenReturn(Collections.emptyList());
//
//        // when
//        List<ProductStockDTO> result = productService.getProductByCategoryCode(nonExistentCategory);
//
//        // then
//        assertTrue(result.isEmpty());
//        verify(productRepository).getProductsWithStockInfoByCategory(nonExistentCategory);
//    }
//
//    @Test
//    void 전체_상품조회_성공() {
//        // given
//        List<ProductStockDTO> expectedProducts = Arrays.asList(
//                createMockProductStockDTO("AP-IP15-PRO", "iPhone 15 Pro", 10),
//                createMockProductStockDTO("SM-S24-ULTRA", "Galaxy S24 Ultra", 15),
//                createMockProductStockDTO("LG-GRAM-17", "LG Gram 17", 6)
//        );
//
//        when(productRepository.getProductsWithStockInfo()).thenReturn(expectedProducts);
//
//        // when
//        List<ProductStockDTO> result = productService.getAllProduct();
//
//        // then
//        assertEquals(expectedProducts.size(), result.size());
//        assertEquals(expectedProducts, result);
//        verify(productRepository).getProductsWithStockInfo();
//    }
//
//    @Test
//    void 전체_상품조회_빈결과() {
//        // given
//        when(productRepository.getProductsWithStockInfo()).thenReturn(Collections.emptyList());
//
//        // when
//        List<ProductStockDTO> result = productService.getAllProduct();
//
//        // then
//        assertTrue(result.isEmpty());
//        verify(productRepository).getProductsWithStockInfo();
//    }
//
//    @Test
//    void SKUID_검증_성공() {
//        // given
//        List<String> validSkuIds = Arrays.asList("SKU001", "SKU002", "SKU003");
//        when(productRepository.countBySkuIdIn(validSkuIds)).thenReturn((long) validSkuIds.size());
//
//        // when
//        productService.validateAllSkuIds(validSkuIds);
//
//        // then
//        verify(productRepository).countBySkuIdIn(validSkuIds);
//    }
//
//    @Test
//    void SKUID_검증_실패() {
//        // given
//        List<String> skuIds = Arrays.asList("SKU001", "SKU002", "INVALID_SKU");
//        when(productRepository.countBySkuIdIn(skuIds)).thenReturn(2L); // 3개 중 2개만 존재
//
//        // when
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                productService.validateAllSkuIds(skuIds));
//
//        // then
//        assertEquals("잘못된 SKU ID가 포함되어 있습니다.", exception.getMessage());
//        verify(productRepository).countBySkuIdIn(skuIds);
//    }
//
//
//
//    private ProductStockDTO createMockProductStockDTO(String skuId, String productName, long ea) {
//        return new ProductStockDTO() {
//            @Override
//            public Long getProductId() {
//                return 1L;
//            }
//
//            @Override
//            public String getSkuId() {
//                return skuId;
//            }
//
//            @Override
//            public String getProductName() {
//                return productName;
//            }
//
//            @Override
//            public String getCategory() {
//                return skuId.substring(0, 2);
//            }
//
//            @Override
//            public Long getUnitPrice() {
//                return 1000L;
//            }
//
//            @Override
//            public Long getStockEa() {
//                return ea;
//            }
//        };
//    }
//}