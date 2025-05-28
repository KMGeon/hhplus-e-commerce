package kr.hhplus.be.server.domain.coupon.event;

import kr.hhplus.be.server.domain.support.EventPayload;
import lombok.Builder;

import java.time.LocalDateTime;

public class CouponEvent {
    public static class Inner {
        public record CouponDecreaseEvent(Long couponId, Long userId) {
            public static CouponDecreaseEvent from(Long couponId, Long userId) {
                return new CouponDecreaseEvent(couponId, userId);
            }
        }
    }

    public static class Outer {
        @Builder
        public record CouponIssueEventPayload(Long couponId, Long userId, LocalDateTime issueTime) implements EventPayload {
        }
    }
}
