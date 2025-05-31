package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.support.Event;
import kr.hhplus.be.server.domain.user.event.UserCouponEvent;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.domain.support.EventType.Topic.COUPON_ISSUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserCouponService userCouponService;

    @KafkaListener(
            topics = COUPON_ISSUE,
            groupId = "coupon-service-group"
    )
    public void handleCouponIssueEvent(String jsonMessage) {
        UserCouponEvent.Outer.UserCouponIssue payload = Event.fromJson(jsonMessage, UserCouponEvent.Outer.UserCouponIssue.class).getPayload();
        Long userCouponId = userCouponService.userCouponPublish(payload.couponId(), payload.userId());
        log.info("userCouponId: {}", userCouponId);
    }
}
