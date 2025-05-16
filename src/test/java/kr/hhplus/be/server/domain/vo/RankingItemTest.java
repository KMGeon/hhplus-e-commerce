package kr.hhplus.be.server.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RankingItemTest {

    @Test
    void 랭킹아이템을_생성한다() {
        // when
        RankingItem item = RankingItem.create("SKU001", "상품1");

        // then
        assertThat(item.getSkuId()).isEqualTo("SKU001");
        assertThat(item.getProductName()).isEqualTo("상품1");
        assertThat(item.getScore()).isNull();
    }

    @Test
    void 스코어와_함께_랭킹아이템을_생성한다() {
        // when
        RankingItem item = RankingItem.createWithScore("SKU001", "상품1", 100L);

        // then
        assertThat(item.getScore()).isEqualTo(100L);
    }

    @Test
    void 동일한_SKU의_랭킹아이템을_병합한다() {
        // given
        RankingItem item1 = RankingItem.createWithScore("SKU001", "상품1", 50L);
        RankingItem item2 = RankingItem.createWithScore("SKU001", "상품1", 30L);

        // when
        RankingItem merged = item1.merge(item2);

        // then
        assertThat(merged.getScore()).isEqualTo(80L);
    }

    @Test
    void 서로_다른_SKU병합시_예외가_발생한다() {
        // given
        RankingItem item1 = RankingItem.create("SKU001", "상품1");
        RankingItem item2 = RankingItem.create("SKU002", "상품2");

        // when & then
        assertThatThrownBy(() -> item1.merge(item2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SKU ID가 다릅니다.");
    }

    @Test
    void NULL_스코어를_0으로_처리하여_병합한다() {
        // given
        RankingItem item1 = RankingItem.create("SKU001", "상품1");
        RankingItem item2 = RankingItem.createWithScore("SKU001", "상품1", 50L);

        // when
        RankingItem merged = item1.merge(item2);

        // then
        assertThat(merged.getScore()).isEqualTo(50L);
    }

    @Test
    void 동등성을_검증한다() {
        // given
        RankingItem item1 = RankingItem.createWithScore("SKU001", "상품1", 100L);
        RankingItem item2 = RankingItem.createWithScore("SKU001", "상품1", 100L);
        RankingItem item3 = RankingItem.createWithScore("SKU001", "상품1", 200L);

        // then
        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);
    }
}