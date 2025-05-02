package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.support.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.domain.support.RedisKeyStore.DECREASE_COUPON_LOCK;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponInfo.CreateInfo save(CouponCommand.Create command){
        CouponEntity saveCoupon = couponRepository.save(CouponEntity.createCoupon(
                command.couponName(),
                command.discountType(),
                command.initQuantity(),
                command.discountAmount(),
                LocalDateTime.now()
        ));
        return CouponInfo.CreateInfo.of(saveCoupon.getId());
    }

    public void decreaseCouponQuantityAfterCheck(long couponId) {
        CouponEntity coupon = couponRepository.findCouponById(couponId);
        coupon.validateForPublish();
        coupon.decreaseQuantity();
    }

    @DistributedLock(key = DECREASE_COUPON_LOCK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseCouponQuantityAfterCheckLock(long couponId) {
        CouponEntity coupon = couponRepository.findCouponById(couponId);
        coupon.validateForPublish();
        coupon.decreaseQuantity();
    }

    public BigDecimal calculateDiscountAmount(Long couponId, BigDecimal totalPrice) {
        return couponRepository.findCouponById(couponId)
                .calculateDiscountAmount(totalPrice);
    }

}
