package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.support.DistributedLock;
import kr.hhplus.be.server.domain.support.EventType;
import kr.hhplus.be.server.domain.support.OutboxEventPublisher;
import kr.hhplus.be.server.support.CacheKeyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponEventPublisher couponEventPublisher;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public CouponInfo.CreateInfo save(CouponCommand.Create command) {
        CouponEntity coupon = CouponEntity.createCoupon(
                command.couponName(),
                command.discountType(),
                command.initQuantity(),
                command.discountAmount(),
                LocalDateTime.now()
        );
        CouponEntity saveCoupon = couponRepository.save(coupon);
        couponRepository.initializeCoupon(saveCoupon.getId(), command.initQuantity());
        return CouponInfo.CreateInfo.of(saveCoupon.getId());
    }

    /**
     * @see CouponEventPublisher
     */
    @Transactional
    public Long publishCoupon(CouponCommand.Publish command) {
        Long couponId = command.couponId();
        Long useId = command.userId();
        couponRepository.issueCoupon(couponId, useId);

        outboxEventPublisher.publish(
                EventType.COUPON_ISSUE,
                CouponEvent.Outer.CouponIssueEventPayload.builder()
                        .couponId(command.couponId())
                        .userId(command.userId())
                        .issueTime(LocalDateTime.now())
                        .build()
        );

        couponEventPublisher.publishCouponToDecrease(CouponEvent.Inner.CouponDecreaseEvent.from(couponId, command.userId()));
        return command.couponId();
    }

    @Transactional
    @DistributedLock(key = CacheKeyManager.RedisLockKey.test)
    public void decreaseCouponQuantity(Long couponId) {
        CouponEntity coupon = couponRepository.findCouponById(couponId);
        coupon.decreaseRemainQuantity();
        couponRepository.save(coupon);
    }

    public BigDecimal calculateDiscountAmount(Long couponId, BigDecimal totalPrice) {
        return couponRepository.findCouponById(couponId)
                .calculateDiscountAmount(totalPrice);
    }

}
