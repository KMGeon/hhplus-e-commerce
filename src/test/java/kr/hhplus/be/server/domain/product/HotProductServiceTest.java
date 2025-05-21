package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.domain.vo.RankingItem;
import kr.hhplus.be.server.domain.vo.RankingPeriod;
import kr.hhplus.be.server.infrastructure.order.OrderCacheRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HotProductServiceTest extends ApplicationContext {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderCacheRepository orderCacheRepository;

    private LocalDateTime now;
    private String todayPath;
    private String yesterdayPath;
    private String twoDaysAgoPath;
    private String threeDaysAgoPath;
    private String fourDaysAgoPath;
    private String fiveDaysAgoPath;
    private String sixDaysAgoPath;

    @BeforeEach
    void setUp() {
        redisTemplateRepository.flushAll();
        now = LocalDateTime.now();

        // 각 날짜별 path 생성
        todayPath = DatePathProvider.toPath(now);
        yesterdayPath = DatePathProvider.toPath(now.minusDays(1));
        twoDaysAgoPath = DatePathProvider.toPath(now.minusDays(2));
        threeDaysAgoPath = DatePathProvider.toPath(now.minusDays(3));
        fourDaysAgoPath = DatePathProvider.toPath(now.minusDays(4));
        fiveDaysAgoPath = DatePathProvider.toPath(now.minusDays(5));
        sixDaysAgoPath = DatePathProvider.toPath(now.minusDays(6));

        // Redis에 테스트 데이터 준비
        setupTestData();
    }

    @AfterEach
    void tearDown() {
        redisTemplateRepository.flushAll();
    }

    private void setupTestData() {
        // 오늘의 인기 상품 데이터
        orderCacheRepository.addDailySummeryRanking(todayPath, RankingItem.create("SKU001", "인기상품1"), 100L);
        orderCacheRepository.addDailySummeryRanking(todayPath, RankingItem.create("SKU002", "인기상품2"), 80L);
        orderCacheRepository.addDailySummeryRanking(todayPath, RankingItem.create("SKU003", "인기상품3"), 60L);
        orderCacheRepository.addDailySummeryRanking(todayPath, RankingItem.create("SKU004", "인기상품4"), 40L);
        orderCacheRepository.addDailySummeryRanking(todayPath, RankingItem.create("SKU005", "인기상품5"), 20L);

        // 어제의 인기 상품 데이터
        orderCacheRepository.addDailySummeryRanking(yesterdayPath, RankingItem.create("SKU001", "인기상품1"), 90L);
        orderCacheRepository.addDailySummeryRanking(yesterdayPath, RankingItem.create("SKU003", "인기상품3"), 70L);
        orderCacheRepository.addDailySummeryRanking(yesterdayPath, RankingItem.create("SKU006", "인기상품6"), 50L);

        // 2일 전 인기 상품 데이터
        orderCacheRepository.addDailySummeryRanking(twoDaysAgoPath, RankingItem.create("SKU002", "인기상품2"), 85L);
        orderCacheRepository.addDailySummeryRanking(twoDaysAgoPath, RankingItem.create("SKU004", "인기상품4"), 65L);
        orderCacheRepository.addDailySummeryRanking(twoDaysAgoPath, RankingItem.create("SKU007", "인기상품7"), 45L);

        // 3일 전 ~ 6일 전 인기 상품 데이터 (주간 테스트용)
        orderCacheRepository.addDailySummeryRanking(threeDaysAgoPath, RankingItem.create("SKU001", "인기상품1"), 75L);
        orderCacheRepository.addDailySummeryRanking(threeDaysAgoPath, RankingItem.create("SKU008", "인기상품8"), 55L);

        orderCacheRepository.addDailySummeryRanking(fourDaysAgoPath, RankingItem.create("SKU002", "인기상품2"), 95L);
        orderCacheRepository.addDailySummeryRanking(fourDaysAgoPath, RankingItem.create("SKU009", "인기상품9"), 35L);

        orderCacheRepository.addDailySummeryRanking(fiveDaysAgoPath, RankingItem.create("SKU003", "인기상품3"), 85L);
        orderCacheRepository.addDailySummeryRanking(fiveDaysAgoPath, RankingItem.create("SKU010", "인기상품10"), 25L);

        orderCacheRepository.addDailySummeryRanking(sixDaysAgoPath, RankingItem.create("SKU001", "인기상품1"), 95L);
        orderCacheRepository.addDailySummeryRanking(sixDaysAgoPath, RankingItem.create("SKU011", "인기상품11"), 15L);
    }

    @Test
    void 일별_인기상품_조회_성공() {
        // given
        String period = "DAILY";
        int topNumber = 3;

        // when
        Ranking result = productService.getHotProducts(period, topNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPeriod()).isEqualTo(RankingPeriod.DAILY);
        assertThat(result.getItems()).hasSize(3);

        // 점수 내림차순 정렬 확인
        assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");
        assertThat(result.getItems().get(0).getScore()).isEqualTo(100L);

        assertThat(result.getItems().get(1).getSkuId()).isEqualTo("SKU002");
        assertThat(result.getItems().get(1).getScore()).isEqualTo(80L);

        assertThat(result.getItems().get(2).getSkuId()).isEqualTo("SKU003");
        assertThat(result.getItems().get(2).getScore()).isEqualTo(60L);
    }

    @Test
    void 삼일_인기상품_조회_성공() {
        // given
        String period = "THREE_DAYS";
        int topNumber = 4;

        // when
        Ranking result = productService.getHotProducts(period, topNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPeriod()).isEqualTo(RankingPeriod.THREE_DAYS);
        assertThat(result.getItems()).hasSize(4);

        // 3일간 병합된 결과 확인
        // SKU001: 100(오늘) + 90(어제) = 190
        // SKU002: 80(오늘) + 85(2일전) = 165
        // SKU003: 60(오늘) + 70(어제) = 130
        // SKU004: 40(오늘) + 65(2일전) = 105

        assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");
        assertThat(result.getItems().get(0).getScore()).isEqualTo(190L);

        assertThat(result.getItems().get(1).getSkuId()).isEqualTo("SKU002");
        assertThat(result.getItems().get(1).getScore()).isEqualTo(165L);

        assertThat(result.getItems().get(2).getSkuId()).isEqualTo("SKU003");
        assertThat(result.getItems().get(2).getScore()).isEqualTo(130L);

        assertThat(result.getItems().get(3).getSkuId()).isEqualTo("SKU004");
        assertThat(result.getItems().get(3).getScore()).isEqualTo(105L);
    }

    @Test
    void 주간_인기상품_조회_성공() {
        // given
        String period = "WEEKLY";
        int topNumber = 5;

        // when
        Ranking result = productService.getHotProducts(period, topNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPeriod()).isEqualTo(RankingPeriod.WEEKLY);
        assertThat(result.getItems()).hasSize(5);

        // 7일간 병합된 결과 확인
        // SKU001: 100(오늘) + 90(어제) + 75(3일전) + 95(6일전) = 360
        // SKU002: 80(오늘) + 85(2일전) + 95(4일전) = 260
        // SKU003: 60(오늘) + 70(어제) + 85(5일전) = 215
        // SKU004: 40(오늘) + 65(2일전) = 105

        assertThat(result.getItems().get(0).getSkuId()).isEqualTo("SKU001");
        assertThat(result.getItems().get(0).getScore()).isEqualTo(360L);

        assertThat(result.getItems().get(1).getSkuId()).isEqualTo("SKU002");
        assertThat(result.getItems().get(1).getScore()).isEqualTo(260L);

        assertThat(result.getItems().get(2).getSkuId()).isEqualTo("SKU003");
        assertThat(result.getItems().get(2).getScore()).isEqualTo(215L);

        assertThat(result.getItems().get(3).getSkuId()).isEqualTo("SKU004");
    }
}