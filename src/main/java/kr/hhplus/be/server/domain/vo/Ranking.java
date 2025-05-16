package kr.hhplus.be.server.domain.vo;

import kr.hhplus.be.server.domain.order.DatePathProvider;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString
public class Ranking {
    private final RankingPeriod period;
    private final LocalDateTime baseDate;
    private final List<RankingItem> items;

    private Ranking(RankingPeriod period, LocalDateTime baseDate, List<RankingItem> items) {
        this.period = period;
        this.baseDate = baseDate;
        this.items = Collections.unmodifiableList(items);
    }

    public static Ranking create(RankingPeriod period, String targetPath, List<RankingItem> items) {
        validateItems(items);
        return new Ranking(period, DatePathProvider.toDateTime(targetPath), items);
    }

    public static Ranking empty(RankingPeriod period, String targetPath) {
        return new Ranking(period, DatePathProvider.toDateTime(targetPath), Collections.emptyList());
    }

    public Ranking getTopN(int n) {
        if (n > 10) throw new RuntimeException("10 이상을 처리할 수 없습니다.");

        return new Ranking(this.period, this.baseDate, items.stream()
                .limit(n)
                .collect(Collectors.toList()));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }


    public static Ranking merge(RankingPeriod period, LocalDateTime baseDate,
                                        List<Ranking> rankings) {
        if (rankings == null || rankings.isEmpty()) {
            return empty(period, DatePathProvider.toPath(baseDate));
        }

        Map<String, RankingItem> mergedMap = new HashMap<>();

        for (Ranking ranking : rankings) {
            for (RankingItem item : ranking.getItems()) {
                mergedMap.merge(item.getSkuId(), item, RankingItem::merge);
            }
        }

        List<RankingItem> mergedItems = new ArrayList<>(mergedMap.values());
        mergedItems.sort((a, b) -> {
            Long scoreA = a.getScore() != null ? a.getScore() : 0L;
            Long scoreB = b.getScore() != null ? b.getScore() : 0L;
            return scoreB.compareTo(scoreA);
        });

        return new Ranking(period, baseDate, mergedItems);
    }

    private static void validateItems(List<RankingItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("아이템 리스트는 null일 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ranking ranking = (Ranking) o;
        return period == ranking.period && Objects.equals(baseDate, ranking.baseDate) && Objects.equals(items, ranking.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, baseDate, items);
    }
}