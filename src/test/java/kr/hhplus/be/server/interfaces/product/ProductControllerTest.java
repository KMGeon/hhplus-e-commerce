package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.domain.vo.RankingItem;
import kr.hhplus.be.server.domain.vo.RankingPeriod;
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

    @Mock
    private ProductService productService;

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
    void 카테고리로_상품_목록을_필터링하여_조회한다() throws Exception {
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
    void 인기_상품_목록을_기본값() throws Exception {
        // given
        String period = "DAILY";
        int topNumber = 5;

        List<RankingItem> items = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 100L),
                RankingItem.createWithScore("SKU002", "상품2", 90L),
                RankingItem.createWithScore("SKU003", "상품3", 80L),
                RankingItem.createWithScore("SKU004", "상품4", 70L),
                RankingItem.createWithScore("SKU005", "상품5", 60L)
        );

        Ranking mockRanking = Ranking.create(RankingPeriod.DAILY, "00001", items);

        when(productService.getHotProducts(period, topNumber)).thenReturn(mockRanking);

        // when
        // then
        mockMvc.perform(get("/api/v1/hot-product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.period").value("DAILY"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(5))
                .andExpect(jsonPath("$.data.items[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data.items[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data.items[0].score").value(100))
                .andExpect(jsonPath("$.data.items[1].skuId").value("SKU002"))
                .andExpect(jsonPath("$.data.items[1].productName").value("상품2"))
                .andExpect(jsonPath("$.data.items[1].score").value(90))
                .andExpect(jsonPath("$.data.empty").value(false));
    }

    @Test
    void 주간_인기_상품상위_10개를_조회한다() throws Exception {
        // given
        String period = "WEEKLY";
        int topNumber = 10;

        List<RankingItem> items = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 1000L),
                RankingItem.createWithScore("SKU002", "상품2", 900L),
                RankingItem.createWithScore("SKU003", "상품3", 800L)
                // ... 더 많은 아이템들
        );

        Ranking mockRanking = Ranking.create(RankingPeriod.WEEKLY, "00001", items);

        when(productService.getHotProducts(period, topNumber)).thenReturn(mockRanking);

        // when
        // then
        mockMvc.perform(get("/api/v1/hot-product")
                        .param("period", period)
                        .param("topNumber", String.valueOf(topNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.period").value("WEEKLY"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].skuId").value("SKU001"))
                .andExpect(jsonPath("$.data.items[0].score").value(1000));
    }

    @Test
    void _3일간_인기_상품을_조회한다() throws Exception {
        // given
        String period = "THREE_DAYS";
        int topNumber = 5;

        List<RankingItem> items = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 300L),
                RankingItem.createWithScore("SKU002", "상품2", 250L),
                RankingItem.createWithScore("SKU003", "상품3", 200L),
                RankingItem.createWithScore("SKU004", "상품4", 150L),
                RankingItem.createWithScore("SKU005", "상품5", 100L)
        );

        Ranking mockRanking = Ranking.create(RankingPeriod.THREE_DAYS, "00001", items);

        when(productService.getHotProducts(period, topNumber)).thenReturn(mockRanking);

        // when
        // then
        mockMvc.perform(get("/api/v1/hot-product")
                        .param("period", period)
                        .param("topNumber", String.valueOf(topNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.period").value("THREE_DAYS"))
                .andExpect(jsonPath("$.data.items.length()").value(5));
    }
}