package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

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
        List<ProductStockDTO> productsList = Arrays.asList(
                new ProductStockDTO(1L, "상품1", "카테고리1", "SKU001", 10000L, 5L),
                new ProductStockDTO(2L, "상품2", "카테고리2", "SKU002", 10000L, 5L)
        );

        ProductInfo.CustomPageImpl<ProductStockDTO> products =
                new ProductInfo.CustomPageImpl<>(
                        new PageImpl<>(productsList, PageRequest.of(0, 10), productsList.size())
                );


        when(productFacadeService.getProducts(null, 0, 10)).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].productId").value(1))
                .andExpect(jsonPath("$.data.content[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data.content[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data.content[1].productId").value(2))
                .andExpect(jsonPath("$.data.content[1].productName").value("상품2"))
                .andExpect(jsonPath("$.data.content[1].skuId").value("SKU002"))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    @DisplayName("카테고리로 상품 목록을 필터링하여 조회한다")
    void getProductsWithCategory() throws Exception {
        // given
        String category = "카테고리1";
        List<ProductStockDTO> productList = Arrays.asList(
                new ProductStockDTO(1L, "상품1", "카테고리1", "SKU001", 10000L, 5L),
                new ProductStockDTO(2L, "상품2", "카테고리1", "SKU002", 10000L, 5L),
                new ProductStockDTO(3L, "상품3", "카테고리1", "SKU003", 10000L, 5L),
                new ProductStockDTO(4L, "상품4", "카테고리1", "SKU004", 10000L, 5L),
                new ProductStockDTO(5L, "상품5", "카테고리1", "SKU005", 10000L, 5L)
        );

        ProductInfo.CustomPageImpl<ProductStockDTO> products =
                new ProductInfo.CustomPageImpl<>(
                        new PageImpl<>(productList, PageRequest.of(0, 10), productList.size())
                );

        when(productFacadeService.getProducts(category, 0, 10)).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/product")
                        .param("category", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(5))
                .andExpect(jsonPath("$.data.content[0].productId").value(1))
                .andExpect(jsonPath("$.data.content[0].category").value(category))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }


    @Test
    @DisplayName("인기 상품 목록을 조회한다")
    void getHotProducts() throws Exception {
        // given
        List<HotProductQuery> hotProducts = Arrays.asList(
                new HotProductQuery("SKU001", CategoryEnum.LG, "상품1", 10L),
                new HotProductQuery("SKU002", CategoryEnum.LG, "상품2", 10L),
                new HotProductQuery("SKU003", CategoryEnum.LG, "상품3", 10L),
                new HotProductQuery("SKU004", CategoryEnum.LG, "상품4", 10L),
                new HotProductQuery("SKU005", CategoryEnum.LG, "상품5", 10L)
        );

        when(productFacadeService.getHotProducts()).thenReturn(hotProducts);

        // when & then
        mockMvc.perform(get("/api/v1/hot-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data[0].orderCount").value(10))
                .andExpect(jsonPath("$.data[1].skuId").value("SKU002"))
                .andExpect(jsonPath("$.data[1].orderCount").value(10))
                .andExpect(jsonPath("$.data[2].skuId").value("SKU003"))
                .andExpect(jsonPath("$.data[2].orderCount").value(10));
    }
}