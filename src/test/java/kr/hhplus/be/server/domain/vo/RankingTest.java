package kr.hhplus.be.server.domain.vo;

import kr.hhplus.be.server.domain.order.DatePathProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class RankingTest {

    private RankingPeriod period;
    private String targetPath;
    private List<RankingItem> items;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        period = RankingPeriod.DAILY;
        testDateTime = LocalDateTime.of(2025, 1, 5, 0, 0, 0); // 기준일로부터 4일 후
        targetPath = DatePathProvider.toPath(testDateTime); // "00004"
        items = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 100L),
                RankingItem.createWithScore("SKU002", "상품2", 80L),
                RankingItem.createWithScore("SKU003", "상품3", 60L)
        );
    }

    @Test
    void 랭킹을_생성한다() {
        // when
        Ranking ranking = Ranking.create(period, targetPath, items);

        // then
        assertThat(ranking.getPeriod()).isEqualTo(period);
        assertThat(ranking.getItems()).hasSize(3);
        assertThat(ranking.isEmpty()).isFalse();
        assertThat(ranking.getBaseDate()).isEqualTo(testDateTime);
    }

    @Test
    void 빈_랭킹을_생성한다() {
        // when
        Ranking ranking = Ranking.empty(period, targetPath);

        // then
        assertThat(ranking.isEmpty()).isTrue();
        assertThat(ranking.getItems()).isEmpty();
        assertThat(ranking.getBaseDate()).isEqualTo(testDateTime);
    }

    @Test
    void NULL_아이템_리스트로_생성시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> Ranking.create(period, targetPath, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이템 리스트는 null일 수 없습니다.");
    }

    @Test
    void 상위_N개_아이템을_조회한다() {
        // given
        Ranking ranking = Ranking.create(period, targetPath, items);

        // when
        Ranking top2 = ranking.getTopN(2);

        // then
        assertThat(top2.getItems()).hasSize(2);
        assertThat(top2.getItems().get(0).getSkuId()).isEqualTo("SKU001");
        assertThat(top2.getItems().get(1).getSkuId()).isEqualTo("SKU002");
    }

    @Test
    void _10개_초과_조회시_예외가_발생한다() {
        // given
        Ranking ranking = Ranking.create(period, targetPath, items);

        // when & then
        assertThatThrownBy(() -> ranking.getTopN(11))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("10 이상을 처리할 수 없습니다.");
    }

    @Test
    void 여러_랭킹을_병합한다() {
        // given
        List<RankingItem> items1 = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 50L),
                RankingItem.createWithScore("SKU002", "상품2", 30L)
        );
        List<RankingItem> items2 = Arrays.asList(
                RankingItem.createWithScore("SKU001", "상품1", 30L),
                RankingItem.createWithScore("SKU003", "상품3", 40L)
        );

        Ranking ranking1 = Ranking.create(period, targetPath, items1);
        Ranking ranking2 = Ranking.create(period, targetPath, items2);
        LocalDateTime mergeBaseDate = LocalDateTime.of(2025, 1, 10, 0, 0, 0);

        // when
        Ranking merged = Ranking.merge(period, mergeBaseDate, Arrays.asList(ranking1, ranking2));

        // then
        assertThat(merged.getItems()).hasSize(3);
        assertThat(merged.getItems().get(0).getSkuId()).isEqualTo("SKU001");
        assertThat(merged.getItems().get(0).getScore()).isEqualTo(80L);
        assertThat(merged.getBaseDate()).isEqualTo(mergeBaseDate);
    }

    @Test
    void 빈_랭킹_리스트_병합시_빈_랭킹을_반환한다() {
        // given
        LocalDateTime baseDate = LocalDateTime.of(2025, 1, 10, 0, 0, 0);

        // when
        Ranking merged = Ranking.merge(period, baseDate, null);

        // then
        assertThat(merged.isEmpty()).isTrue();
        assertThat(merged.getBaseDate()).isEqualTo(baseDate);
    }

}