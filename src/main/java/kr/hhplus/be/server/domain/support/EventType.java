package kr.hhplus.be.server.domain.support;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.payment.event.PaymentEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    COUPON_ISSUE(CouponEvent.Outer.CouponIssueEventPayload.class, Topic.COUPON_ISSUE),
    COUPON_DECREASE(CouponEvent.Outer.CouponDecreaseEvent.class, Topic.COUPON_DECREASE),
    PAYMENT_INTERNAL_API(PaymentEvent.PaymentSendInternalPayload.class, Topic.PAYMENT_INTERNAL_API),
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }

    /**
     * <환경>.<팀명>.<메세지>
     */
    public static class Topic {
        public static final String COUPON_ISSUE = "prod.userTeam.user.coupon";
        public static final String COUPON_DECREASE = "prod.couponTeam.coupon.decrease";
        public static final String PAYMENT_INTERNAL_API = "prod.paymentTeam.paymentInternal";
    }
}
