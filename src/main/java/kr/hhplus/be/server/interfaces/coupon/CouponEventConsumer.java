package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.support.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.domain.support.EventType.Topic.COUPON_DECREASE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventConsumer {

    private final CouponService couponService;

    @KafkaListener(
            topics = COUPON_DECREASE,
            groupId = "coupon-quantity-decrease-service"
    )
    public void handleCouponIssueEvent(String jsonMessage) {
        CouponEvent.Outer.CouponDecreaseEvent payload = Event.fromJson(jsonMessage, CouponEvent.Outer.CouponDecreaseEvent.class).getPayload();
        couponService.decreaseCouponQuantity(payload.couponId());
    }
}
