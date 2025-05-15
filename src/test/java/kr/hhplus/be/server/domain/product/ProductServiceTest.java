package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.domain.vo.RankingItem;
import kr.hhplus.be.server.domain.vo.RankingPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    @Test
    void 일별_인기상품_조회_성공() {
        // given
        String period = "DAILY";
        int topNumber = 3;
        LocalDateTime now = LocalDateTime.of(2025, 1, 5, 0, 0, 0);
        String targetPath = DatePathProvider.toPath(now);

        List<RankingItem> items = Arrays.asList(
                RankingItem.createWithScore("SKU001", "인기상품1", 100L),
                RankingItem.createWithScore("SKU002", "인기상품2", 80L),
                RankingItem.createWithScore("SKU003", "인기상품3", 60L),
                RankingItem.createWithScore("SKU004", "인기상품4", 40L)
        );

        Ranking dailyRanking = Ranking.create(RankingPeriod.DAILY, targetPath, items);

        try (MockedStatic<DatePathProvider> datePathProviderMock = mockStatic(DatePathProvider.class)) {
            datePathProviderMock.when(() -> DatePathProvider.toPath(any(LocalDateTime.class))).thenReturn(targetPath);
            when(productRepository.findDailyByPeriod(targetPath)).thenReturn(dailyRanking);

            // when
            Ranking result = productService.getHotProducts(period, topNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPeriod()).isEqualTo(RankingPeriod.DAILY);
            assertThat(result.getItems()).hasSize(3);
            assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");
            assertThat(result.getItems().get(0).getScore()).isEqualTo(100L);
            assertThat(result.getItems().get(2).getSkuId()).isEqualTo("SKU003");

            // 메서드 호출 검증
            verify(productRepository, times(1)).findDailyByPeriod(targetPath);
        }
    }

    @Test
    void 삼일_인기상품_조회_성공() {
        // given
        String period = "THREE_DAYS";
        int topNumber = 2;
        LocalDateTime now = LocalDateTime.of(2025, 1, 5, 0, 0, 0);
        String todayPath = DatePathProvider.toPath(now);
        String yesterdayPath = DatePathProvider.toPath(now.minusDays(1));
        String twoDaysAgoPath = DatePathProvider.toPath(now.minusDays(2));

        // 테스트용 랭킹 아이템 생성
        List<RankingItem> todayItems = Arrays.asList(
                RankingItem.createWithScore("SKU001", "인기상품1", 50L),
                RankingItem.createWithScore("SKU002", "인기상품2", 40L)
        );

        List<RankingItem> yesterdayItems = Arrays.asList(
                RankingItem.createWithScore("SKU001", "인기상품1", 30L),
                RankingItem.createWithScore("SKU003", "인기상품3", 60L)
        );

        List<RankingItem> twoDaysAgoItems = Arrays.asList(
                RankingItem.createWithScore("SKU002", "인기상품2", 20L),
                RankingItem.createWithScore("SKU004", "인기상품4", 10L)
        );

        Ranking todayRanking = Ranking.create(RankingPeriod.DAILY, todayPath, todayItems);
        Ranking yesterdayRanking = Ranking.create(RankingPeriod.DAILY, yesterdayPath, yesterdayItems);
        Ranking twoDaysAgoRanking = Ranking.create(RankingPeriod.DAILY, twoDaysAgoPath, twoDaysAgoItems);

        try (MockedStatic<DatePathProvider> datePathProviderMock = mockStatic(DatePathProvider.class)) {
            datePathProviderMock.when(() -> DatePathProvider.toPath(any(LocalDateTime.class)))
                    .thenReturn(todayPath)
                    .thenReturn(todayPath)
                    .thenReturn(yesterdayPath)
                    .thenReturn(twoDaysAgoPath);

            datePathProviderMock.when(() -> DatePathProvider.toDateTime(todayPath)).thenReturn(now);

            when(productRepository.findDailyByPeriod(todayPath)).thenReturn(todayRanking);
            when(productRepository.findDailyByPeriod(yesterdayPath)).thenReturn(yesterdayRanking);
            when(productRepository.findDailyByPeriod(twoDaysAgoPath)).thenReturn(twoDaysAgoRanking);

            // when
            Ranking result = productService.getHotProducts(period, topNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPeriod()).isEqualTo(RankingPeriod.THREE_DAYS);
            assertThat(result.getItems()).hasSize(2);

            assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");
            assertThat(result.getItems().get(0).getScore()).isEqualTo(80L); // 50+30
            assertThat(result.getItems().get(1).getSkuId()).isEqualTo("SKU003");
            assertThat(result.getItems().get(1).getScore()).isEqualTo(60L);

            verify(productRepository, times(1)).findDailyByPeriod(todayPath);
            verify(productRepository, times(1)).findDailyByPeriod(yesterdayPath);
            verify(productRepository, times(1)).findDailyByPeriod(twoDaysAgoPath);
        }
    }

    @Test
    void 주간_인기상품_조회_성공() {
        // given
        String period = "WEEKLY";
        int topNumber = 3;
        LocalDateTime now = LocalDateTime.of(2025, 1, 7, 0, 0, 0);
        String todayPath = DatePathProvider.toPath(now);

        List<Ranking> dailyRankings = new ArrayList<>();
        List<RankingItem> mergedItems = new ArrayList<>();

        try (MockedStatic<DatePathProvider> datePathProviderMock = mockStatic(DatePathProvider.class)) {
            datePathProviderMock.when(() -> DatePathProvider.toPath(any(LocalDateTime.class))).thenReturn(todayPath);
            datePathProviderMock.when(() -> DatePathProvider.toDateTime(todayPath)).thenReturn(now);

            for (int i = 0; i < 7; i++) {
                LocalDateTime date = now.minusDays(i);
                String path = "0000" + i;

                List<RankingItem> items = Arrays.asList(
                        RankingItem.createWithScore("SKU001", "인기상품1", 10L * (7 - i)),
                        RankingItem.createWithScore("SKU00" + (i + 2), "인기상품" + (i + 2), 5L * (7 - i))
                );

                Ranking dailyRanking = Ranking.create(RankingPeriod.DAILY, path, items);
                dailyRankings.add(dailyRanking);

                if (i == 0) {
                    mergedItems.addAll(items);
                } else {
                    for (RankingItem item : items) {
                        if (item.getSkuId().equals("SKU001")) {
                            for (RankingItem mergedItem : mergedItems) {
                                if (mergedItem.getSkuId().equals("SKU001")) {
                                    mergedItem.setScore(mergedItem.getScore() + item.getScore());
                                    break;
                                }
                            }
                        } else {
                            mergedItems.add(item);
                        }
                    }
                }

                datePathProviderMock.when(() -> DatePathProvider.toPath(date)).thenReturn(path);
                when(productRepository.findDailyByPeriod(path)).thenReturn(dailyRanking);
            }

            // when
            Ranking result = productService.getHotProducts(period, topNumber);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPeriod()).isEqualTo(RankingPeriod.WEEKLY);
            assertThat(result.getItems()).hasSize(3);

            assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");

            for (int i = 0; i < 7; i++) {
                verify(productRepository, times(1)).findDailyByPeriod("0000" + i);
            }
        }
    }
}