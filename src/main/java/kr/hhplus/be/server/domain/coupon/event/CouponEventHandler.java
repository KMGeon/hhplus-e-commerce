package kr.hhplus.be.server.domain.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void decreaseCouponQuantity(CouponEvent.Inner.CouponDecreaseEvent event) {
        couponService.decreaseCouponQuantity(event.couponId());
    }
}
