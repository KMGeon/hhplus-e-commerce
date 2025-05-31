package kr.hhplus.be.server.domain.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.support.EventType;
import kr.hhplus.be.server.domain.support.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponService couponService;
    private final OutboxEventPublisher outboxEventPublisher;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void decreaseCouponQuantity(CouponEvent.Inner.CouponDecreaseEvent event) {
        couponService.decreaseCouponQuantity(event.couponId());
                outboxEventPublisher.publish(
                EventType.COUPON_ISSUE,
                        CouponEvent.Outer.CouponIssueEventPayload.builder()
                                .couponId(event.couponId())
                                .userId(event.userId())
                                .issueTime(LocalDateTime.now())
                                .build()
        );
    }
}
