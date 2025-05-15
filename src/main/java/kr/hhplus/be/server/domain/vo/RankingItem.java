package kr.hhplus.be.server.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class RankingItem {
    private final String skuId;
    private final String productName;
    @Setter
    private Long score;

    private RankingItem(String skuId, String productName) {
        this.skuId = skuId;
        this.productName = productName;
    }

    public static RankingItem create(String skuId, String productName) {
        return new RankingItem(skuId, productName);
    }


    // 테스트 전용
    public static RankingItem createWithScore(String skuId, String productName, Long score) {
        RankingItem item = new RankingItem(skuId, productName);
        item.setScore(score);
        return item;
    }

    public RankingItem merge(RankingItem other) {
        if (!this.skuId.equals(other.skuId)) {
            throw new IllegalArgumentException("SKU ID가 다릅니다.");
        }

        Long mergedScore = (this.score != null ? this.score : 0L) +
                (other.score != null ? other.score : 0L);

        RankingItem merged = new RankingItem(this.skuId, this.productName);
        merged.setScore(mergedScore);
        return merged;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RankingItem that = (RankingItem) o;
        return Objects.equals(skuId, that.skuId) && Objects.equals(productName, that.productName) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId, productName, score);
    }
}