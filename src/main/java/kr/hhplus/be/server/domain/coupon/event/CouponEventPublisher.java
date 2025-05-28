package kr.hhplus.be.server.domain.coupon.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * @see kr.hhplus.be.server.domain.coupon.CouponService
     */
    public void publishCouponToDecrease(CouponEvent.Inner.CouponDecreaseEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
