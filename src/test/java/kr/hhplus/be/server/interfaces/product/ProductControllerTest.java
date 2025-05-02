package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductFacadeService productFacadeService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("카테고리 없이 상품 목록을 조회한다")
    void getProductsWithoutCategory() throws Exception {
        // given
        List<ProductStockDTO> products = Arrays.asList(
                createProductStockDTO(1L, "상품1", "카테고리1", "SKU001", 10000L, 5L),
                createProductStockDTO(2L, "상품2", "카테고리2", "SKU002", 20000L, 10L)
        );
        
        when(productFacadeService.getProducts(any())).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data[1].productId").value(2))
                .andExpect(jsonPath("$.data[1].productName").value("상품2"))
                .andExpect(jsonPath("$.data[1].skuId").value("SKU002"));
    }

    @Test
    @DisplayName("카테고리로 상품 목록을 필터링하여 조회한다")
    void getProductsWithCategory() throws Exception {
        // given
        String category = "카테고리1";
        List<ProductStockDTO> products = Arrays.asList(
                createProductStockDTO(1L, "상품1", category, "SKU001", 10000L, 5L)
        );
        
        when(productFacadeService.getProducts(category)).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/product")
                        .param("category", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[0].category").value(category));
    }

    @Test
    @DisplayName("인기 상품 목록을 조회한다")
    void getHotProducts() throws Exception {
        // given
        List<HotProductDTO> hotProducts = Arrays.asList(
                createHotProductDTO("SKU001", 10L),
                createHotProductDTO("SKU002", 8L),
                createHotProductDTO("SKU003", 5L)
        );
        
        when(productFacadeService.getHotProducts()).thenReturn(hotProducts);

        // when & then
        mockMvc.perform(get("/api/v1/hot-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data[0].orderCount").value(10))
                .andExpect(jsonPath("$.data[1].skuId").value("SKU002"))
                .andExpect(jsonPath("$.data[1].orderCount").value(8))
                .andExpect(jsonPath("$.data[2].skuId").value("SKU003"))
                .andExpect(jsonPath("$.data[2].orderCount").value(5));
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

    private HotProductDTO createHotProductDTO(String skuId, Long orderCount) {
        return new HotProductDTO() {
            @Override
            public String getSkuId() {
                return skuId;
            }

            @Override
            public Long getOrderCount() {
                return orderCount;
            }
        };
    }
}