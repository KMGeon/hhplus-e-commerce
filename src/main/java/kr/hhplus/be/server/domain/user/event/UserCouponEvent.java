package kr.hhplus.be.server.domain.user.event;

import kr.hhplus.be.server.domain.support.EventPayload;

import java.time.LocalDateTime;

public class UserCouponEvent {

    public static class Inner {

    }

    public static class Outer {
        public record UserCouponIssue(Long userId, Long couponId, LocalDateTime issueTime) implements EventPayload {

        }
    }

}
