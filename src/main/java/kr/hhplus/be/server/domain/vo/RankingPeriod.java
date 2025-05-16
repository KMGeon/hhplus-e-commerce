package kr.hhplus.be.server.domain.vo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum RankingPeriod {
    DAILY(1),
    THREE_DAYS(3),
    WEEKLY(7),
    MONTHLY(30);

    private final int days;

    RankingPeriod(int days) {
        this.days = days;
    }

    public static RankingPeriod matching(String period) {
        try {
            return RankingPeriod.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("RankingPeriod error : {}", e.getMessage(), e);
            throw new IllegalArgumentException("적절하지 않은 기간입니다: " + period);
        }
    }
}